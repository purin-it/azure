package com.example.batch;

import java.net.MalformedURLException;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisBatchItemWriter;
import org.mybatis.spring.batch.builder.MyBatisBatchItemWriterBuilder;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.UrlResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.example.mybatis.model.UserData;
import com.example.util.DemoStringUtil;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class DemoChunkConfig {

	public final JobBuilderFactory jobBuilderFactory;

	public final StepBuilderFactory stepBuilderFactory;

	public final SqlSessionFactory sqlSessionFactory;

	private final DemoUpdateProcessor demoUpdateProcessor;

	/** Azure Storageのアカウント名 */
	@Value("${azure.storage.accountName}")
	private String storageAccountName;

	/** Azure Storageへのアクセスキー */
	@Value("${azure.storage.accessKey}")
	private String storageAccessKey;

	/** Azure StorageのBlobコンテナー名 */
	@Value("${azure.storage.containerName}")
	private String storageContainerName;

	/**
	 * BlobStorageからファイルを読み込む.
	 * @return 読み込みオブジェクト
	 */
	@Bean
	public FlatFileItemReader<UserData> reader() {
		FlatFileItemReader<UserData> reader = new FlatFileItemReader<UserData>();
		try {
			// Blobストレージへの接続文字列
			String storageConnectionString = "DefaultEndpointsProtocol=https;" 
					+ "AccountName=" + storageAccountName
					+ ";" + "AccountKey=" + storageAccessKey + ";";
			// Blobサービスクライアントの生成
			BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
					.connectionString(storageConnectionString).buildClient();
			// BlobサービスクライアントからSASを生成
			String sas = DemoStringUtil.getServiceSasUriForBlob(blobServiceClient);
			// BlobStorageからファイル(user_data.csv)を読み込む際のURLをreaderに設定
			String url = blobServiceClient.getAccountUrl() + "/" 
					+ storageContainerName + "/user_data.csv" 
					+ "?" + sas;
			reader.setResource(new UrlResource(url));
		} catch (MalformedURLException ex) {
			throw new RuntimeException(ex);
		}

		// ファイルを読み込む際の文字コード
		reader.setEncoding("UTF-8");
		// 1行目を読み飛ばす
		reader.setLinesToSkip(1);
		// ファイルから読み込んだデータをUserDataオブジェクトに格納
		reader.setLineMapper(new DefaultLineMapper<UserData>() {
			{
				setLineTokenizer(new DelimitedLineTokenizer() {
					{
						setNames(new String[] { "id", "name", "birth_year"
								, "birth_month", "birth_day", "sex", "memo" });
					}
				});
				setFieldSetMapper(new BeanWrapperFieldSetMapper<UserData>() {
					{
						setTargetType(UserData.class);
					}
				});
			}
		});
		return reader;
	}

	/**
	 * 読み込んだファイルのデータを、DBに書き込む.
	 * @param dataSource データソース
	 * @return 書き込みオブジェクト
	 */
	@Bean
	public MyBatisBatchItemWriter<UserData> writer(DataSource dataSource) {
		return new MyBatisBatchItemWriterBuilder<UserData>()
				.sqlSessionFactory(sqlSessionFactory)
				.statementId("com.example.mybatis.UserDataMapper.upsert")
				.build();
	}
	
	/**
	 * Spring Batchのジョブを並列実行するためのTaskExecutorを定義する.
	 * @return TaskExecutorオブジェクト
	 */
	@Bean
	public TaskExecutor taskExecutor() {
		return new SimpleAsyncTaskExecutor("step-thread");
	}

	/**
	 * Spring Batchのジョブ内で指定する処理単位(ステップ)を定義する.
	 * @param reader 読み込みオブジェクト
	 * @param writer 書き込みオブジェクト
	 * @param taskExecutor TaskExecutorオブジェクト
	 * @return Spring Batchのジョブ内で指定する処理単位
	 */
	@Bean
	public Step step(ItemReader<UserData> reader, ItemWriter<UserData> writer, TaskExecutor taskExecutor) {
		// 生成するステップ内で読み込み/加工/書き込みを一連の流れを指定する
		// その際のチャンクサイズ(=何件読み込む毎にコミットするか)を5に指定している
		// また、TaskExecutorによって並列実行できるようにし、
		// その際のスロットル制限(=並列可能なスレッド数)を8に指定している
		return stepBuilderFactory.get("step")
				.<UserData, UserData>chunk(5)
				.reader(reader)
				.processor(demoUpdateProcessor)
				.writer(writer)
				.taskExecutor(taskExecutor)
				.throttleLimit(8)
				.build();
	}

	/**
	 * Spring Batchのジョブを定義する.
	 * @param jobListener 実行前後の処理(リスナ)
	 * @param step 実行するステップ
	 * @return Spring Batchのジョブ
	 */
	@Bean
	public Job updateUserData(DemoJobListener jobListener, Step step) {
		// 生成するジョブ内で、実行前後の処理(リスナ)と処理単位(ステップ)を指定する
		return jobBuilderFactory
				.get("updateUserData")
				.incrementer(new RunIdIncrementer())
				.listener(jobListener)
				.flow(step)
				.end()
				.build();
	}
}
