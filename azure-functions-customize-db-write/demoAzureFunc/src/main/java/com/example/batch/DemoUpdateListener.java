package com.example.batch;

import java.util.List;

import org.springframework.batch.core.ItemWriteListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.mybatis.model.UserData;
import com.example.service.DemoBlobService;

@Component
public class DemoUpdateListener implements ItemWriteListener<UserData>{
	
	/** BLOBへアクセスするサービス */
	@Autowired
	private DemoBlobService demoBlobService;
	
	/** 結果を出力するBLOB名 */
	private static final String BLOB_NAME = "result.txt";
	
	/**
	 * データ書き込み前の処理を定義する.
	 */
	@Override
	public void beforeWrite(List<? extends UserData> items) {
		// 何もしない
	}

	/**
	 * データ書き込み後の処理を定義する.
	 */
	@Override
	public void afterWrite(List<? extends UserData> items) {
		// BlobStorageに正常終了だった旨のメッセージを書き込む
		for(UserData u : items) {
			demoBlobService.writeAppendBlob(BLOB_NAME, "ID: " + u.getId() + ": 書き込み正常");
		}
	}

	/**
	 * データ書き込みエラー後の処理を定義する.
	 */
	@Override
	public void onWriteError(Exception exception, List<? extends UserData> items) {
		// BlobStorageに異常終了だった旨のメッセージを書き込む
		for(UserData u : items) {
			demoBlobService.writeAppendBlob(BLOB_NAME, "ID: " + u.getId() + ": 書き込み異常");
		}
	}

}
