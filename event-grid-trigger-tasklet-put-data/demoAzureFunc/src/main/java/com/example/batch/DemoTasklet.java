package com.example.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.model.EventGridTriggerParam;
import com.example.service.DemoBatchService;

import org.springframework.batch.core.step.tasklet.Tasklet;

@Component
public class DemoTasklet implements Tasklet, StepExecutionListener {

	/* Spring Bootでログ出力するためのLogbackのクラスを生成 */
	private static final Logger LOGGER = LoggerFactory.getLogger(DemoTasklet.class);

	/**
	 * BlobStorageからファイル(user_data.csv)を読み込み、USER_DATAテーブルに書き込むサービス
	 */
	@Autowired
	private DemoBatchService demoBatchService;

	/**
	 * Spring Batchのジョブ実行前の処理を定義する.
	 */
	@Override
	public void beforeStep(StepExecution stepExecution) {
		// Spring Batchのジョブで渡されたパラメータを取得し、処理が呼び出されたことをログ出力する
		EventGridTriggerParam param = DemoTaskletUtil.getEventGridTriggerParam(stepExecution);
		LOGGER.info("DemoTasklet beforeStep " + " triggered: " + param.getTimerInfo());
	}

	/**
	 * Spring Batchのジョブ内での処理を定義する.
	 */
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		// Spring Batchのジョブで渡されたパラメータを取得し、処理が呼び出されたことをログ出力する
		EventGridTriggerParam param = DemoTaskletUtil
				.getEventGridTriggerParam(chunkContext.getStepContext().getStepExecution());
		LOGGER.info("DemoTasklet execute " + " triggered: " + param.getTimerInfo());

		// BlobStorageから指定したファイルを読み込み、USER_DATAテーブルに書き込むサービスを呼び出す
		demoBatchService.readUserData(param.getFileName());

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

		// Spring Batchのジョブ内での処理が呼び出されたことをログ出力する
		LOGGER.info("DemoTasklet afterStep " + " triggered: " + param.getTimerInfo());

		// ファイル名を次のタスクレットに渡し、実行後ジョブが終了したことを返す
		stepExecution.getJobExecution().getExecutionContext().put("inputFileName", param.getFileName());
		return ExitStatus.COMPLETED;
	}

}
