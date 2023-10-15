package com.example.mybatis.model;

import lombok.Data;

@Data
public class UserData {

	/** ID */
	private String id;
	
	/** 名前 */
	private String name;
	
	/** 生年月日_年 */
	private String birth_year;
	
	/** 生年月日_月 */
	private String birth_month;
	
	/** 生年月日_日 */
	private String birth_day;
	
	/** 性別 */
	private String sex;
	
	/** メモ */
	private String memo;
	
}
