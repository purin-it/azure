package com.example.batch;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.mybatis.model.UserData;
import com.example.service.DemoBlobService;
import com.example.util.DemoStringUtil;

@Component
public class DemoUpdateProcessor implements ItemProcessor<UserData, UserData> {

	/* Spring Bootでログ出力するためのLogbackのクラスを生成 */
	private static final Logger LOGGER = LoggerFactory.getLogger(DemoUpdateProcessor.class);
	
	/** BLOBへアクセスするサービス */
	@Autowired
	private DemoBlobService demoBlobService;
	
	/** 結果を出力するBLOB名 */
	private static final String BLOB_NAME = "result.txt";
	
	/**
	 * 読み込んだデータの加工を行う.
	 * ここでは、読み込んだデータのチェックを行い、エラーがあればNULLを、
	 * エラーがなければ引数の値をそのまま返す.
	 */
	@Override
	public UserData process(UserData item) throws Exception {
		String itemId = StringUtils.isEmpty(item.getId()) ? "(未設定)" : item.getId();
		LOGGER.info("読み込んだデータの加工を行います。ID=" + itemId);
		
		// 1列目が空またはNULLの場合はエラー
		if (StringUtils.isEmpty(itemId)) {
			printLog(itemId, "1列目が空またはNULLです。");
			return null;
		}
		// 1列目が数値以外の場合はエラー
		if (!StringUtils.isNumeric(itemId)) {
			printLog(itemId, "1列目が数値以外です。");
			return null;
		}
		// 1列目の桁数が不正な場合はエラー
		if (itemId.length() > 6) {
			printLog(itemId, "1列目の桁数が不正です。");
			return null;
		}

		// 2列目が空またはNULLの場合はエラー
		if (StringUtils.isEmpty(item.getName())) {
			printLog(itemId, "2列目が空またはNULLです。");
			return null;
		}
		// 2列目の桁数が不正な場合はエラー
		if (item.getName().length() > 40) {
			printLog(itemId, "2列目の桁数が不正です。");
			return null;
		}

		// 3列目が空またはNULLの場合はエラー
		if (StringUtils.isEmpty(item.getBirth_year())) {
			printLog(itemId, "3列目が空またはNULLです。");
			return null;
		}
		// 3列目が数値以外の場合はエラー
		if (!StringUtils.isNumeric(item.getBirth_year())) {
			printLog(itemId, "3列目が数値以外です。");
			return null;
		}
		// 3列目の桁数が不正な場合はエラー
		if (item.getBirth_year().length() > 4) {
			printLog(itemId, "3列目の桁数が不正です。");
			return null;
		}

		// 4列目が空またはNULLの場合はエラー
		if (StringUtils.isEmpty(item.getBirth_month())) {
			printLog(itemId, "4列目が空またはNULLです。");
			return null;
		}
		// 4列目が数値以外の場合はエラー
		if (!StringUtils.isNumeric(item.getBirth_month())) {
			printLog(itemId, "4列目が数値以外です。");
			return null;
		}
		// 4列目の桁数が不正な場合はエラー
		if (item.getBirth_month().length() > 4) {
			printLog(itemId, "4列目の桁数が不正です。");
			return null;
		}

		// 5列目が空またはNULLの場合はエラー
		if (StringUtils.isEmpty(item.getBirth_day())) {
			printLog(itemId, "5列目が空またはNULLです。");
			return null;
		}
		// 5列目が数値以外の場合はエラー
		if (!StringUtils.isNumeric(item.getBirth_day())) {
			printLog(itemId, "5列目が数値以外です。");
			return null;
		}
		// 5列目の桁数が不正な場合はエラー
		if (item.getBirth_day().length() > 2) {
			printLog(itemId, "5列目の桁数が不正です。");
			return null;
		}

		// 3列目・4列目・5列目から生成される日付が不正であればエラー
		String birthDay = item.getBirth_year() + DemoStringUtil.addZero(item.getBirth_month())
				+ DemoStringUtil.addZero(item.getBirth_day());
		if (!DemoStringUtil.isCorrectDate(birthDay, "uuuuMMdd")) {
			printLog(itemId, "3～5列目の日付が不正です。");
			return null;
		}

		// 6列目が1,2以外の場合はエラー
		if (!("1".equals(item.getSex())) && !("2".equals(item.getSex()))) {
			printLog(itemId, "6列目の性別が不正です。");
			return null;
		}

		// 7列目の桁数が不正な場合はエラー
		if (!StringUtils.isEmpty(item.getMemo()) && item.getMemo().length() > 1024) {
			printLog(itemId, "7列目の桁数が不正です。");
			return null;
		}

		return item;
	}
	
	/**
	 * エラーメッセージを出力する
	 * @param itemId ID
	 * @param message メッセージ
	 */
	private void printLog(String itemId, String message) {
		// ログにエラーメッセージを書き込む
		LOGGER.info(message + " 該当のID=" + itemId);
		
		// BlobStorageにチェックエラーだった旨のメッセージを書き込む
		demoBlobService.writeAppendBlob(BLOB_NAME, "ID: " + itemId + ": チェックエラー");
	}

}
