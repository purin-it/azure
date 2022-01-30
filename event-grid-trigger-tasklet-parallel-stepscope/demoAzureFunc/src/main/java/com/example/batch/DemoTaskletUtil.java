package com.example.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepExecution;

import com.example.model.EventGridTriggerParam;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DemoTaskletUtil {

	/* Spring Bootでログ出力するためのLogbackのクラスを生成 */
	private static final Logger LOGGER = LoggerFactory.getLogger(DemoTaskletUtil.class);

	/**
	 * Spring Batchのジョブで渡されたパラメータから、EventGridTriggerParamオブジェクトを生成する.
	 * @param stepExecution ジョブステップ実行定義
	 * @return EventGridTriggerParamオブジェクト
	 */
	public static EventGridTriggerParam getEventGridTriggerParam(StepExecution stepExecution) {
		// Spring Batchのジョブで渡されたパラメータを取得し、EventGridTriggerParamオブジェクトを生成する
		String paramStr = stepExecution.getJobParameters().getString("eventGridTriggerParam");
		EventGridTriggerParam param = null;
		if (paramStr != null) {
			try {
				param = new ObjectMapper().readValue(paramStr
						, new TypeReference<EventGridTriggerParam>() { });
			} catch (Exception ex) {
				LOGGER.error(ex.getMessage());
				throw new RuntimeException(ex);
			}
		}
		return param;
	}
}
