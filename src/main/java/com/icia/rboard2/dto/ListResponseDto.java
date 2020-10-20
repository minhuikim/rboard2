package com.icia.rboard2.dto;

import lombok.*;
import lombok.experimental.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain=true)
public class ListResponseDto {
	private Integer bno;
	private String title;
	private String writer;
	private String writeTimeStr;
	private Integer readCnt;
	private Integer commentCnt;
	private Boolean attachmentExist;
}
