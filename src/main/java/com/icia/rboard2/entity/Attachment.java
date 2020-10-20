package com.icia.rboard2.entity;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Attachment {
	// ano로 첨부파일 삭제 -> bno로 첨부파일 읽어서 화면 업데이트
	private Integer ano;
	private Integer bno;
	private String originalFileName;
	private String saveFileName;
	private String writer;
	private Integer length;
	private Boolean isImage;
}
