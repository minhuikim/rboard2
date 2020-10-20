package com.icia.rboard2.dto;

import java.util.*;

import org.springframework.web.multipart.*;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BoardWriteDto {
	private String title;
	private String content;
	private String writer;
	
	private List<MultipartFile> attachments;
}
