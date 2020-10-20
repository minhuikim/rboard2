package com.icia.rboard2.entity;

import java.util.*;

import org.springframework.format.annotation.*;

import lombok.*;
import lombok.experimental.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain=true)
@Builder
public class User {
	private String username;
	private String password;
	private String irum;
	private String email;
	private Date joinday;
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date birthday;
	// 로그인 실패 횟수 : 5회 이상 실패할 경우 사용자 비활성화
	private Integer loginFailureCnt;
	private Integer loginCnt;
	private Integer writeCnt;
	// enabled가 false일 경우 사용자 비활성화. 로그인해도 아무것도 할 수 없다
	private Boolean enabled;
	// 프로필 사진을 볼 수 있는 경로
	private String profile;
	// 가입을 신청하면 이메일로 보내질 인증 코드
	private String checkCode;
}

