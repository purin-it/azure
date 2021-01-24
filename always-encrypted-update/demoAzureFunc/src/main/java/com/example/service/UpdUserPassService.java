package com.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.model.UpdUserPassParam;
import com.example.model.UpdUserPassResult;
import com.example.mybatis.UserPassMapper;
import com.example.mybatis.model.UserPass;

@Service
public class UpdUserPassService {

	/** USER_PASSテーブルのデータを取得するMapperオブジェクト */
	@Autowired
	private UserPassMapper userPassMapper;

	/**
	 * USER_PASSテーブルのデータを更新し結果を返却する.
	 * @param updUserPassParam 更新用Param
	 * @return 結果情報オブジェクト
	 */
	public UpdUserPassResult updUserPass(UpdUserPassParam updUserPassParam) {
		UserPass userPass = new UserPass();
		userPass.setId("1");
		userPass.setPass(updUserPassParam.getUpdUserPass());
		userPass.setPassEncrypted(updUserPassParam.getUpdUserPass());
		userPassMapper.updateUserPass(userPass);
		
		return new UpdUserPassResult();
	}

}
