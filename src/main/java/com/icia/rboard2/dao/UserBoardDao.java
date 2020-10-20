package com.icia.rboard2.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface UserBoardDao {
	@Select("select count(*) from users_board where bno=#{bno} and username=#{username} and rownum=1")
	public Boolean existsById(@Param("bno") Integer bno, @Param("username") String username);

	@Insert("insert into users_board values(#{username}, #{bno})")
	public Integer insert(@Param("bno") Integer bno, @Param("username") String username);

}
