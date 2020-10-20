package com.icia.rboard2.dao;

import java.util.*;

import org.apache.ibatis.annotations.*;

import com.icia.rboard2.entity.*;

public interface AttachmentDao {
	@Insert("insert into attachment values(attachment_seq.nextval,#{a.bno},#{a.writer},#{a.originalFileName},#{a.saveFileName},#{a.length},#{a.isImage})")
	public void insert(@Param("a") Attachment attachment);

	@Select("select ano, bno, writer, original_file_name originalFileName, save_file_name saveFileName, length, is_image isImage from attachment where bno=#{bno}")
	public List<Attachment> findAllByBno(@Param("bno") Integer bno);

	@Select("select ano, bno, writer, original_file_name originalFileName, save_file_name saveFileName, length, is_image isImage from attachment where ano=#{ano} and rownum=1")
	public Attachment findById(@Param("ano") Integer ano);

	@Delete("delete from attachment where ano=#{ano} and rownum=1")
	public void deleteById(@Param("ano") Integer ano);
	
	@Delete("delete from attachment where bno=#{bno}")
	public void deleteByBno(@Param("bno") Integer bno);

}








