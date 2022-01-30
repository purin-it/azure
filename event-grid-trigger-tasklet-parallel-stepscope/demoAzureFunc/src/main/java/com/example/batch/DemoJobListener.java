package com.example.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;

import com.example.model.EventGridTriggerParam;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DemoJobListener extends JobExecutionListenerSupport {
	
	/* Spring Bootでログ出力するためのLogbackのクラスを生成 */
	private static final Logger LOGGER = LoggerFactory.getLogger(DemoJobListener.class);

	/**
	 * Spring Batchのジョブ実行前の処理を定義する.
	 */
	@Override
	public void beforeJob(JobExecution jobExecution) {
		super.beforeJob(jobExecution);
		
		// Spring Batchのジョブ実行前の処理が呼び出されたことをログ出力する
		printLog(jobExecution, "beforeJob");
	}
	
	/**
	 * Spring Batchのジョブ実行後の処理を定義する.
	 */
	@Override
	public void afterJob(JobExecution jobExecution) {
		super.afterJob(jobExecution);
		
		// Spring Batchのジョブ実行後の処理が呼び出されたことをログ出力する
		printLog(jobExecution, "afterJob");
	}
	
	/**
	 * ログ出力を行う.
	 * @param jobExecution ジョブ実行時の定義オブジェクト
	 * @param methodName メソッド名
	 */
	private void printLog(JobExecution jobExecution, String methodName) {
		try {
			String paramStr = jobExecution.getJobParameters().getString("eventGridTriggerParam");			
			if(paramStr != null) {
				EventGridTriggerParam param = new ObjectMapper()
						.readValue(paramStr, new TypeReference<EventGridTriggerParam>() {});
				LOGGER.info("DemoJobListener " + methodName + " triggered: " + param.getTimerInfo());
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
}
