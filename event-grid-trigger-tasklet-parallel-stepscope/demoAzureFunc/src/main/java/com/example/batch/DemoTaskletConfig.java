package com.example.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBatchProcessing
public class DemoTaskletConfig {

	/** Spring Batchのジョブ内で指定する実行処理を定義するクラス */
	@Autowired
	private DemoTasklet demoTasklet;
	
	/** Spring Batchのジョブ内で指定するログ出力処理を定義するクラス */
	@Autowired
	private DemoTaskletLog demoTaskletLog;

	/** Spring Batchのジョブを生成するクラス */
	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	/** Spring Batchのステップを生成するクラス */
	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	/**
	 * Spring Batchのジョブ内で指定するCSVファイル取込処理(ステップ)を定義する.
	 * @return Spring Batchのジョブ内で指定するCSVファイル取込処理
	 */
	@Bean
	public Step step() {
		// 生成するステップ内でCSVファイル取込処理(Tasklet)を指定する
		return stepBuilderFactory
				.get("step")
				.tasklet(demoTasklet)
				.build();
	}
	
	/**
	 * Spring Batchのジョブ内で指定するログ出力処理(ステップ)を定義する.
	 * @return Spring Batchのジョブ内で指定するログ出力処理
	 */
	@Bean
	public Step stepLog() {
		// 生成するステップ内でログ出力する処理(Tasklet)を指定する
		return stepBuilderFactory
				.get("stepLog")
				.tasklet(demoTaskletLog)
				.build();
	}

	/**
	 * Spring Batchのジョブを定義する.
	 * @param step CSVファイル取込処理
	 * @param stepLog ログ出力処理
	 * @return Spring Batchのジョブ
	 */
	@Bean
	public Job job(Step step, Step stepLog) {
		// 生成するジョブ内で、実行前後の処理(リスナ)と処理単位(ステップ)を指定する
		return jobBuilderFactory
				.get("job")
				.incrementer(new RunIdIncrementer())
				.listener(listener())
				.start(step)
				.next(stepLog)
				.build();
	}

	/**
	 * Spring Batchのジョブの実行前後の処理を定義する.
	 * @return Spring Batchのジョブの実行前後の処理
	 */
	@Bean
	public DemoJobListener listener() {
		return new DemoJobListener();
	}
}
