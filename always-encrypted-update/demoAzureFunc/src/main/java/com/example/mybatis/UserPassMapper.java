package com.example.mybatis;

import org.apache.ibatis.annotations.Mapper;

import com.example.mybatis.model.UserPass;

@Mapper
public interface UserPassMapper {

	/**
	 * USER_PASSテーブルからデータを1件取得する.
	 * @return USER_PASSテーブルからの取得結果
	 */
	UserPass selectOne();
	
	/**
	 * USER_PASSテーブルのデータを更新する.
	 * @param userPass 更新対象のUSER_PASSテーブルの値
	 */
	void updateUserPass(UserPass userPass);
	
}
