package com.example.model;

import lombok.Data;

@Data
public class FileUploadParam {

	/** ファイル名 */
	private String fileName;
	
	/** ファイルデータ */
	private Byte[] fileData;
	
}