package com.example.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import com.example.model.EventGridTriggerParam;

@Component
@StepScope
public class DemoTaskletLog implements Tasklet, StepExecutionListener {

	/* Spring Bootでログ出力するためのLogbackのクラスを生成 */
	private static final Logger LOGGER = LoggerFactory.getLogger(DemoTaskletLog.class);

	/**
	 * Spring Batchのジョブ実行前の処理を定義する.
	 */
	@Override
	public void beforeStep(StepExecution stepExecution) {
		// Spring Batchのジョブで渡されたパラメータを取得し、処理が呼び出されたことをログ出力する
		EventGridTriggerParam param = DemoTaskletUtil.getEventGridTriggerParam(stepExecution);
		LOGGER.info("DemoTaskletLog beforeStep " + " triggered: " + param.getTimerInfo());

		// タスクレットに渡されたファイル名を取得する
		String inputFileName = (String) stepExecution.getJobExecution().getExecutionContext().get("inputFileName");
		LOGGER.info("inputFileName: " + inputFileName);
	}

	/**
	 * Spring Batchのジョブ内でのログ出力処理を定義する.
	 */
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		// Spring Batchのジョブで渡されたパラメータを取得し、処理が呼び出されたことをログ出力する
		EventGridTriggerParam param = DemoTaskletUtil
				.getEventGridTriggerParam(chunkContext.getStepContext().getStepExecution());
		LOGGER.info("DemoTaskletLog execute " + " triggered: " + param.getTimerInfo());

		// ジョブが終了したことを返す
		return RepeatStatus.FINISHED;
	}

	/**
	 * Spring Batchのジョブ実行後の処理を定義する.
	 */
	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		// Spring Batchのジョブで渡されたパラメータを取得し、処理が呼び出されたことをログ出力する
		EventGridTriggerParam param = DemoTaskletUtil.getEventGridTriggerParam(stepExecution);
		LOGGER.info("DemoTaskletLog afterStep " + " triggered: " + param.getTimerInfo());

		// 実行後ジョブが終了したことを返す
		return ExitStatus.COMPLETED;
	}

}
