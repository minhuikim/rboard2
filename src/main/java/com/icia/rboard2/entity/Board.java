package com.icia.rboard2.entity;

import java.util.*;

import lombok.*;
import lombok.experimental.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain=true)
public class Board {
	private Integer bno;
	private String title;
	private String content;
	private String writer;
	private Date writeTime;
	private Integer readCnt;
	// 첨부파일 개수(*)
	private Integer attachmentCnt;
	// 댓글 개수
	private Integer commentCnt;
	// 삭제한 댓글 개수
	private Integer deleteCommentCnt;
	// 추천수
	private Integer goodCnt;
	// 비추천수
	private Integer badCnt;
}
