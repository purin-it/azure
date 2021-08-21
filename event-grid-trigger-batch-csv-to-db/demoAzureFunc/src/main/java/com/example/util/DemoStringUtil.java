package com.example.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;

import org.apache.commons.lang3.StringUtils;

public class DemoStringUtil {

	/**
	 * 文字列前後のダブルクォーテーションを削除する.
	 * @param str 変換前文字列
	 * @return 変換後文字列
	 */
	public static String trimDoubleQuot(String regStr) {
		if (StringUtils.isEmpty(regStr)) {
			return regStr;
		}
		char c = '"';
		if (regStr.charAt(0) == c && regStr.charAt(regStr.length() - 1) == c) {
			return regStr.substring(1, regStr.length() - 1);
		} else {
			return regStr;
		}
	}

	/**
	 * DateTimeFormatterを利用して日付チェックを行う.
	 * @param dateStr    チェック対象文字列
	 * @param dateFormat 日付フォーマット
	 * @return 日付チェック結果
	 */
	public static boolean isCorrectDate(String dateStr, String dateFormat) {
		if (StringUtils.isEmpty(dateStr) || StringUtils.isEmpty(dateFormat)) {
			return false;
		}
		// 日付と時刻を厳密に解決するスタイルで、DateTimeFormatterオブジェクトを作成
		DateTimeFormatter df = DateTimeFormatter.ofPattern(dateFormat).withResolverStyle(ResolverStyle.STRICT);
		try {
			// チェック対象文字列をLocalDate型の日付に変換できれば、チェックOKとする
			LocalDate.parse(dateStr, df);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 数値文字列が1桁の場合、頭に0を付けて返す.
	 * @param intNum 数値文字列
	 * @return 変換後数値文字列
	 */
	public static String addZero(String intNum) {
		if (StringUtils.isEmpty(intNum)) {
			return intNum;
		}
		if (intNum.length() == 1) {
			return "0" + intNum;
		}
		return intNum;
	}
	
	/**
	 * 現在日時をyyyyMMddHHmmss形式で返す.
	 * @return 現在日時
	 */
	public static String getNowDateTime() {
		LocalDateTime localDateTime = LocalDateTime.now();
		DateTimeFormatter datetimeformatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
		return datetimeformatter.format(localDateTime);
	}
}
