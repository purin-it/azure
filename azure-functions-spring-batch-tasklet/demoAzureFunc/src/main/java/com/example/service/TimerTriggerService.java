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

import com.example.model.TimerTriggerParam;
import com.example.model.TimerTriggerResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class TimerTriggerService {

	/* Spring Bootでログ出力するためのLogbackのクラスを生成 */
	private static final Logger LOGGER = LoggerFactory.getLogger(TimerTriggerService.class);

	/** Spring Batchのジョブを起動するクラス */
	@Autowired
	JobLauncher jobLauncher;

	/** Spring Batchのジョブを定義するクラス */
	@Autowired
	Job job;

	/**
	 * タイマートリガーのテストを行うサービス.
	 * @param param TimerTrigger呼出用Param
	 * @return タイマートリガーのテストを行うサービスクラスの呼出結果
	 */
	public TimerTriggerResult timerTriggerTest(TimerTriggerParam param) {
		// タイマートリガーのテストを行うサービスが呼び出されたことをログ出力する
		LOGGER.info("TimerTriggerService timerTriggerTest triggered: " + param.getTimerInfo());

		// Spring Batchのジョブを起動する
		// ジョブ起動時に生成したパラメータを指定する
		try {
			jobLauncher.run(job, createInitialJobParameterMap(param));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		// タイマートリガーのテストを行うサービスクラスの呼出結果を返却する
		TimerTriggerResult result = new TimerTriggerResult();
		result.setResult("success");
		return result;
	}

	/**
	 * ジョブを起動するためのパラメータを生成する.
	 * @param TimerTriggerParamオブジェクト
	 * @return ジョブを起動するためのパラメータ
	 * @throws JsonProcessingException
	 */
	private JobParameters createInitialJobParameterMap(TimerTriggerParam param) throws JsonProcessingException {
		Map<String, JobParameter> m = new HashMap<>();
		m.put("time", new JobParameter(System.currentTimeMillis()));
		m.put("timerTriggerParam", new JobParameter(new ObjectMapper().writeValueAsString(param)));

		JobParameters p = new JobParameters(m);
		return p;
	}
}
