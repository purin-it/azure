package com.example.util;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;

import org.apache.commons.lang3.StringUtils;
import org.mozilla.universalchardet.UniversalDetector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DemoStringUtil {

	/* Spring Bootでログ出力するためのLogbackのクラスを生成 */
	private static final Logger LOGGER = LoggerFactory.getLogger(DemoStringUtil.class);
	
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
	 * 引数のInputStreamから判定した文字コードを返す.
	 * @param is InputStreamオブジェクト
	 * @return 判定した文字コード
	 * @throws IOException
	 */
	public static String getCharsetName(InputStream is) throws IOException {
		// 4kBのメモリバッファを確保する
		byte[] buf = new byte[4096];
		UniversalDetector detector = new UniversalDetector(null);

		// 文字コードの推測結果が得られるまでInputStreamを読み進める
		int nread;
		while ((nread = is.read(buf)) > 0 && !detector.isDone()) {
			detector.handleData(buf, 0, nread);
		}

		// 推測結果を取得する
		detector.dataEnd();
		final String detectedCharset = detector.getDetectedCharset();
		LOGGER.info("判定された文字コード(DemoStringUtil): " + detectedCharset);
		
		detector.reset();

		// 文字コードを取得できなかった場合、環境のデフォルトを使用する
		if (detectedCharset != null) {
			return detectedCharset;
		}
		return System.getProperty("file.encoding");
	}
}
