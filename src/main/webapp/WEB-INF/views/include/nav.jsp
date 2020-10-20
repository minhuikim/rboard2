<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<script>
$(function() {
	$("#logout").on("click", function() {
		var $input = $("<input>").attr("type","hidden").attr("name","_csrf").val('${_csrf.token}');
		$("<form>").attr("action","/rboard2/user/logout").attr("method","post").append($input).appendTo("body").submit();
	});
})
</script>
</head>
<body>
<div id="nav" class="navbar navbar-inverse">
	<div class="container-fluid">
		<div class="navbar-header">
			<a class="navbar-brand" href="/rboard2">ICIA</a>
		</div>
		<ul class="nav navbar-nav" id="menu_parent">
			<sec:authorize access="isAnonymous()">
				<li><a href="/rboard2/user/findId">아이디 찾기</a></li>
				<li><a href="/rboard2/user/findPwd">비번 찾기</a></li>
				<li><a href="/rboard2/user/join">회원가입</a></li>
				<li><a id='login' href='/rboard2/user/login'>로그인</a></li>
			</sec:authorize>
          	<sec:authorize access="isAuthenticated()">
          		<li><a href='/rboard2/user/pwdCheck'>내정보</a></li>
          		<li><a href='/rboard2/board/write'>글쓰기</a></li>
				<li><a id='logout' href='#'>로그아웃</a></li>
			</sec:authorize>
		</ul>
	</div>

</div>
</body>
</html>