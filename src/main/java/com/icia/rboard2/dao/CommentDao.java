package com.icia.rboard2.dao;

import java.util.*;

import org.apache.ibatis.annotations.*;

import com.icia.rboard2.entity.*;

public interface CommentDao {
	@Select("select bno, cno, writer, content, profile, write_time writeTime from comments where bno=#{bno} order by cno desc")
	public List<Comment> findAllByBno(@Param("bno") Integer bno);

	@Delete("delete from comments where bno=#{bno}")
	public Integer deleteByBno(@Param("bno") Integer bno);

	@Insert("insert into comments values(comment_seq.nextval, #{c.bno}, #{c.writer}, #{c.content}, sysdate, #{c.profile})")
	public Integer insert(@Param("c") Comment comment);

	@Delete("delete from comments where cno=#{cno} and rownum=1")
	public Integer deleteById(@Param("cno") Integer cno);
	
	@Select("select bno, cno, writer, content, profile, write_time writeTime from comments where cno=#{cno} and rownum=1")
	public Comment findById(@Param("cno") Integer cno);

}
