package com.example.batch;

import org.springframework.batch.item.file.transform.LineAggregator;

import com.example.mybatis.model.UserData;
import com.example.util.DemoStringUtil;

public class DemoLineAggregator implements LineAggregator<UserData>{

	/**
	 * 書き込みデータ(1行分)の設定内容を指定する.
	 */
	@Override
	public String aggregate(UserData userData) {
		StringBuilder sb = new StringBuilder();
		// ID
		sb.append(userData.getId());
		sb.append(",");
		// 名前
		sb.append(DemoStringUtil.addDoubleQuote(userData.getName()));
		sb.append(",");
		// 生年月日_年
		sb.append(userData.getBirth_year());
		sb.append(",");
		// 生年月日_月
		sb.append(userData.getBirth_month());
		sb.append(",");
		// 生年月日_日
		sb.append(userData.getBirth_day());
		sb.append(",");
		// 性別
		sb.append(DemoStringUtil.addDoubleQuote(userData.getSex()));
		sb.append(",");
		// メモ
		sb.append(DemoStringUtil.addDoubleQuote(userData.getMemo()));
		return sb.toString();
	}

}
