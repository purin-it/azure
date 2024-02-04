package com.example.batch;

import java.util.List;

import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.example.mybatis.model.UserData;
import com.example.service.DemoBlobService;
import com.example.util.DemoStringUtil;

@Component
public class DemoItemWriter implements ItemWriter<UserData> {
	
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
	 * 読み込んだDBのデータを、BLOB(CSVファイル)に書き込む処理を定義する.
	 */
	@Override
	public void write(List<? extends UserData> items) {
		// 書き込みデータを格納する文字列
		StringBuilder sb = new StringBuilder();

		// 改行コードを変換
		String lineSeparator = DemoStringUtil.getLineSepCode(lineSepName);
		
		// CSV書き込み方式が一括(ALL)の場合
		if ("ALL".equals(csvWriteMethod)) {
			// ヘッダー行を、書き込みデータに追加
			String header = "id,name,birth_year,birth_month,birth_day,sex,memo";
			sb.append(header + lineSeparator);
		}
		
		// 読み込んだDBのデータ各行を、書き込みデータに追加
		for(UserData item : items) {
			// ID
			sb.append(item.getId());
			sb.append(",");
			// 名前
			sb.append(DemoStringUtil.addDoubleQuote(item.getName()));
			sb.append(",");
			// 生年月日_年
			sb.append(item.getBirth_year());
			sb.append(",");
			// 生年月日_月
			sb.append(item.getBirth_month());
			sb.append(",");
			// 生年月日_日
			sb.append(item.getBirth_day());
			sb.append(",");
			// 性別
			sb.append(DemoStringUtil.addDoubleQuote(item.getSex()));
			sb.append(",");
			// メモ
			sb.append(DemoStringUtil.addDoubleQuote(item.getMemo()));
			// 改行コード
			sb.append(lineSeparator);
		}
		
		// CSV書き込み方式に応じて、書き込みデータを、BLOB(CSVファイル)に書き込む
		if ("ALL".equals(csvWriteMethod)) {
			demoBlobService.writeBlockBlob(blobName, sb.toString());
		} else {
			demoBlobService.writeAppendBlob(blobName, sb.toString());
		}
	}

}
