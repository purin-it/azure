package com.example.batch;

import java.io.IOException;
import java.io.Writer;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisPagingItemReader;
import org.mybatis.spring.batch.builder.MyBatisPagingItemReaderBuilder;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileHeaderCallback;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import com.example.mybatis.model.UserData;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class DemoChunkConfig {

	/** ジョブ生成ファクトリ */
	public final JobBuilderFactory jobBuilderFactory;

	/** ステップ生成ファクトリ */
	public final StepBuilderFactory stepBuilderFactory;

	/** SQLセッションファクトリ */
	public final SqlSessionFactory sqlSessionFactory;
	
	/** ステップの前後処理 */
	private final DemoStepListener demoStepListener;
	
	/** 取得したUSER_DATAテーブルのデータを出力するファイルパス */
	private static final String TMP_FILE_PATH = "/home/";
	
	/** 取得したUSER_DATAテーブルのデータを出力するファイル名 */
	private static final String TMP_FILE_NAME = "tmp_user_data.csv";
	
	/** 改行コード */
	private static final String LINE_SEPARATOR = "\r\n";
	
	/** 文字コード */
	private static final String CHARCTER_CODE = "UTF-8";
	
	/** チャンクサイズ */
	private final int CHUNK_SIZE = 3;
	
	/**
	 * MyBatisPagingItemReaderを使ってUSER_DATAデータをページを分けて取得する.
	 * @return 読み込みオブジェクト
	 */
	@Bean
	public MyBatisPagingItemReader<UserData> reader(){
		return new MyBatisPagingItemReaderBuilder<UserData>()
				.sqlSessionFactory(sqlSessionFactory)
				.queryId("com.example.mybatis.UserDataMapper.findAll")
				.pageSize(CHUNK_SIZE)
				.build();
	}
	
	/**
	 * 読み込んだDBのデータを、ローカルのファイルに書き込む.
	 * @return 書き込みオブジェクト
	 */
	@Bean
	public FlatFileItemWriter<UserData> writer(){
		FlatFileItemWriter<UserData> writer = new FlatFileItemWriter<UserData>();
		// ファイルの出力先を指定
		writer.setResource(new FileSystemResource(TMP_FILE_PATH + TMP_FILE_NAME));
		// ファイルを書き込む際の文字コード・改行コードを指定
		writer.setEncoding(CHARCTER_CODE);
		writer.setLineSeparator(LINE_SEPARATOR);
		// ファイルを追記書き込みNGとする
		writer.setAppendAllowed(false);
		// ファイルに書き込む
		// ヘッダー行を定義する
		writer.setHeaderCallback(new FlatFileHeaderCallback() {
            public void writeHeader(Writer ioWriter) throws IOException {
            	ioWriter.append("id,name,birth_year,birth_month,birth_day,sex,memo");
            }
        });
		// 書き込む文字列は、DemoLineAggregatorクラスで設定
		writer.setLineAggregator(new DemoLineAggregator());
		return writer;
	}

	/**
	 * Spring Batchのジョブ内で指定する処理単位(ステップ)を定義する.
	 * @param reader 読み込みオブジェクト
	 * @param writer 書き込みオブジェクト
	 * @return Spring Batchのジョブ内で指定する処理単位
	 */
	@Bean
	public Step step(MyBatisPagingItemReader<UserData> reader, ItemWriter<UserData> writer) {
		// 生成するステップ内で読み込み/書き込みを一連の流れを指定する
		// その際、読み込みデータの加工は行わず、
		// チャンクサイズ(=何件読み込む毎にコミットするか)を指定している
		return stepBuilderFactory.get("step")
				.<UserData, UserData>chunk(CHUNK_SIZE)
				.reader(reader)
				.writer(writer)
				.listener(demoStepListener)
				.build();
	}

	/**
	 * Spring Batchのジョブを定義する.
	 * @param jobListener 実行前後の処理(リスナ)
	 * @param step        実行するステップ
	 * @return Spring Batchのジョブ
	 */
	@Bean
	public Job updateUserData(DemoJobListener jobListener, Step step) {
		// 生成するジョブ内で、実行前後の処理(リスナ)と処理単位(ステップ)を指定する
		return jobBuilderFactory
				.get("selectUserData")
				.incrementer(new RunIdIncrementer())
				.listener(jobListener)
				.flow(step)
				.end()
				.build();
	}
}
