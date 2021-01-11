package com.example.mybatis.model;

import lombok.Data;

@Data
public class UserPass {

	/** ID */
	private String id;
	
	/** パスワード */
	private String pass;
	
	/** パスワード(暗号化後) */
	private String passEncrypted;
	
}
