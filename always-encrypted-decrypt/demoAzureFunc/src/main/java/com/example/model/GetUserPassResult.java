package com.example.model;

import com.example.mybatis.model.UserPass;

import lombok.Data;

@Data
public class GetUserPassResult {

	/** USER_PASSテーブルからの取得結果 */
	private UserPass userPass;
	
}
