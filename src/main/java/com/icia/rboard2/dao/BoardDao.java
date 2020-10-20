package com.icia.rboard2.dao;

import java.util.*;

import org.apache.ibatis.annotations.*;

import com.icia.rboard2.entity.*;

public interface BoardDao {
	public Integer countByWriter(@Param("writer") String writer);

	public List<Board> findAll(@Param("startRowNum") Integer srn,  @Param("endRowNum") Integer ern);

	public Board findById(@Param("bno") Integer bno);

	public Integer insert(@Param("b") Board board);

	public Integer update(@Param("b") Board board);

	public Integer deleteById(@Param("bno") Integer bno);

}
