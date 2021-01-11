package com.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.model.GetUserPassParam;
import com.example.model.GetUserPassResult;
import com.example.mybatis.UserPassMapper;

@Service
public class GetUserPassService {

	/** USER_PASSテーブルのデータを取得するMapperオブジェクト */
	@Autowired
	private UserPassMapper userPassMapper;

	/**
	 * USER_PASSテーブルのデータを取得し結果を返却する
	 * @param getUserPassParam 検索条件Param
	 * @return 結果情報オブジェクト
	 */
	public GetUserPassResult getUserPass(GetUserPassParam getUserPassParam) {
		GetUserPassResult getUserPassResult = new GetUserPassResult();
		getUserPassResult.setUserPass(userPassMapper.selectOne());
		return getUserPassResult;
	}

}
