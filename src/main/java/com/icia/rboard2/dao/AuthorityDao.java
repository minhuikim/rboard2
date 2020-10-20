package com.icia.rboard2.dao;

import org.apache.ibatis.annotations.*;

public interface AuthorityDao {
	@Insert("insert into authorities values(#{username}, #{authority})")
	public void insert(@Param("username") String username, @Param("authority") String authority);

	@Delete("delete from authorities where username=#{username}")
	public void deleteAllById(@Param("username") String username);
}
