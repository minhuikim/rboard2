<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>    
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<script>
$(function() {
	var msg = "${msg}";
	if(msg!="") {
		$("#alert").text(msg);
		$("#msg").show();
	}
	
	$("#pwdCheck").on("click", function() {
		$("#pwdCheckFrm").submit();
	});
});
</script>
</head>
<body>
	<div class="alert alert-danger alert-dismissible" id="msg" style="display:none;">
    	<a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a>
    	<strong>서버 메시지 </strong><span id="alert"></span>
  	</div>
	<form id="pwdCheckFrm" action="/rboard2/user/pwdCheck" method="post">
		<div class="form-group">
			<label for="password">비밀번호</label>
			<input id="password" type="password" name="password" class="form-control">
			<span class="helper-text" id="password_msg"></span>
		</div>
		<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
		<button type="button" class="btn btn-success" id="pwdCheck">비밀번호 확인</button>
	</form>
</body>
</html>


