package com.icia.rboard2.service.rest;

import java.io.File;
import java.io.IOException;

import org.modelmapper.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.scheduling.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.*;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import com.icia.rboard2.dao.*;
import com.icia.rboard2.dto.*;
import com.icia.rboard2.entity.*;
import com.icia.rboard2.exception.IllegalJobException;

@Service
public class UserRestService {
	@Autowired
	private UserDao userDao;
	@Autowired
	private ModelMapper modelMapper;
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Value("d:/upload/profile")
	private String profileUploadFolder;
	@Value("http://localhost:8081/profile/")
	private String profilePath;
	@Value("default.jpg")
	private String defaultProfile;
	
	public Boolean idCheck(String username) {
		return !userDao.existsById(username);
	}

	public Boolean emailCheck(String email) {
		return !userDao.existsByEmail(email);
	}
	
	public Boolean update(UserDto.UpdateRequestDto dto) {
		User user = modelMapper.map(dto, User.class);
		MultipartFile sajin = dto.getProfile();
		// <input type="file" name="sajin">
		// join(User user, MultipartFile sajin) -> sajin은 절대 null이 아니다
		//										-> sajin, isEmpty()
		// join(JoinRequestDto dto)
		//		@ModelAttribute로 받을 경우 업로드 안하면 sajin이 null이다
		if(sajin!=null && sajin.isEmpty()==false) {
			int lastIndexOfDot = sajin.getOriginalFilename().lastIndexOf('.');
			String extension = sajin.getOriginalFilename().substring(lastIndexOfDot);
			String profile = user.getUsername() + extension;
			user.setProfile(profilePath + profile); 
			File profileFile = new File(profileUploadFolder, profile);
			try {
				FileCopyUtils.copy(sajin.getBytes(), profileFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} 
		
		if(dto.getNewPassword()!=null) {
			String newPassword = dto.getNewPassword();
			user.setPassword(passwordEncoder.encode(newPassword));
		}
		return userDao.update(user)==1;
	}
	
	// 일정 간격 또는 일정 시간에 메소드를 실행
	// 크론식 : 몇 시분초에 (점검)을 시작하도록 자동으로 메세지를 띄우게 설정한다.
	//		    원래는 리눅스에서 온거다
	//		    크론식 메이커 검색해서 사용
	// Spring Scheduler, Spring Batch
	// 초 분 시 일 월 요일
	@Scheduled(cron="0 0 4 ? * THU") 	//매주 목요일 새벽 4시에
	public void deleteUncheckUser() {	//가입확인하지 않은 가입 신청을 삭제해라
		userDao.deleteByCheckCodeIsNotNull();
	}
	
	// 날짜 계산 : java.util.date는 날짜 계산기능이 굉장히 약하기 때문에
	// 			계산기능이 필요하다면 LocalDate, LocalDateTime을 사용한다.
	//			(글을 쓴지 6개월이 지난 글의 첨부파일은 삭제한다 등)
	// 양력외의 날짜를 사용하고 싶은데 util.date에는 양력밖에 없어서 -> Calendar인터페이스를 사용(망함)
	// 	-> JodaTime -> LocalDate, LocalDateTime(발전되는중(mvc,mybatis등은 지원이 부실하다))
	
//	@Scheduled(cron="0 0/1 * 1/1 * ? ")
	public void printHello() {
		System.out.println("서버의 출력입니다");
	}
}
