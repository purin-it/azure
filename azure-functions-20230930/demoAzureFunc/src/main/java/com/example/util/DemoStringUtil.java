package com.example.util;

import java.time.Duration;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;

import org.apache.commons.lang3.StringUtils;

import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.common.sas.AccountSasPermission;
import com.azure.storage.common.sas.AccountSasResourceType;
import com.azure.storage.common.sas.AccountSasService;
import com.azure.storage.common.sas.AccountSasSignatureValues;

public class DemoStringUtil {

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
	 * 引数で指定したBlobのSASトークンを生成し返す.
	 * @param client Blobサービスクライアントオブジェクト
	 * @return SASトークン
	 */
	public static String getServiceSasUriForBlob(BlobServiceClient client) {
		// SASトークンのアクセス権を読み取り可能に設定
		AccountSasPermission permissions = new AccountSasPermission().setReadPermission(true);

		// SASトークンがBlobコンテナやオブジェクトにアクセスできるように設定
		AccountSasResourceType resourceTypes = new AccountSasResourceType().setContainer(true).setObject(true);

		// SASトークンがBlobにアクセスできるように設定
		AccountSasService services = new AccountSasService().setBlobAccess(true);

		// SASトークンの有効期限を5分に設定
		OffsetDateTime expiryTime = OffsetDateTime.now().plus(Duration.ofMinutes(5));

		// SASトークンを作成
		AccountSasSignatureValues sasValues = new AccountSasSignatureValues(expiryTime, permissions, services,
				resourceTypes);
		return client.generateAccountSas(sasValues);
	}

}
