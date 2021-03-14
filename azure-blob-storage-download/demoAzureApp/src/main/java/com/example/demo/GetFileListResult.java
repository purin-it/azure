package com.example.demo;

import java.util.ArrayList;
import lombok.Data;

@Data
public class GetFileListResult {
	
	/** ファイルデータリスト */
	private ArrayList<FileData> fileDataList;
	
}