package com.example.model;

import lombok.Data;
import  java.util.logging.Logger;

@Data
public class TimerTriggerParam {

	/** TimerTrigger情報 */
	private String timerInfo;
	
	/** ログ出力クラス */
	private Logger logger;
	
}
