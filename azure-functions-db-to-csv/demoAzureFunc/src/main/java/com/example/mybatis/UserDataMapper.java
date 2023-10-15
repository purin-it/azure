package com.example.mybatis;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import com.example.mybatis.model.UserData;

@Mapper
public interface UserDataMapper {
	
	/**
	 * USER_DATAテーブルのデータを全件取得する.
	 * @return USER_DATAテーブルの全データ
	 */
	List<UserData> findAll();

}
