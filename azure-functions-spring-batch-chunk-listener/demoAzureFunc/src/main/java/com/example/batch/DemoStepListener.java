package com.example.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.service.DemoBlobService;

@Component
public class DemoStepListener implements StepExecutionListener {

	/* Spring Bootでログ出力するためのLogbackのクラスを生成 */
	private static final Logger LOGGER = LoggerFactory.getLogger(DemoStepListener.class);
	
	/** BLOBへアクセスするサービス */
	@Autowired
	private DemoBlobService demoBlobService;
	
	/** 結果を出力するBLOB名 */
	private static final String BLOB_NAME = "result.txt";
	
	/**
	 * Spring Batchのジョブ内で指定する処理単位(ステップ)実行前の処理を定義する.
	 */
	@Override
	public void beforeStep(StepExecution stepExecution) {
		// 結果を出力するBLOBが残っていれば削除
		demoBlobService.deleteAppendBlob(BLOB_NAME);
	}

	/**
	 * Spring Batchのジョブ内で指定する処理単位(ステップ)実行後の処理を定義する.
	 */
	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		// 読み込み件数・書き込み件数を取得しログ出力
		int readCnt = stepExecution.getReadCount();
		int writeCnt = stepExecution.getWriteCount();
		LOGGER.info("読み込み件数：" + readCnt + ", 書き込み件数：" + writeCnt);
		
		return ExitStatus.COMPLETED;
	}

}
