package com.example.model;

import java.io.Serializable;
import java.util.ArrayList;
import lombok.Data;

@Data
public class GetFileListResult implements Serializable {
	
	/** シリアライズバージョンを設定 */
	private static final long serialVersionUID = -2696400144403338648L;
	
	/** ファイルデータリスト */
	private ArrayList<FileData> fileDataList;
	
	/** Azure Functionsのメッセージ */
	private String azureFunctionsMessage;
	
}