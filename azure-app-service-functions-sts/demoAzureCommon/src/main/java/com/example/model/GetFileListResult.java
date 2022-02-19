package com.example.model;

import java.util.ArrayList;
import lombok.Data;

@Data
public class GetFileListResult {
	
	/** ファイルデータリスト */
	private ArrayList<FileData> fileDataList;
	
}