package com.example.mybatis;

import org.apache.ibatis.annotations.Mapper;

import com.example.mybatis.model.UserPass;

@Mapper
public interface UserPassMapper {

	UserPass selectOne();
	
}
