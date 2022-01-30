package com.example.model;

import lombok.Data;

@Data
public class EventGridTriggerParam {

	/** 読み込むBlobストレージのファイル名 */
	private String fileName;
	
	/** Timer情報 */
	private String timerInfo;
	
}
