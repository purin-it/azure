package com.example.util;

import java.io.IOException;
import java.io.Reader;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

public class MyBatisUtil {

	/** SqlSessionファクトリ */
	private static SqlSessionFactory sqlSessionFactory = null;
	
	static {
		try {
			// MyBatisの定義ファイルを読み込む
			Reader reader = Resources.getResourceAsReader("mybatis-config.xml");
			
			// SqlSessionファクトリを生成する
			// 第2引数には、MyBatisの定義ファイルの接続するDBを識別するidを指定する
			sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader, "sqldb");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * SqlSessionファクトリを返却する
	 * @return SqlSessionファクトリ
	 */
	public static SqlSessionFactory getSqlSessionFactory(){
		return sqlSessionFactory;
	}
}
