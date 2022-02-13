package com.example.model;

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
	private Byte[] fileData;
	
}
