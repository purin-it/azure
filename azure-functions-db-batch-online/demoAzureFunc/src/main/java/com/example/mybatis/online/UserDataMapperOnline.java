package com.example.mybatis.online;

import org.apache.ibatis.annotations.Mapper;

import com.example.mybatis.model.UserData;

@Mapper
public interface UserDataMapperOnline {

	/**
	 * 引数のIDをキーにもつUserDataオブジェクトのバージョン番号を更新する.
	 * @param id ID
	 * @return 更新件数
	 */
	int updateVersion(Integer id);
	
	/**
	 * 引数のIDをキーにもつUserDataオブジェクトを取得する.
	 * @param id ID
	 * @return UserDataオブジェクト
	 */
	UserData findById(Integer id);
}
