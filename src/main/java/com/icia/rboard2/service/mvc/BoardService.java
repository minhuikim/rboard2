package com.icia.rboard2.service.mvc;

import java.io.*;
import java.text.*;
import java.util.*;

import org.modelmapper.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.*;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.*;
import org.springframework.web.servlet.ModelAndView;

import com.icia.rboard2.dao.*;
import com.icia.rboard2.dto.*;
import com.icia.rboard2.entity.*;
import com.icia.rboard2.exception.*;
import com.icia.rboard2.util.*;


@Service
public class BoardService {
	@Autowired
	private BoardDao boardDao;
	@Autowired
	private CommentDao commentDao;
	@Autowired
	private AttachmentDao attachmentDao;
	@Autowired
	private ModelMapper modelMapper;
	@Autowired
	private SimpleDateFormat timeFormat;
	
	@Value("c:/upload/attachment")
	private String attachmentFolder;
	
	// 캐시 삭제 : 글을 작성하는 순간 1페이지의 캐시를 날린다. 
	@CacheEvict(cacheNames="list", key="1")
	public Integer write(BoardWriteDto dto) {
		Board board = modelMapper.map(dto, Board.class);
		List<MultipartFile> attachments = dto.getAttachments();
		
		// 첨부파일의 개수를 추가
		if(attachments!=null)
			board.setAttachmentCnt(attachments.size());
		else
			board.setAttachmentCnt(0);
		
		boardDao.insert(board);
		
		// 첨부파일을 저장하려면 bno값이 있어야 한다 -> boardDao.insert()를 먼저 실행
		if(attachments!=null) {
			for(MultipartFile a:attachments) {
				String originalFileName = a.getOriginalFilename();
				String saveFileName = System.currentTimeMillis() + "-" + originalFileName;
				Integer length = (int)a.getSize();
				Boolean isImage = a.getContentType().toLowerCase().startsWith("image/");
				String writer = dto.getWriter();
				Integer bno = board.getBno();
				Attachment attachment = Attachment.builder().bno(bno).writer(writer)
					.isImage(isImage).length(length).saveFileName(saveFileName)
					.originalFileName(originalFileName).build();
				File file = new File(attachmentFolder, saveFileName);
				try {
					FileCopyUtils.copy(a.getBytes(), file);
				} catch (IOException e) {
					e.printStackTrace();
				}
				attachmentDao.insert(attachment);
			}
		}
		return board.getBno();
	}

	public BoardResponseDto read(Integer bno, String username) {
		Board board = boardDao.findById(bno);
		if(board==null)
			throw new BoardNotFoundException();
		BoardResponseDto dto = modelMapper.map(board, BoardResponseDto.class);
		dto.setWriteTimeStr(timeFormat.format(board.getWriteTime()));
		if(board.getCommentCnt()>0)
			dto.setComments(commentDao.findAllByBno(bno));
		if(board.getAttachmentCnt()>0)
			dto.setAttachments(attachmentDao.findAllByBno(bno));
		
		// 조회수 증가 : 비로그인, 자기글을 읽었을 때 조회수 증가 X, 
		if(username!=null && username.equals(board.getWriter())==false)
			boardDao.update(Board.builder().bno(bno).readCnt(0).build());
		return dto;
	}
	
	// Cache를 추가하는데 페이지 번호를 키값으로 캐시를 구별
	@Cacheable(cacheNames="list", key="#pageno")
	public Page list(Integer pageno) {
		int countOfBoard = boardDao.countByWriter(null);
		Page page = PagingUtil.getPage(pageno, countOfBoard);
		int srn = page.getStartRowNum();
		int ern = page.getEndRowNum();
		
		List<Board> boardList = boardDao.findAll(srn, ern);
		List<ListResponseDto> dtoList = new ArrayList<>();
		for(Board board:boardList) {
			ListResponseDto dto = modelMapper.map(board, ListResponseDto.class);
			dto.setWriteTimeStr(timeFormat.format(board.getWriteTime()));
			dto.setAttachmentExist(board.getAttachmentCnt()>0);
			dtoList.add(dto);
		}
		page.setList(dtoList);
		return page;
	}

	public void update(Board board) {
		// 내가 작성한 글이어야만 업데이트 가능
		Board result = boardDao.findById(board.getBno());
		// 로그인한 사용자(board.getWriter())가 글쓴이(result.getWriter())인지 확인 -> 실패
		// 스프링은 작업이 실패했을 때 처리하는 전담 컨트롤러를 제공 -> @ControllerAdvice
		//		예외에 대해 응답
		if(result==null)
			throw new IllegalJobException();
		if(board.getWriter().equals(result.getWriter())==false) 
			throw new IllegalJobException();
		boardDao.update(board);
	}

	@Transactional
	public void delete(Integer bno, String name) {
		Board board = boardDao.findById(bno);
		if(board==null)
			throw new IllegalJobException();
		if(board.getWriter().equals(board.getWriter())==false) 
			throw new IllegalJobException();
		List<Attachment> list = attachmentDao.findAllByBno(bno);
		System.out.println(list.size());
		for(Attachment a:list) {
			System.out.println(a.getSaveFileName());
			File file = new File(attachmentFolder, a.getSaveFileName());
			System.out.println(file.exists());
			if(file.exists()==true)
				file.delete();
		}
		attachmentDao.deleteByBno(bno);
		commentDao.deleteByBno(bno);
		boardDao.deleteById(bno);
	}
}