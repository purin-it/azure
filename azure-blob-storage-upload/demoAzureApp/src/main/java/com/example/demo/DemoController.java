package com.example.demo;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.BlobOutputStream;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;

@Controller
public class DemoController {

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
	 * メイン画面を初期表示する.
	 * 
	 * @param model Modelオブジェクト
	 * @return メイン画面
	 */
	@GetMapping("/")
	public String index(Model model) {
		model.addAttribute("message", "アップロードするファイルを指定し、アップロードボタンを押下してください。");
		return "main";
	}

	/**
	 * ファイルデータをAzure Blob Storageに登録する.
	 * 
	 * @param uploadFile アップロードファイル
	 * @param model      Modelオブジェクト
	 * @return メイン画面
	 */
	@PostMapping("/upload")
	public String add(@RequestParam("upload_file") MultipartFile uploadFile, Model model) {
		// ファイルが未指定の場合はエラーとする
		if (uploadFile == null || StringUtils.isEmptyOrWhitespace(uploadFile.getOriginalFilename())) {
			model.addAttribute("errMessage", "ファイルを指定してください。");
			return "main";
		}

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
			CloudBlockBlob blob = container.getBlockBlobReference(uploadFile.getOriginalFilename());
			BlobOutputStream output = blob.openOutputStream();
			output.write(IOUtils.toByteArray(uploadFile.getInputStream()));
			output.close();

		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}

		// メイン画面へ遷移
		model.addAttribute("message", "ファイルアップロードが完了しました。");
		return "main";
	}
}