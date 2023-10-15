package com.example.batch;

import java.io.File;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.service.DemoBlobService;

@Component
public class DemoStepListener implements StepExecutionListener {
	
	/** BLOBへアクセスするサービス */
	@Autowired
	private DemoBlobService demoBlobService;

	/** 取得したUSER_DATAテーブルのデータを出力するファイルパス */
	private static final String TMP_FILE_PATH = "/home/";
	
	/** 取得したUSER_DATAテーブルのデータを出力するファイル名 */
	private static final String TMP_FILE_NAME = "tmp_user_data.csv";
	
	/** 取得したUSER_DATAテーブルのデータを出力するBLOB名 */
	private static final String BLOB_NAME = "user_data.csv";
	
	/**
	 * Spring Batchのジョブ内で指定する処理単位(ステップ)実行前の処理を定義する.
	 */
	@Override
	public void beforeStep(StepExecution stepExecution) {
		// Blobファイルを削除
		demoBlobService.deleteBlockBlob(BLOB_NAME);
		
		// ローカルファイルがあれば削除
		// (後続のafterStepメソッドでの削除ができないため、ここで削除)
		File tmpFile = new File(TMP_FILE_PATH + TMP_FILE_NAME);
		if(tmpFile.exists()) {
			tmpFile.delete();
		}
	}

	/**
	 * Spring Batchのジョブ内で指定する処理単位(ステップ)実行後の処理を定義する.
	 */
	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		// ローカルファイルをBlobにアップロード
		demoBlobService.uploadBlobFromFile(
				TMP_FILE_PATH + TMP_FILE_NAME, BLOB_NAME);
		
		return ExitStatus.COMPLETED;
	}

}
