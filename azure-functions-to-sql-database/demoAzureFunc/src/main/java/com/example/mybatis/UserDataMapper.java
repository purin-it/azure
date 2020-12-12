package com.example.mybatis;

import org.apache.ibatis.annotations.Mapper;
import com.example.mybatis.model.UserData;

@Mapper
public interface UserDataMapper {

	/**
	 * 引数のIDに対応するユーザーデータをDBから取得する
	 * @param id ID
	 * @return DBのユーザーデータ
	 */
	UserData getUserData(int id);
	
}
