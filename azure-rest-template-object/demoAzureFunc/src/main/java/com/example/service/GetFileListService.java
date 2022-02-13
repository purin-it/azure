package com.example.service;

import java.io.InputStream;
import java.util.ArrayList;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.model.FileData;
import com.example.model.GetFileListParam;
import com.example.model.GetFileListResult;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import com.microsoft.azure.storage.blob.ListBlobItem;

@Service
public class GetFileListService {

	/** Azure Storageのアカウント名 */
	@Value("${azure.storage.accountName}")
	private String storageAccountName;

	/** Azure Storageへのアクセスキー */
	@Value("${azure.storage.accessKey}")
	private String storageAccessKey;

	/** Azure StorageのBlobコンテナー名 */
	@Value("${azure.storage.containerName}")
	private String storageContainerName;

	/**
	 * ファイルリスト取得処理を行うサービス.
	 * @param getFileListParam ファイル取得用Param
	 * @return ファイルリスト取得サービスクラスの呼出結果
	 */
	public GetFileListResult getFileList(GetFileListParam getFileListParam) {
		GetFileListResult result = new GetFileListResult();

		// ファイルアップロード処理
		try {
			// Blobストレージへの接続文字列
			String storageConnectionString = "DefaultEndpointsProtocol=https;" 
			        + "AccountName=" + storageAccountName + ";" 
					+ "AccountKey=" + storageAccessKey + ";";

			// ストレージアカウントオブジェクトを取得
			CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);

			// Blobクライアントオブジェクトを取得
			CloudBlobClient blobClient = storageAccount.createCloudBlobClient();

			// Blob内のコンテナーを取得
			CloudBlobContainer container = blobClient.getContainerReference(storageContainerName);

			// Blob情報を取得し、戻り値に設定する
			ArrayList<FileData> fileDataList = new ArrayList<>();
			int index = 1;
			for (ListBlobItem blobItem : container.listBlobs()) {
				// ファイル名・ファイルパスを取得
				String filePath = blobItem.getUri().toString();
				String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);

				// Blobデータを読み込み
				CloudBlockBlob blob = container.getBlockBlobReference(fileName);
				InputStream input = blob.openInputStream();

				// ファイルデータを設定
				FileData fileData = new FileData();
				fileData.setId(index);
				fileData.setFileName(fileName);
				fileData.setFilePath(filePath);
				fileData.setFileData(ArrayUtils.toObject(IOUtils.toByteArray(input)));
				fileDataList.add(fileData);
				index++;
			}
			result.setFileDataList(fileDataList);

		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}

		return result;
	}
}
