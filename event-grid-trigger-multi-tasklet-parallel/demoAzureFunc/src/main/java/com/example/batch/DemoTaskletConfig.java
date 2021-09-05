package com.example.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.support.SimpleFlow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

@Configuration
@EnableBatchProcessing
public class DemoTaskletConfig {

	/** Spring Batchのジョブ内で指定する実行処理を定義するクラス */
	@Autowired
	private DemoTasklet demoTasklet;
	
	/** Spring Batchのジョブ内で指定するログ出力処理を定義するクラス */
	@Autowired
	private DemoTaskletLog demoTaskletLog;
	
	/** Spring Batchのジョブ内で指定するログ出力処理を定義するクラス(2) */
	@Autowired
	private DemoTaskletLog2 demoTaskletLog2;
	
	/** Spring Batchのジョブ内で指定するログ出力処理を定義するクラス(3) */
	@Autowired
	private DemoTaskletLog3 demoTaskletLog3;
	
	/** Spring Batchのジョブ内で指定するログ出力処理を定義するクラス(4) */
	@Autowired
	private DemoTaskletLog4 demoTaskletLog4;

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
	 * Spring Batchのジョブ内で指定するログ出力処理(ステップ)(2)を定義する.
	 * @return Spring Batchのジョブ内で指定するログ出力処理
	 */
	@Bean
	public Step stepLog2() {
		// 生成するステップ内でログ出力する処理(Tasklet)を指定する
		return stepBuilderFactory
				.get("stepLog2")
				.tasklet(demoTaskletLog2)
				.build();
	}
	
	/**
	 * Spring Batchのジョブ内で指定するログ出力処理(ステップ)(3)を定義する.
	 * @return Spring Batchのジョブ内で指定するログ出力処理
	 */
	@Bean
	public Step stepLog3() {
		// 生成するステップ内でログ出力する処理(Tasklet)を指定する
		return stepBuilderFactory
				.get("stepLog3")
				.tasklet(demoTaskletLog3)
				.build();
	}
	
	/**
	 * Spring Batchのジョブ内で指定するログ出力処理(ステップ)(4)を定義する.
	 * @return Spring Batchのジョブ内で指定するログ出力処理
	 */
	@Bean
	public Step stepLog4() {
		// 生成するステップ内でログ出力する処理(Tasklet)を指定する
		return stepBuilderFactory
				.get("stepLog4")
				.tasklet(demoTaskletLog4)
				.build();
	}
	
	/**
	 * 並列にステップを実行するフロー1(ログ出力処理1, ログ出力処理2)を定義する.
	 * @param stepLog ログ出力処理1
	 * @param stepLog2 ログ出力処理2
	 * @return 並列にステップを実行するフロー1
	 */
	@Bean
	public Flow flow1(Step stepLog, Step stepLog2) {
	    return new FlowBuilder<SimpleFlow>("flow1")
	        .start(stepLog)
	        .next(stepLog2)
	        .build();
	}

	/**
	 * 並列にステップを実行するフロー2(ログ出力処理3, CSVファイル取込処理)を定義する.
	 * @param stepLog3 ログ出力処理3
	 * @param step CSVファイル取込処理
	 * @return 並列にステップを実行するフロー2
	 */
	@Bean
	public Flow flow2(Step stepLog3, Step step) {
	    return new FlowBuilder<SimpleFlow>("flow2")
	        .start(stepLog3)
	        .next(step)
	        .build();
	}
	
	/**
	 * Spring Batchのジョブを定義する.
	 * @param flow1 並列にステップを実行するフロー1
	 * @param flow2 並列にステップを実行するフロー2
	 * @param stepLog4 ログ出力処理4
	 * @return Spring Batchのジョブ
	 */
	@Bean
	public Job job(Flow flow1, Flow flow2, Step stepLog4) {
		// 生成するジョブ内で、実行前後の処理(リスナ)と処理単位(ステップ)を指定する
		// flow1とflow2を並列に実行した後で、ログ出力処理4を実行する
		return jobBuilderFactory
				.get("job")
				.incrementer(new RunIdIncrementer())
				.listener(listener())
				.start(flow1)
				.split(new SimpleAsyncTaskExecutor())
				.add(flow2)
				.next(stepLog4)
				.end()
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
