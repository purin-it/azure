package com.example;

import org.springframework.cloud.function.adapter.azure.FunctionInvoker;

import com.example.model.TimerTriggerParam;
import com.example.model.TimerTriggerResult;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.TimerTrigger;

public class TimerTriggerTestHandler extends FunctionInvoker<TimerTriggerParam, TimerTriggerResult>{

	/**
	 * TimerTriggerによって、DemoAzureFunctionクラスのtimerTriggerTestメソッドを呼び出す.
	 * @param timerInfo TimerTriggerイベント情報
	 * @param context コンテキストオブジェクト
	 */
	// 「schedule = "0 */1 * * * *"」で、1分毎にTimerTriggerイベントが発生するようになっている
	@FunctionName("timerTriggerTest")
	public void timerTriggerTest(@TimerTrigger(name = "timerTriggerTest", schedule = "0 */1 * * * *") String timerInfo,
			ExecutionContext context) {
		context.getLogger().info("TimerTriggerTestHandler timerTriggerTest triggered: " + timerInfo);
		
		TimerTriggerParam param = new TimerTriggerParam();
		param.setTimerInfo(timerInfo);
		
		handleRequest(param, context);
	}
	
}
