package com.example.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.mybatis.UserDataMapper;
import com.example.mybatis.model.UserData;
import com.example.util.DemoStringUtil;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import com.microsoft.azure.storage.blob.CopyStatus;

@Service
public class DemoBatchService {

	/* Spring Bootでログ出力するためのLogbackのクラスを生成 */
	private static final Logger LOGGER = LoggerFactory.getLogger(DemoBatchService.class);

	/** Azure Storageのアカウント名 */
	@Value("${azure.storage.accountName}")
	private String storageAccountName;

	/** Azure Storageへのアクセスキー */
	@Value("${azure.storage.accessKey}")
	private String storageAccessKey;

	/** Azure StorageのBlobコンテナー名 */
	@Value("${azure.storage.containerName}")
	private String storageContainerName;

	/** USER_DATAテーブルにアクセスするマッパー */
	@Autowired
	private UserDataMapper userDataMapper;

	/**
	 * BlobStorageから引数で指定したファイルを読み込み、USER_DATAテーブルに書き込む
	 * @param fileName ファイル名
	 */
	@Transactional
	public void readUserData(String fileName) {
		// ファイルがCSVファイル以外か、ファイル名の末尾が「_yyyymmddhhmmss.csv」の場合は、何もせず処理を終了する
		if (!fileName.matches(".*\\.csv") || fileName.matches(".*_[0-9]{14}\\.csv")) {
			return;
		}

		// BlobStorageから引数で指定したファイルを読み込む
		try (BufferedReader br = new BufferedReader(new InputStreamReader(getBlobCsvData(fileName), "UTF-8"))) {
			String lineStr = null;
			int lineCnt = 0;

			// 1行目(タイトル行)は読み飛ばし、2行目以降はチェックの上、USER_DATAテーブルに書き込む
			// チェックエラー時はエラーログを出力の上、DB更新は行わず先へ進む
			while ((lineStr = br.readLine()) != null) {
				// 1行目(タイトル行)は読み飛ばす
				lineCnt++;
				if (lineCnt == 1) {
					continue;
				}

				// 引数のCSVファイル1行分の文字列を受け取り、エラーがあればNULLを、
				// エラーがなければUserDataオブジェクトに変換し返す
				UserData userData = checkData(lineStr, lineCnt);

				// 読み込んだファイルをUSER_DATAテーブルに書き込む
				if (userData != null) {
					userDataMapper.upsert(userData);
				}
			}

		} catch (Exception ex) {
			LOGGER.error(ex.getMessage());
			throw new RuntimeException(ex);

		} finally {
			try {
				// 読み込んだファイル名の末尾に「_yyyymmddhhmmss」を追加する
				renameBlobCsvData(fileName);
			} catch (Exception ex) {
				LOGGER.error(ex.getMessage());
				throw new RuntimeException(ex);
			}
		}
	}

	/**
	 * Blobストレージから指定したファイルデータを取得する.
	 * @param fileName ファイル名
	 * @return 指定したファイルデータの入力ストリーム
	 * @throws URISyntaxException
	 * @throws InvalidKeyException
	 * @throws StorageException
	 */
	private InputStream getBlobCsvData(String fileName)
			throws URISyntaxException, InvalidKeyException, StorageException {
		// Blob内のコンテナーを取得
		CloudBlobContainer container = getBlobContainer();

		// BlobStorageから指定したファイルを読み込む
		CloudBlockBlob blob = container.getBlockBlobReference(fileName);
		return blob.openInputStream();
	}

	/**
	 * Blobストレージの指定したファイルをリネームする.
	 * @param fileName ファイル名
	 * @throws URISyntaxException
	 * @throws InvalidKeyException
	 * @throws StorageException
	 * @throws InterruptedException
	 */
	private void renameBlobCsvData(String fileName)
			throws URISyntaxException, InvalidKeyException, StorageException, InterruptedException {
		// Blob内のコンテナーを取得
		CloudBlobContainer container = getBlobContainer();

		// 指定したファイルを、末尾に「_yyyymmddhhmmss」を追加したファイル名のBlobにコピー
		CloudBlockBlob blob = container.getBlockBlobReference(fileName);
		CloudBlockBlob blobCopy = container.getBlockBlobReference(getNewFileName(fileName));
		blobCopy.startCopy(blob.getUri());

		// コピーが終わるまで待機
		while (true) {
			if (blobCopy.getCopyState().getStatus() != CopyStatus.PENDING) {
				break;
			}
			Thread.sleep(1000);
		}

		// 指定したファイルを削除
		blob.delete();
	}

	/**
	 * Blob内のコンテナーを取得し返す.
	 * @return Blob内のコンテナー
	 * @throws URISyntaxException
	 * @throws InvalidKeyException
	 * @throws StorageException
	 */
	private CloudBlobContainer getBlobContainer() throws URISyntaxException, InvalidKeyException, StorageException {
		// Blobストレージへの接続文字列
		String storageConnectionString = "DefaultEndpointsProtocol=https;" 
				+ "AccountName=" + storageAccountName + ";"
				+ "AccountKey=" + storageAccessKey + ";";

		// ストレージアカウントオブジェクトを取得
		CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);

		// Blobクライアントオブジェクトを取得
		CloudBlobClient blobClient = storageAccount.createCloudBlobClient();

