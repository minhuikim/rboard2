package com.icia.rboard2;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.*;

import org.junit.*;
import org.junit.runner.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.test.context.*;
import org.springframework.test.context.junit4.*;
import org.springframework.transaction.annotation.*;

import com.icia.rboard2.dao.*;
import com.icia.rboard2.entity.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "file:src/main/webapp/WEB-INF/spring/**/*-context.xml")
public class BoardDaoTest {
	@Autowired
	private BoardDao dao;
	
	//@Test
	public void initTest() {
		assertThat(dao, is(notNullValue()));
	}
	
	@Test
	public void insertAllTest() {
		List<String> writers = Arrays.asList("spring11", "summer", "fall", "winter");
		for(int i=0; i<123; i++) {
			int index = i%4;
			String writer = writers.get(index);
			Board board = Board.builder().title(index+"의 연습글").content("내용이 있겠음?").writer(writer).attachmentCnt(0).build();
			dao.insert(board);
		}
	}
	
	//@Test
	public void findAllTest() {
		int count = dao.countByWriter(null);
		assertThat(count, is(123));
		List<Board> list = dao.findAll(11, 20);
		assertThat(list.size(), is(10));
	}
	
	//@Test
	public void insertAndFindByIdTest() {
		Board result = dao.findById(11);
		assertThat(result.getBno(), is(11));
	}
	
	@Transactional
	//@Test
	public void updateTest1() {
		// 글을 읽는 다면
		Board b1 = Board.builder().bno(1).readCnt(0).build();
		assertThat(dao.update(b1), is(1));
		assertThat(dao.findById(1).getReadCnt(), is(1));	
	}
	
	@Transactional
	//@Test
	public void updateTest2() {
		// 글을 변경한다면
		Board b2 = Board.builder().title("변경했어요").content("변경했다니까요").bno(1).build();
		assertThat(dao.update(b2), is(1));	
	}
	
	@Transactional
	//@Test
	public void updateTest3() {
		// 댓글을 달면
		Board b3 = Board.builder().bno(1).commentCnt(5).build();
		assertThat(dao.update(b3), is(1));
		assertThat(dao.findById(1).getCommentCnt(), is(5));	
	}
	
	@Transactional
	//@Test
	public void updateTest4() {
		// 댓글을 지우면
		Board b4 = Board.builder().bno(1).commentCnt(4).deleteCommentCnt(0).build();
		assertThat(dao.update(b4), is(1));
		assertThat(dao.findById(1).getCommentCnt(), is(4));
		assertThat(dao.findById(1).getDeleteCommentCnt(), is(1));	
	}
	
	@Transactional
	//@Test
	public void updateTest5() {
		// 글을 추천
		Board b5= Board.builder().bno(1).goodCnt(0).build();
		assertThat(dao.update(b5), is(1));
		assertThat(dao.findById(1).getGoodCnt(), is(1));
		
		// 글을 비추
		Board b6= Board.builder().bno(1).badCnt(0).build();
		assertThat(dao.update(b6), is(1));
		assertThat(dao.findById(1).getBadCnt(), is(1));
	}
	
	@Transactional
	//@Test
	public void deleteTest() {
		assertThat(dao.deleteById(11), is(1));
		assertThat(dao.findById(11), is(nullValue()));
	}
}