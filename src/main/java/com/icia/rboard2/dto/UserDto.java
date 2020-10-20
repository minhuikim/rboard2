package com.icia.rboard2.dto;

import java.util.*;

import org.springframework.web.multipart.*;

import lombok.*;
import lombok.experimental.*;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public class UserDto {
	@Data
	public static class JoinRequestDto {
		private String username;
		private String password;
		private String irum;
		private String email;
		private Date birthday;
		private List<String> authorities;
		private MultipartFile sajin;
	}
	
	@Data
	public static class UpdateRequestDto {
		private String username;
		private String irum;
		private String password;
		private String newPassword;
		private String email;
		//private String tel;
		private MultipartFile profile;
		//ㄴ없으면 url-encoded | 있으면 MultipartFile (Ajax로 파일 업로드 가능)
		// <form action="" method="" encType="multipart/form-data"> 파일 </form>
		// var params = { bno : 22, ano = 33 } 자바스크립트 객체를 보내면 자바가 못받으니까	ajax(jQuery)를 사용하면 자동으로 변환해준다.
		// 근데 multipartFile을 쓰면 processData:false, contentType:false 을 사용해서 자동변환을 꺼줘야한다.
		// $.ajax({ data:params, processData:false, contentType:false }); 
		// mvc : 백에서 프론트로 자바 객체를 보낸다.
	}
	
	@Data
	@Accessors(chain=true)
	public static class ReadDto {
		private String username;
		private String irum;
		private String tel;
		private String email;
		private String joindayStr;
		private String birthdayStr;
		private String profile;
		private Integer loginCnt;
		private Integer writeCnt;
	}

}
