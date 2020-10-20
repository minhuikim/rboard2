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
	$("#findId").on("click", function() {
		$("#findIdFrm").submit();
	});
});
</script>
</head>
<body>
	<div class="alert alert-danger alert-dismissible" id="msg" style="display:none;">
    	<a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a>
    	<strong>서버 메시지 </strong><span id="alert"></span>
  	</div>
	<form id="findIdFrm" action="/rboard2/user/findId" method="post">
		<div class="form-group">
			<label for="irum">이름</label>
			<input id="irum" type="text" name="irum" class="form-control">
			<span class="helper-text" id="irum_msg"></span>
		</div>
		<div class="form-group">
			<label for="email">이메일</label>
			<input id="email" type="text" name="email" class="form-control">
			<span class="helper-text" id="email_msg"></span>
		</div>
		<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
		<button type="button" class="btn btn-success" id="findId">아이디 찾기</button>
		</form>
</body>
</html>
