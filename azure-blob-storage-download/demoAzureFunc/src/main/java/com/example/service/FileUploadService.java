package com.example.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.model.FileUploadParam;
import com.example.model.FileUploadResult;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.BlobOutputStream;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;

@Service
public class FileUploadService {

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
	 * ファイルアップロード処理を行うサービス.
	 * @param fileUploadParam ファイルアップロード用Param
	 * @return ファイルアップロードサービスクラスの呼出結果
	 */
	public FileUploadResult fileUpload(FileUploadParam fileUploadParam) {		
		// ファイルアップロード処理
		try {
			// Blobストレージへの接続文字列
			String storageConnectionString = "DefaultEndpointsProtocol=https;" + "AccountName=" + storageAccountName
							+ ";" + "AccountKey=" + storageAccessKey + ";";

			// ストレージアカウントオブジェクトを取得
			CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);

			// Blobクライアントオブジェクトを取得
			CloudBlobClient blobClient = storageAccount.createCloudBlobClient();

			// Blob内のコンテナーを取得
			CloudBlobContainer container = blobClient.getContainerReference(storageContainerName);

			// Blob内のコンテナーにデータを書き込む
			CloudBlockBlob blob = container.getBlockBlobReference(fileUploadParam.getFileName());
			BlobOutputStream output = blob.openOutputStream();
			output.write(fileUploadParam.getFileData());
			output.close();

		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}

		FileUploadResult result = new FileUploadResult();
		result.setMessage("ファイルアップロードが完了しました。");
		return result;
	}
}
