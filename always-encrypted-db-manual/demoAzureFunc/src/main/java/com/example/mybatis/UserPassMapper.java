package com.example.mybatis;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import com.example.mybatis.model.UserPass;
import com.example.util.MyBatisUtil;

@Repository
public class UserPassMapper {

	/**
	 * USER_PASSテーブルからデータを1件取得する.
	 * @return USER_PASSテーブルからの取得結果
	 */
	public UserPass selectOne() {
		SqlSession session = MyBatisUtil.getSqlSessionFactory().openSession();
		UserPass userPass = (UserPass)session.selectOne("selectOne");
		session.commit();
		session.close();
		return userPass;
	}
	
	/**
	 * USER_PASSテーブルのデータを更新する.
	 * @param userPass 更新対象のUSER_PASSテーブルの値
	 */
	public void updateUserPass(UserPass userPass) {
		SqlSession session = MyBatisUtil.getSqlSessionFactory().openSession();
		session.update("updateUserPass", userPass);
		session.commit();
		session.close();
	}
	
}