		// Blob内のコンテナーを取得し返却
		return blobClient.getContainerReference(storageContainerName);
	}

	/**
	 * 指定したファイルの末尾に「_yyyymmddhhmmss」を追加したファイル名を返す.
	 * @param fileName ファイル名
	 * @return リネーム後のファイル名
	 */
	private String getNewFileName(String fileName) {
		StringBuilder sbNewFile = new StringBuilder();
		sbNewFile.append(fileName.replace(".csv", ""));
		sbNewFile.append("_");
		sbNewFile.append(DemoStringUtil.getNowDateTime());
		sbNewFile.append(".csv");
		return sbNewFile.toString();
	}

	/**
	 * 引数のCSVファイル1行分の文字列を受け取り、エラーがあればNULLを、
	 * エラーがなければUserDataオブジェクトに変換し返す.
	 * @param lineStr CSVファイル1行分の文字列
	 * @param lineCnt 行数
	 * @return 変換後のUserData
	 */
	private UserData checkData(String lineStr, int lineCnt) {
		// 引数のCSVファイル1行分の文字列をカンマで分割
		String[] strArray = lineStr.split(",");

		// 桁数不正の場合はエラー
		if (strArray == null || strArray.length != 7) {
			LOGGER.info(lineCnt + "行目： 桁数が不正です。");
			return null;
		}

		// 文字列前後のダブルクォーテーションを削除する
		for (int i = 0; i < strArray.length; i++) {
			strArray[i] = DemoStringUtil.trimDoubleQuot(strArray[i]);
		}

		// 1列目が空またはNULLの場合はエラー
		if (StringUtils.isEmpty(strArray[0])) {
			LOGGER.info(lineCnt + "行目： 1列目が空またはNULLです。");
			return null;
		}
		// 1列目が数値以外の場合はエラー
		if (!StringUtils.isNumeric(strArray[0])) {
			LOGGER.info(lineCnt + "行目： 1列目が数値以外です。");
			return null;
		}
		// 1列目の桁数が不正な場合はエラー
		if (strArray[0].length() > 6) {
			LOGGER.info(lineCnt + "行目： 1列目の桁数が不正です。");
			return null;
		}

		// 2列目が空またはNULLの場合はエラー
		if (StringUtils.isEmpty(strArray[1])) {
			LOGGER.info(lineCnt + "行目： 2列目が空またはNULLです。");
			return null;
		}
		// 2列目の桁数が不正な場合はエラー
		if (strArray[1].length() > 40) {
			LOGGER.info(lineCnt + "行目： 2列目の桁数が不正です。");
			return null;
		}

		// 3列目が空またはNULLの場合はエラー
		if (StringUtils.isEmpty(strArray[2])) {
			LOGGER.info(lineCnt + "行目： 3列目が空またはNULLです。");
			return null;
		}
		// 3列目が数値以外の場合はエラー
		if (!StringUtils.isNumeric(strArray[2])) {
			LOGGER.info(lineCnt + "行目： 3列目が数値以外です。");
			return null;
		}
		// 3列目の桁数が不正な場合はエラー
		if (strArray[2].length() > 4) {
			LOGGER.info(lineCnt + "行目： 3列目の桁数が不正です。");
			return null;
		}

		// 4列目が空またはNULLの場合はエラー
		if (StringUtils.isEmpty(strArray[3])) {
			LOGGER.info(lineCnt + "行目： 4列目が空またはNULLです。");
			return null;
		}
		// 4列目が数値以外の場合はエラー
		if (!StringUtils.isNumeric(strArray[3])) {
			LOGGER.info(lineCnt + "行目： 4列目が数値以外です。");
			return null;
		}
		// 4列目の桁数が不正な場合はエラー
		if (strArray[3].length() > 2) {
			LOGGER.info(lineCnt + "行目： 4列目の桁数が不正です。");
			return null;
		}

		// 5列目が空またはNULLの場合はエラー
		if (StringUtils.isEmpty(strArray[4])) {
			LOGGER.info(lineCnt + "行目： 5列目が空またはNULLです。");
			return null;
		}
		// 5列目が数値以外の場合はエラー
		if (!StringUtils.isNumeric(strArray[4])) {
			LOGGER.info(lineCnt + "行目： 5列目が数値以外です。");
			return null;
		}
		// 5列目の桁数が不正な場合はエラー
		if (strArray[4].length() > 2) {
			LOGGER.info(lineCnt + "行目： 5列目の桁数が不正です。");
			return null;
		}

		// 3列目・4列目・5列目から生成される日付が不正であればエラー
		String birthDay = strArray[2] + DemoStringUtil.addZero(strArray[3]) + DemoStringUtil.addZero(strArray[4]);
		if (!DemoStringUtil.isCorrectDate(birthDay, "uuuuMMdd")) {
			LOGGER.info(lineCnt + "行目： 3～5列目の日付が不正です。");
			return null;
		}

		// 6列目が1,2以外の場合はエラー
		if (!("1".equals(strArray[5])) && !("2".equals(strArray[5]))) {
			LOGGER.info(lineCnt + "行目： 6列目の性別が不正です。");
			return null;
		}

		// 7列目の桁数が不正な場合はエラー
		if (!StringUtils.isEmpty(strArray[6]) && strArray[6].length() > 1024) {
			LOGGER.info(lineCnt + "行目： 7列目の桁数が不正です。");
			return null;
		}

		// エラーがなければUserDataオブジェクトに変換し返す
		UserData userData = new UserData();
		userData.setId(Integer.parseInt(strArray[0]));
		userData.setName(strArray[1]);
		userData.setBirth_year(Integer.parseInt(strArray[2]));
		userData.setBirth_month(Integer.parseInt(strArray[3]));
		userData.setBirth_day(Integer.parseInt(strArray[4]));
		userData.setSex(strArray[5]);
		userData.setMemo(strArray[6]);
		return userData;
	}
}
