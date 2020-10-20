package com.icia.rboard2.dto;

import java.util.*;

import lombok.*;

@Data
@Builder
public class Page {
	// 아래 두 필드는 페이지네이션과 상관없다
	private int startRowNum;
	private int endRowNum;
	
	private int pageno;
	private int start;
	private int end;
	private int prev;
	private int next;
	
	List<ListResponseDto> list;
}
