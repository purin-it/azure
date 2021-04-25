package com.example.service;

import org.springframework.stereotype.Service;

import com.example.model.TimerTriggerParam;
import com.example.model.TimerTriggerResult;

@Service
public class TimerTriggerService {

	/**
	 * タイマートリガーのテストを行うサービス.
	 * @param param TimerTrigger呼出用Param
	 * @return タイマートリガーのテストを行うサービスクラスの呼出結果
	 */
	public TimerTriggerResult timerTriggerTest(TimerTriggerParam param) {
		// タイマートリガーのテストを行うサービスが呼び出されたことをログ出力する
		param.getLogger().info("TimerTriggerService timerTriggerTest triggered: " + param.getTimerInfo());

		// タイマートリガーのテストを行うサービスクラスの呼出結果を返却する
		TimerTriggerResult result = new TimerTriggerResult();
		result.setResult("success");
		return result;
	}
}
