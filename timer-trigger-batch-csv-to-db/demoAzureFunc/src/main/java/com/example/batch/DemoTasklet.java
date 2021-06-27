package com.example.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.model.TimerTriggerParam;
import com.example.service.DemoBatchService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.batch.core.step.tasklet.Tasklet;

@Component
public class DemoTasklet implements Tasklet {

	/* Spring Bootでログ出力するためのLogbackのクラスを生成 */
	private static final Logger LOGGER = LoggerFactory.getLogger(DemoTasklet.class);

	/**
	 * BlobStorageからファイル(user_data.csv)を読み込み、USER_DATAテーブルに書き込むサービス
	 */
	@Autowired
	private DemoBatchService demoBatchService;

	/**
	 * Spring Batchのジョブ内での処理を定義する.
	 */
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		// Spring Batchのジョブ内での処理が呼び出されたことをログ出力する
		String paramStr = chunkContext.getStepContext().getStepExecution().getJobParameters()
				.getString("timerTriggerParam");
		if (paramStr != null) {
			TimerTriggerParam param = new ObjectMapper().readValue(paramStr, new TypeReference<TimerTriggerParam>() {
			});
			LOGGER.info("DemoTasklet execute " + " triggered: " + param.getTimerInfo());
		}

		// BlobStorageからファイル(user_data.csv)を読み込み、USER_DATAテーブルに書き込むサービスを呼び出す
		demoBatchService.readUserData();

		// ジョブが終了したことを返す
		return RepeatStatus.FINISHED;
	}

}
