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
public class CommentDaoTest {
	@Autowired
	private CommentDao dao;
	
	//@Test
	public void initTest() {
		assertThat(dao, is(notNullValue()));
	}
	
	//@Test
	public void insertTest() {
		Comment c = new Comment(0, 11, "spring11", "안녕하세요", "http://localhost:8081/profile/default.jpg", new Date());
		assertThat(dao.insert(c), is(1));
	}
	
	//@Test
	public void findAllByBnoTest() {
		assertThat(dao.findAllByBno(11).size(), is(1));
	}
	
	@Transactional
	//@Test
	public void deleteByIdTest() {
		assertThat(dao.deleteById(1), is(1));
	}
	
	@Transactional
	@Test
	public void deleteByBnoTest() {
		assertThat(dao.deleteByBno(11), is(1));
	}
}
