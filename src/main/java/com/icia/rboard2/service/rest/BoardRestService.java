package com.icia.rboard2.service.rest;

import java.io.*;
import java.util.*;

import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;
import org.springframework.util.*;
import org.springframework.web.multipart.*;

import com.fasterxml.jackson.databind.*;
import com.icia.rboard2.dao.*;
import com.icia.rboard2.entity.*;
import com.icia.rboard2.exception.*;

@Service
public class BoardRestService {
	@Autowired
	private BoardDao boardDao;
	@Autowired
	private CommentDao commentDao;
	@Autowired
	private UserBoardDao userBoardDao;
	@Autowired
	private AttachmentDao attachmentDao;
	
	@Value("c:/upload/image")	
	private String ckImageFolder;
	@Value("http://localhost:8081/image/")
	private String ckImagePath;
	@Value("c:/upload/attachment")
	private String attachmentFolder;
	
	@Autowired
	private ObjectMapper objectMapper;	
	
	@Transactional
	public Integer good(Integer bno, String username) {
		if(userBoardDao.existsById(bno, username)==true) 
			return boardDao.findById(bno).getGoodCnt();
		userBoardDao.insert(bno, username);
		boardDao.update(Board.builder().bno(bno).goodCnt(0).build());
		return boardDao.findById(bno).getGoodCnt();
	}

	public Integer bad(Integer bno, String username) {
		if(userBoardDao.existsById(bno, username)==true) 
			return boardDao.findById(bno).getBadCnt();
		boardDao.update(Board.builder().bno(bno).badCnt(0).build());
		userBoardDao.insert(bno, username);
		return boardDao.findById(bno).getBadCnt();
	}
	
	public List<Comment> writeComment(Comment comment) {
		// 글 읽기, 댓글 추가, 댓글의 개수를 증가, 댓글 다시 읽기
		Board board = boardDao.findById(comment.getBno());
		comment.setProfile("http://localhost:8081/profile/default.jpg");
		commentDao.insert(comment);
		boardDao.update(Board.builder().bno(board.getBno()).commentCnt(board.getCommentCnt()+1).build());
		return commentDao.findAllByBno(comment.getBno());
	}

	public List<Comment> deleteComment(Integer cno, Integer bno, String username) {
		// 여기 수정 : 글 읽기, 댓글 읽기, 댓글 삭제, 댓글의 개수 감소, 삭제한 댓글 개수 증가, 댓글 다시 읽기
		Comment comment = commentDao.findById(cno);
		if(comment.getWriter().equals(username)==false)
			throw new IllegalJobRestException();
		Board board = boardDao.findById(bno);
		commentDao.deleteById(cno);
		if(board.getCommentCnt()>0) {
			boardDao.update(Board.builder().bno(bno).commentCnt(board.getCommentCnt()-1).deleteCommentCnt(-1111).build());
		}
		return commentDao.findAllByBno(bno);
	}
	
	// 출력형식: {"uploaded":1, "fileName":파일명, "url":주소}
	public String ckImageUpload(MultipartFile upload) {
		Map<String, Object> map = new HashMap<String,Object>();
		if(upload!=null) {
			// mime type
			// 문서 형식 : text/html, text/plain, application/excel, image/jpg, image/gif...
			if(upload.getContentType().toLowerCase().startsWith("image/")) {
				// 사진 이름을 변경
				String imageName = System.currentTimeMillis() + ".jpg";
				// 사진을 저장하기 위해 파일 객체를 생성 -> 없으면 생성, 있으면 덮어쓴다
				// c:/upload/image/1111112222.jpg
				File file = new File(ckImageFolder, imageName);
				try {
					FileCopyUtils.copy(upload.getBytes(), file);
					// localhost:8081/image/1111112222.jpg
					String url = ckImagePath + imageName;
					map.put("uploaded", 1);
					map.put("fileName", imageName);
					map.put("url", url);
					String json = objectMapper.writeValueAsString(map);
					return json;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	
	public Attachment readAttachment(Integer ano) {
		return attachmentDao.findById(ano);
	}

	public List<Attachment> deleteAttachment(Integer ano, Integer bno, String username) {
		Attachment attachment = attachmentDao.findById(ano);
		if(attachment.getWriter().equals(username)==false)
			throw new IllegalJobRestException();
		attachmentDao.deleteById(ano);
		boardDao.update(Board.builder().bno(bno).attachmentCnt(0).build());
		File file = new File(attachmentFolder, attachment.getSaveFileName());
		if(file.exists()==true)
			file.delete();
		return attachmentDao.findAllByBno(bno);
	}
}
