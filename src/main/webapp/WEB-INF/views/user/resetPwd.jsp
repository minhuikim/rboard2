<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
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
	$("#findPwd").on("click", function() {
		$("#findPwdFrm").submit();
	});
	$("#username").on("blur", function() {
		var username = $("#username").val().toUpperCase();
		 $("#username").val(username);
	});
});
</script>
</head>
<body>
	<form id="findPwdFrm" action="/rboard2/user/findPwd" method="post">
		<div class="form-group">
			<label for="username">아이디</label>
			<input id="username" type="text" name="username" class="form-control">
		</div>
		<div class="form-group">
			<label for="email">이메일</label>
			<input id="email" type="text" name="email" class="form-control">
		</div>
		<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
		<button type="button" class="btn btn-success" id="findPwd">비밀번호 찾기</button>
		</form>
</body>
</html>