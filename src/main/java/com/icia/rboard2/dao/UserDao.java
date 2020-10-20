package com.icia.rboard2.dao;

import org.mybatis.spring.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import com.icia.rboard2.entity.*;

@Repository
public class UserDao {
	@Autowired
	private SqlSessionTemplate tpl;
	
	public Integer insert(User user) {
		return tpl.insert("userMapper.insert", user);
	}

	public User findByCheckCode(String checkCode) {
		return tpl.selectOne("userMapper.findByCheckCode", checkCode);
	}
	
	public User findById(String username) {
		return tpl.selectOne("userMapper.findById", username);
	}
	
	public User findByEmail(String email) {
		return tpl.selectOne("userMapper.findByEmail", email);
	}

	public Integer update(User user) {
		return tpl.update("userMapper.update", user);
	}

	public Integer deleteById(String username) {
		return tpl.delete("userMapper.deleteById", username);
	}
	
	public Boolean existsById(String username) {
		return tpl.selectOne("userMapper.existsById", username);
	}

	public Boolean existsByEmail(String email) {
		return tpl.selectOne("userMapper.existsByEmail", email);
	}

	public Integer deleteByCheckCodeIsNotNull() {
		return tpl.delete("userMapper.deleteByCheckCodeIsNotNull");
	}
}