package com.icia.rboard2.service.mvc;

import java.io.*;
import java.text.*;

import javax.mail.*;
import javax.mail.internet.*;

import org.apache.commons.lang3.*;
import org.modelmapper.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.mail.javamail.*;
import org.springframework.security.crypto.password.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.*;
import org.springframework.util.*;
import org.springframework.web.multipart.*;

import com.icia.rboard2.dao.*;
import com.icia.rboard2.dto.*;
import com.icia.rboard2.dto.UserDto.*;
import com.icia.rboard2.entity.*;

@Service
public class UserService {
	@Autowired
	private UserDao userDao;
	@Autowired
	private AuthorityDao authorityDao;
	@Autowired
	private ModelMapper modelMapper;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private SimpleDateFormat dateFormat;
	@Autowired
	private JavaMailSender javaMailSender;
	
	@Value("d:/upload/profile")
	private String profileUploadFolder;
	@Value("http://localhost:8081/profile/")
	private String profilePath;
	@Value("default.jpg")
	private String defaultProfile;
	
	@Transactional
	public void join(JoinRequestDto dto) {
		// dto를 user로 변환
		User user = modelMapper.map(dto, User.class);
		MultipartFile sajin = dto.getSajin();
		
		// 프사를 업로드한 경우 저장, 업로드하지 않은 경우 default.jpg
		if(sajin!=null && sajin.isEmpty()==false) {
			int lastIndexOfDot =  sajin.getOriginalFilename().lastIndexOf('.');
			String extension = sajin.getOriginalFilename().substring(lastIndexOfDot);
			String profile = user.getUsername() + extension;
			user.setProfile(profilePath + profile); 
			File profileFile = new File(profileUploadFolder, profile);
			try {
				FileCopyUtils.copy(sajin.getBytes(), profileFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else
			user.setProfile(profilePath + defaultProfile);
		
		// 랜덤문자열 20자리 checkCode 생성
		String checkCode = RandomStringUtils.randomAlphanumeric(20);
		user.setPassword(passwordEncoder.encode(user.getPassword())).setCheckCode(checkCode);
		
		// 비밀번호 암호화
		user.setPassword(passwordEncoder.encode(dto.getPassword()));
		
		// 가입 정보와 권한 정보 저장
		userDao.insert(user);
		for(String authority:dto.getAuthorities())
			authorityDao.insert(user.getUsername(), authority);
		
		// 가입 확인 이메일 메시지 작성
		String link = "<a href='http://localhost:8081/rboard2/user/joinCheck?checkCode=" + checkCode + "'>";
		StringBuffer stringBuffer = new StringBuffer("<p>회원가입을 위한 안내 메일입니다</p>");
		stringBuffer.append("<p>가입 확인을 위해 아래 링크를 클릭해 주세요</p>").append("<p>로그인 하기 : ").append(link).append("클릭하세요</a></p>");
		String emailMessage = stringBuffer.toString();
		
		// 이메일 발송 : 직접 발송이 아니라 gmail을 이용한 간접 발송이므로 발송이 실패해도 실제 예외가 전달되지는 않는다
		try {
			MimeMessage message = javaMailSender.createMimeMessage();
			MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, true, "UTF-8");
			mimeMessageHelper.setFrom(new InternetAddress("webmaster@icia.com"));
			mimeMessageHelper.setTo(user.getEmail());
			mimeMessageHelper.setSubject("가입 안내 메일입니다");
			mimeMessageHelper.setText(emailMessage, true);
			javaMailSender.send(message);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}

	public Boolean joinCheck(String checkCode) {
		User user = userDao.findByCheckCode(checkCode);
		if(user==null)
			return false;
		return userDao.update(User.builder().enabled(true).checkCode(checkCode).username(user.getUsername()).build())==1;
	}

	public ReadDto read(String username) {
		User user = userDao.findById(username);
		UserDto.ReadDto dto = modelMapper.map(user, UserDto.ReadDto.class);
		return dto.setJoindayStr(dateFormat.format(user.getJoinday())).setBirthdayStr(dateFormat.format(user.getJoinday()));
	}
	
	@Transactional
	public void resign(String username) {
		User user = userDao.findById(username);
		// http://localhost:8081/profile/spring11.jpg -> 마지막 /를 먼저 찾고 +1로 다음글자부터 가져온다.
		String profile = user.getProfile();
		String profileName = profile.substring(profile.lastIndexOf("/")+1);
		// 파일 삭제 : 프사 삭제, 첨부파일 삭제, (ck이미지 삭제)
		// ck 이미지를 저장할 때 임시폴더명으로 폴더를 생선한 다음 글을 쓰면 폴더명을 글번호로 변경???
		File file = new File(profileUploadFolder, profileName);
		if(file.exists()==true)
			file.delete();
		userDao.deleteById(username);
		authorityDao.deleteAllById(username);
	}

	public String findId(String irum, String email) { //아이디를 찾으면 아이디를 가져가야 하기때문에 String타입이어야 한다.
		User user = userDao.findByEmail(email);
		if(user!=null && user.getIrum().equals(irum))
			return user.getUsername();
		return null; 
	}

	public Boolean resetPwd(String username, String email) {
		//String password = userDao.findPasswordByUsernameAndEmail(username, email); Dao를 복잡하게 하지 않기 위해 findById로 통일
		User user = userDao.findById(username);
		if(user!=null && user.getEmail().equals(email)) {
			/*
			 && : 논리 곱
			 || : 논리 합
			 
			  */
			String newPassword = RandomStringUtils.randomAlphanumeric(20); //사용자 비밀번호에 접근할 수 없으므로 임시비밀번호 발급
			userDao.update(User.builder().username(username).password(passwordEncoder.encode(newPassword)).build());
			// 가입 확인 이메일 메시지 작성
			String link = "<a href='http://localhost:8081/rboard2/user/login'>";
			StringBuffer stringBuffer = new StringBuffer("<h1>임시 비밀번호 발급</h1>");
			stringBuffer.append("<p>임시비밀번호 : " + newPassword + "</p>");
			String emailMessage = stringBuffer.toString();
			
			// 이메일 발송 : 직접 발송이 아니라 gmail을 이용한 간접 발송이므로 발송이 실패해도 실제 예외가 전달되지는 않는다
			try {
				MimeMessage message = javaMailSender.createMimeMessage();
				MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, true, "UTF-8");
				mimeMessageHelper.setFrom(new InternetAddress("webmaster@icia.com"));
				mimeMessageHelper.setTo(user.getEmail());
				mimeMessageHelper.setSubject("임시비밀번호 발급 메일입니다");
				mimeMessageHelper.setText(emailMessage, true);
				javaMailSender.send(message);
			} catch (MessagingException e) {
				e.printStackTrace();
			}
			return true;
		}
		return false; //아이디나 비밀번호가 틀린경우 모두 실패
	}

	public Boolean pwdCheck(String password, String username) {
		User user = userDao.findById(username);
		if(user==null)
			return false;
		return passwordEncoder.matches(password, user.getPassword());
	}
}
