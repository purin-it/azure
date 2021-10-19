package com.example.batch;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.example.mybatis.model.UserData;
import com.example.util.DemoStringUtil;

@Component
public class DemoUpdateProcessor implements ItemProcessor<UserData, UserData> {

	/* Spring Bootでログ出力するためのLogbackのクラスを生成 */
	private static final Logger LOGGER = LoggerFactory.getLogger(DemoUpdateProcessor.class);

	/**
	 * 読み込んだデータの加工を行う.
	 * ここでは、読み込んだデータのチェックを行い、エラーがあればNULLを、エラーがなければ引数の値をそのまま返す.
	 */
	@Override
	public UserData process(UserData item) throws Exception {
		String itemId = item.getId();
		LOGGER.info("読み込んだデータの加工を行います。ID=" + itemId);
		
		// 1列目が空またはNULLの場合はエラー
		if (StringUtils.isEmpty(itemId)) {
			LOGGER.info("1列目が空またはNULLです。");
			return null;
		}
		// 1列目が数値以外の場合はエラー
		if (!StringUtils.isNumeric(itemId)) {
			LOGGER.info("1列目が数値以外です。" + " 該当のid=" + itemId);
			return null;
		}
		// 1列目の桁数が不正な場合はエラー
		if (itemId.length() > 6) {
			LOGGER.info("1列目の桁数が不正です。" + " 該当のid=" + itemId);
			return null;
		}

		// 2列目が空またはNULLの場合はエラー
		if (StringUtils.isEmpty(item.getName())) {
			LOGGER.info("2列目が空またはNULLです。" + " 該当のid=" + itemId);
			return null;
		}
		// 2列目の桁数が不正な場合はエラー
		if (item.getName().length() > 40) {
			LOGGER.info("2列目の桁数が不正です。" + " 該当のid=" + itemId);
			return null;
		}

		// 3列目が空またはNULLの場合はエラー
		if (StringUtils.isEmpty(item.getBirth_year())) {
			LOGGER.info("3列目が空またはNULLです。" + " 該当のid=" + itemId);
			return null;
		}
		// 3列目が数値以外の場合はエラー
		if (!StringUtils.isNumeric(item.getBirth_year())) {
			LOGGER.info("3列目が数値以外です。" + " 該当のid=" + itemId);
			return null;
		}
		// 3列目の桁数が不正な場合はエラー
		if (item.getBirth_year().length() > 4) {
			LOGGER.info("3列目の桁数が不正です。" + " 該当のid=" + itemId);
			return null;
		}

		// 4列目が空またはNULLの場合はエラー
		if (StringUtils.isEmpty(item.getBirth_month())) {
			LOGGER.info("4列目が空またはNULLです。" + " 該当のid=" + itemId);
			return null;
		}
		// 4列目が数値以外の場合はエラー
		if (!StringUtils.isNumeric(item.getBirth_month())) {
			LOGGER.info("4列目が数値以外です。" + " 該当のid=" + itemId);
			return null;
		}
		// 4列目の桁数が不正な場合はエラー
		if (item.getBirth_month().length() > 4) {
			LOGGER.info("4列目の桁数が不正です。" + " 該当のid=" + itemId);
			return null;
		}

		// 5列目が空またはNULLの場合はエラー
		if (StringUtils.isEmpty(item.getBirth_day())) {
			LOGGER.info("5列目が空またはNULLです。" + " 該当のid=" + itemId);
			return null;
		}
		// 5列目が数値以外の場合はエラー
		if (!StringUtils.isNumeric(item.getBirth_day())) {
			LOGGER.info("5列目が数値以外です。"+ " 該当のid=" + itemId);
			return null;
		}
		// 5列目の桁数が不正な場合はエラー
		if (item.getBirth_day().length() > 2) {
			LOGGER.info(" 5列目の桁数が不正です。"+ " 該当のid=" + itemId);
			return null;
		}

		// 3列目・4列目・5列目から生成される日付が不正であればエラー
		String birthDay = item.getBirth_year() + DemoStringUtil.addZero(item.getBirth_month())
				+ DemoStringUtil.addZero(item.getBirth_day());
		if (!DemoStringUtil.isCorrectDate(birthDay, "uuuuMMdd")) {
			LOGGER.info("3～5列目の日付が不正です。"+ " 該当のid=" + itemId);
			return null;
		}

		// 6列目が1,2以外の場合はエラー
		if (!("1".equals(item.getSex())) && !("2".equals(item.getSex()))) {
			LOGGER.info("6列目の性別が不正です。"+ " 該当のid=" + itemId);
			return null;
		}

		// 7列目の桁数が不正な場合はエラー
		if (!StringUtils.isEmpty(item.getMemo()) && item.getMemo().length() > 1024) {
			LOGGER.info("7列目の桁数が不正です。"+ " 該当のid=" + itemId);
			return null;
		}

		return item;
	}

}
