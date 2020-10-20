package com.icia.rboard2.dto;

import java.util.*;

import com.icia.rboard2.entity.*;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BoardResponseDto {
	private Integer bno;
	private String title;
	private String content;
	private String writer;
	// java.util.Date를 문자열로 변경해서 출력할 필드
	private String writeTimeStr;
	private Integer readCnt;
	private Integer commentCnt;
	private Integer deleteCommentCnt;
	private Integer goodCnt;
	private Integer badCnt;
	// 댓글들의 목록을 출력할 필드
	private List<Comment> comments;
	private List<Attachment> attachments;
}




