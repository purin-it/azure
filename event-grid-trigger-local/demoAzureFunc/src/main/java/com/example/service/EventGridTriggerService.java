package com.example.service;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.model.EventGridTriggerParam;
import com.example.model.EventGridTriggerResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class EventGridTriggerService {

	/* Spring Bootでログ出力するためのLogbackのクラスを生成 */
	private static final Logger LOGGER = LoggerFactory.getLogger(EventGridTriggerService.class);

	/** Spring Batchのジョブを起動するクラス */
	@Autowired
	JobLauncher jobLauncher;

	/** Spring Batchのジョブを定義するクラス */
	@Autowired
	Job job;

	/**
	 * イベントグリッドトリガーのテストを行うサービス.
	 * @param param EventGridTrigger呼出用Param
	 * @return イベントグリッドトリガーのテストを行うサービスクラスの呼出結果
	 */
	public EventGridTriggerResult eventGridTriggerTest(EventGridTriggerParam param) {
		// イベントグリッドトリガーのテストを行うサービスが呼び出されたことをログ出力する
		LOGGER.info("EventGridTriggerService eventGridTriggerTest triggered: " + param.getTimerInfo());

		// Spring Batchのジョブを起動する
		// ジョブ起動時に生成したパラメータを指定する
		try {
			jobLauncher.run(job, createInitialJobParameterMap(param));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		// イベントグリッドトリガーのテストを行うサービスクラスの呼出結果を返却する
		EventGridTriggerResult result = new EventGridTriggerResult();
		result.setResult("success");
		return result;
	}

	/**
	 * ジョブを起動するためのパラメータを生成する.
	 * @param EventGridTriggerParamオブジェクト
	 * @return ジョブを起動するためのパラメータ
	 * @throws JsonProcessingException
	 */
	private JobParameters createInitialJobParameterMap(EventGridTriggerParam param) throws JsonProcessingException {
		Map<String, JobParameter> m = new HashMap<>();
		m.put("time", new JobParameter(System.currentTimeMillis()));
		m.put("eventGridTriggerParam", new JobParameter(new ObjectMapper().writeValueAsString(param)));

		JobParameters p = new JobParameters(m);
		return p;
	}
}
