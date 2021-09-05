package com.example.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import com.example.model.EventGridTriggerParam;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class DemoTaskletLog3 implements Tasklet {

	/* Spring Bootでログ出力するためのLogbackのクラスを生成 */
	private static final Logger LOGGER = LoggerFactory.getLogger(DemoTaskletLog3.class);

	/**
	 * Spring Batchのジョブ内でのログ出力処理を定義する.
	 */
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		// Spring Batchのジョブ内での処理が呼び出されたことをログ出力する
		String paramStr = chunkContext.getStepContext().getStepExecution().getJobParameters()
				.getString("eventGridTriggerParam");

		EventGridTriggerParam param = null;
		if (paramStr != null) {
			param = new ObjectMapper().readValue(paramStr, new TypeReference<EventGridTriggerParam>() {});
			LOGGER.info("DemoTaskletLog3 execute " + " triggered: " + param.getTimerInfo());
		}

		// ジョブが終了したことを返す
		return RepeatStatus.FINISHED;
	}

}
