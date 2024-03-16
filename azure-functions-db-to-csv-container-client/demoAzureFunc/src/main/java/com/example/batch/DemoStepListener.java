package com.example.batch;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.example.service.DemoBlobService;
import com.example.util.DemoStringUtil;

@Component
public class DemoStepListener implements StepExecutionListener {
	
	/** BLOBへアクセスするサービス */
	@Autowired
	private DemoBlobService demoBlobService;

	/** 取得したUSER_DATAテーブルのデータを出力するBLOB名 */
	@Value("${blob.name}")
	private String blobName;
	
	/** 改行コード名 */
	@Value("${line.sep.name}")
	private String lineSepName;
	
	/** CSV書き込み方式 */
	@Value("${csv.write.method}")
	private String csvWriteMethod;
	
	/**
	 * Spring Batchのジョブ内で指定する処理単位(ステップ)実行前の処理を定義する.
	 */
	@Override
	public void beforeStep(StepExecution stepExecution) {
		// Blobファイル(BlockBlob,追加Blob)があれば削除
		demoBlobService.deleteBlockBlob(blobName);
		demoBlobService.deleteAppendBlob(blobName);
		
		// 改行コードを変換
		String lineSeparator = DemoStringUtil.getLineSepCode(lineSepName);
		
		// CSV書き込み方式が分割(DIV)の場合
		if ("DIV".equals(csvWriteMethod)) {
			// Blobファイルを作成後、ヘッダー行を出力
			String header = "id,name,birth_year,birth_month,birth_day,sex,memo";
			demoBlobService.writeAppendBlob(blobName, header + lineSeparator);
		}

	}

	/**
	 * Spring Batchのジョブ内で指定する処理単位(ステップ)実行後の処理を定義する.
	 */
	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		return ExitStatus.COMPLETED;
	}

}
