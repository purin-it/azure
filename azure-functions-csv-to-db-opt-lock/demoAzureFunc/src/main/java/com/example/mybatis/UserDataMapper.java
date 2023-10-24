package com.example.mybatis;

import org.apache.ibatis.annotations.Mapper;
import com.example.mybatis.model.UserData;

@Mapper
public interface UserDataMapper {

	/**
	 * DBにUserDataオブジェクトがあれば更新し、なければ追加する.
	 * @param userData UserDataオブジェクト
	 */
	void upsert(UserData userData);

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
}
