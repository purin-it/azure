package com.example.mybatis.batch;

import org.apache.ibatis.annotations.Mapper;
import com.example.mybatis.model.UserData;

@Mapper
public interface UserDataMapperBatch {

	/**
	 * 引数のIDをキーにもつUserDataオブジェクトを行ロックをかけ取得する.
	 * @param id ID
	 * @return UserDataオブジェクト
	 */
	UserData findByIdRowLock(Integer id);
	
	/**
	 * 引数のUserDataオブジェクトのバージョンを更新する.
	 * @param userData UserDataオブジェクト
	 */
	void updateVersion(UserData userData);

	/**
	 * 引数のIDをキーにもつUserDataオブジェクトを取得する.
	 * @param id ID
	 * @return UserDataオブジェクト
	 */
	UserData findById(Integer id);
	
	/**
	 * 引数のUserDataオブジェクトをDBに追加する.
	 * @param userData UserDataオブジェクト
	 */
	void insert(UserData userData);
	
	/**
	 * DBにある引数のUserDataオブジェクトを更新する.
	 * @param userData UserDataオブジェクト
	 */
	void update(UserData userData);
}
