package com.example.util;

public class DemoStringUtil {
	
	/**
	 * 引数の文字列の前後にダブルクォートを追加する.
	 * ただし、引数の文字列がnullの場合は空文字を返却する.
	 * @param str 任意の文字列
	 * @return 変換後文字列
	 */
	public static String addDoubleQuote(String str) {
		String retStr = "";
		if(str != null) {
			retStr = "\"" + str + "\"";
		}
		return retStr;
	}
	
	/**
	 * 引数の改行コード名にあてはまる改行コードを返却する.
	 * @param lineSepName 改行コード名
	 * @return 改行コード
	 */
	public static String getLineSepCode(String lineSepName) {
		String lineSepCode = "";
		if ("CR".equals(lineSepName)) {
			lineSepCode = "\r";
		} else if ("LF".equals(lineSepName)) {
			lineSepCode = "\n";
		} else if ("CRLF".equals(lineSepName)) {
			lineSepCode = "\r\n";
		}
		return lineSepCode;
	}

}
