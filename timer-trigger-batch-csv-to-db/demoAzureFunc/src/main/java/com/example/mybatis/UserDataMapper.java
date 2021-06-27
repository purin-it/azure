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

}
