package com.example.demo;

import lombok.Data;

@Data
public class FileData {

	/** ID */
	private int id;
	
	/** ファイル名 */
	private String fileName;
	
	/** ファイルパス */
	private String filePath;
	
	/** ファイルデータ */
	private byte[] fileData;
	
}
