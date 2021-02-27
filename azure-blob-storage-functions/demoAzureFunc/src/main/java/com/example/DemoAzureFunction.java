package com.example;

import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.example.model.FileUploadParam;
import com.example.model.FileUploadResult;
import com.example.service.FileUploadService;

@SpringBootApplication
public class DemoAzureFunction {

	/** ファイルアップロードサービスクラスのオブジェクト */
	@Autowired
	private FileUploadService fileUploadService;

	public static void main(String[] args) throws Exception {
		SpringApplication.run(DemoAzureFunction.class, args);
	}

	/**
	 * ファイルアップロードを行い結果を返却する関数
	 * @return ファイルアップロードサービスクラスの呼出結果
	 */
	@Bean
	public Function<FileUploadParam, FileUploadResult> fileUpload() {
		return fileUploadParam -> fileUploadService.fileUpload(fileUploadParam);
	}
}
