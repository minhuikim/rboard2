package com.icia.rboard2.entity;

import java.util.*;

import com.fasterxml.jackson.annotation.*;

import lombok.*;
import lombok.experimental.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain=true)
public class Comment {
	private Integer cno;
	private Integer bno;
	private String writer;
	private String content;
	// 댓글 작성자의 프사
	private String profile;
	@JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
	private Date writeTime;
}




