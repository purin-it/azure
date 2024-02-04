package com.example.batch;

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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
	
	/** データ書き込み処理 */
	private final DemoItemWriter demoItemWriter;
	
	/** チャンクサイズ */
	@Value("${chunk.size}")
	private String chunkSize;
	
	/**
	 * MyBatisPagingItemReaderを使ってUSER_DATAデータをページを分けて取得する.
	 * @return 読み込みオブジェクト
	 */
	@Bean
	public MyBatisPagingItemReader<UserData> reader(){
		return new MyBatisPagingItemReaderBuilder<UserData>()
				.sqlSessionFactory(sqlSessionFactory)
				.queryId("com.example.mybatis.UserDataMapper.findAll")
				.pageSize(Integer.parseInt(chunkSize))
				.build();
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
				.<UserData, UserData>chunk(Integer.parseInt(chunkSize))
				.reader(reader)
				.writer(demoItemWriter)
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
