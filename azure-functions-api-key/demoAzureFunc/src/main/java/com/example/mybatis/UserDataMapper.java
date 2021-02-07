package com.example.mybatis;

import java.util.ArrayList;
import org.apache.ibatis.annotations.Mapper;
import com.example.model.SearchForm;
import com.example.mybatis.model.UserData;

@Mapper
public interface UserDataMapper {

	/**
	 * 引数の検索条件に合うユーザーデータを取得し結果を返却する
	 * @param searchForm 検索条件Form
	 * @return ユーザーデータリスト
	 */
	ArrayList<UserData> searchUserDataList(SearchForm searchForm);

}
