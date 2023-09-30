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
	
	/** Spring Batchのジョブを生成するクラス */
	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	/** Spring Batchのステップを生成するクラス */
	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	/**
	 * Spring Batchのジョブ内で指定する処理単位(ステップ)を定義する.
	 * @return Spring Batchのジョブ内で指定する処理単位
	 */
	@Bean
	public Step step() {
		// 生成するステップ内で実行処理(Tasklet)を指定する
		return stepBuilderFactory.get("step")
				.tasklet(demoTasklet)
				.build();
	}
	
	/**
	 * Spring Batchのジョブを定義する.
	 * @param step1 実行するステップ
	 * @return Spring Batchのジョブ
	 */
	@Bean
	public Job job(Step step) {
		// 生成するジョブ内で、実行前後の処理(リスナ)と処理単位(ステップ)を指定する
        return jobBuilderFactory.get("job")
                .incrementer(new RunIdIncrementer())
                .listener(listener())
                .start(step)
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
