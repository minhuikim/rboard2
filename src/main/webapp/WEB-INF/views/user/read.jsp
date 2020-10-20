<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>    
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<style>
	#user td {
		height: 60px;
		line-height: 60px;
	}
	
	#user td input {
		height: 25px;
	}
	
	#tel1, #tel2, #tel3 {
		width: 125px;
	}
	
	.first {
		background-color: #f3f3f3;
		text-align: center;
	}
	#profile_sajin {
		line-height: 25px;
	}
	.key {
		width: 35%;
		display: inline-block;
	}
</style>
<script>
//하드디스크에 있는 사진을 읽어와 출력하는 함수
function loadImage() {
	// $로 html요소를 선택하면 jQuery가 html요소에 이런 저런 메소드와 필드를 추가한 객체 생성 : jQuery객체에서 순수한 html 부분을 꺼내려면 [0]
	var file = $("#profile")[0].files[0];	
	var maxSize = 1024*1024; // 1MB

	// 파일 크기가 1MB가 넘어가면 오류 처리
	if(file.size>maxSize) {
		Swal.fire({
			icon: 'error',
		  	title: '크기 오류',
			text: '파일크기는 1MB를 넘을 수 없습니다'
		});
		$("#sajin").val("");
		return false;
	}
	
	// 이하 하드디스크에 있는 이미지 파일을 로딩해 화면에 출력하는 코드
	var reader = new FileReader();
	reader.onload = function(e) {
		$("#show_profile").attr("src", e.target.result);
	}
	reader.readAsDataURL(file);
	return true;
}

function makePage() {
	$("#passwordArea").hide();
	
	var email = "${user.email}";
	var tel = "${user.tel}";
	
	// 이 메일을 @기준으로 분리
	var indexOfAt = email.indexOf("@");
	var email1 = email.substr(0, indexOfAt);
	var email2 = email.substr(indexOfAt+1);
	$("#email1").val(email1);
	$("#email2").val(email2).prop("disabled", true);
	
	// email2에 따라 selectEmail에 같은 이메일 사이트 출력
	var $options = $("#selectEmail option"); 
	
	// jQuery each는 jQuery 객체용과 범용
	//		객체.each(function(idx, 원소) { });
	//		$.each(집합, function(idx, 원소))
	// $.each()로 반복할 경우 원소는 jQuery 객체가 아니다
	$options.each(function(idx, option) {
		if($(option).text()==email2)
			$(option).prop("selected", true);
	});
}
$(function() {
	$("#profile").on("change", loadImage);
	makePage();
	// 비번 영역을 toggle
	$("#activateChangePwd").on("click", function() {
		$("#passwordArea").toggle();
	});

	// 이메일에 대한 select 인풋 처리
	$("#selectEmail").on("change", function() {
		var $choice = $("#selectEmail").val();
		if($choice==="직접 입력") {
			$("#email2").val("").attr("placeholder", "직접 입력");
			$("#email2").prop("disabled", false);
		} else {
			$("#email2").val($choice);
			$("#email2").prop("disabled", true);
		}
	});

	// 이름변경
	$("#changeIrum").on("click", function() {
		var params = {
			irum: $("#irum").val(),
			_csrf: "${_csrf.token}"
		}
		$.ajax({
			url: "/rboard2/user/update",
			method: "post",
			data: params,   // urlencoded로 자동 변환(processData 옵션)
			success:function() {
				toastr.info("이름변경 성공","서버 메시지");
			}, error: function(xhr) {
				console.log(xhr.response)
				toastr.warning("이름변경 실패", "서버 메시지");
			}
		});
	});
	
	
	// 비밀번호만 변경
	$("#changePwd").on("click", function() {
		// var $form = $("<form>").attr("action","/rboard2/user/update").attr("method","post")
		// $("<input>").attr("type","hidden").attr("name","password")
		//    .val($("#password").val()).appendTo($form);
		// $form.serialize() -> urlencoded로 변환
		
		var $password = $("#password").val();
		var $newPassword = $("#newPassword").val();
		var $newPassword2 = $("#newPassword2").val();
		if($newPassword!==$newPassword2)
			return false;
		var params = {
			_csrf: "${_csrf.token}",
			password: $password,
			newPassword: $newPassword
		};
		$.ajax({
			url: "/rboard2/user/update",
			method: "post",
			data: params,
			success:function() {
				toastr.info("변경 성공","서버 메시지");
				$("#password").val("");
				$("#newPassword").val("");
				$("#newPassword2").val("");
				$("#passwordArea").hide();
			}, error: function(xhr) {
				console.log(xhr.response)
				toastr.info("비밀번호 변경 실패", "서버 메시지");
			}
		});
	});
	
	
	// 이름, 이메일은 변경. 프사와 비번은 선택적으로 변경 update
	$("#update").on("click", function() { 
		var email = $("#email1").val() + "@" + $("#email2").val();
		
		// 프사가 있을 수도 없을 수도, 비번이 있을 수도 없을 수도
		// multipart/form-data로 ajax 업로드하려면 자바스크립트의 FormData 내장 객체를 사용
		// 1. var formData = new FormData(document.getElementById('myForm'));
		// 2. 비어있는 FormData를 생성한 다음 조건에 맞는 경우 append
		var formData = new FormData();
		formData.append("irum", $("#irum").val());
		formData.append("email", email);
		formData.append("_csrf", "${_csrf.token}");
		console.log($("#profile"))
		if($("#profile")[0].files[0]!=undefined)
			formData.append("profile", $("#profile")[0].files[0]);
		if($("#password").val()!=="" && $("#newPassword").val!="") {
			// ?= 전방탐색 : 앞에서 부터 찾아라
			// . : 정규식에서 .은 임의의 글자
			// * : 정규식에서 *는 있을 수도 있고 없을수도 있다(0이상)
			// () : 독립적인 하나의 조건
			// 다 합치면 : 특수문자가 하나이상
			var pattern =  /(?=.*[!@#$%^&*])^[A-Za-z0-9!@#$%^&*]{8,10}$/;
			var $newPassword = $("#newPassword").val();
			if(pattern.test($newPassword)==false) {
				alert("비밀번호는 특수문자를 포함하는 영숫자와 특수문자 8~10자입니다");
				return false;
			}
			if( $("#newPassword").val()!==$("#newPassword2").val()) {
				alert("비밀번호 확인에 실패했습니다");
				return false;
			}
			formData.append("newPassword",$("#newPassword").val());
			formData.append("password", $("#password").val());
		}
		
		$.ajax({
			url: "/rboard2/user/update",
			data: formData,
			method: "post",
			processData:false,
			contentType:false,
			success:function() {
				location.reload();
			}, error: function(xhr) {
				console.log(xhr.response)
				toastr.info("변경 실패", "서버 메시지");
			}
		});
	});

	// 회원 탈퇴
	$("#resign").on("click", function() {
		var $input = $("<input>").attr("type","hidden").attr("name","_csrf").val('${_csrf.token}');
		$("<form>").attr("action","/rboard2/user/resign").attr("method","post").append($input).appendTo("body").submit();
	});
});
</script>
</head>
<body>
	<div>
		<img id="show_profile" height="200px;" src="${user.profile }">
	</div>
	<div>
		<input type="file" name="profile" id="profile">
	</div>
	<table class="table table-hover" id="user">
		<tr>
			<td class="first">이름</td>
			<td><input type="text" id="irum" value="${user.irum}">&nbsp;	<button type="button" class="btn btn-info" id="changeIrum">이름변경</button></td>
			<td><img width="120px" id="profile">
			</td>
		</tr>
		<tr>
			<td class="first">아이디</td><td colspan="2"><span id="username">${user.username }</span></td>
		</tr>
		<tr>
			<td class="first">생년월일</td><td colspan="2"><span id="birthDate">${user.birthdayStr }</span></td>
		</tr>
		<tr>
			<td class="first">가입일</td><td colspan="2"><span id="joinDate">${user.joindayStr }</span></td>
		</tr>
		<tr><td class="first">비밀번호</td>
			<td colspan="2">
				<button type="button" class="btn btn-info" id="activateChangePwd">비밀번호 수정</button>
				<div id="passwordArea">
					<span class="key">현재 비밀번호 : </span><input type="password" id="password" ><br>
					<span class="key">새 비밀번호 : </span><input type="password" id="newPassword"><br>
					<span class="key">새 비밀번호 확인 : </span><input type="password" id="newPassword2">
	  				<button type="button" class="btn btn-info" id="changePwd">변경</button>
				</div>
			</td></tr>
		<tr><td class="first">이메일</td>
			<td colspan="2">
				<input type="text" name="email1" id="email1">&nbsp;@&nbsp;<input type="text" name="email2" id="email2">&nbsp;&nbsp;
				<select id="selectEmail">
					<option selected="selected">직접 입력</option>
					<option>naver.com</option>
					<option>daum.net</option>
					<option>gmail.com</option>
				</select>
			</td></tr>
		<tr><td class="first">회원정보</td>
			<td colspan="2">
				로그인 횟수 : <span id="loginCnt">${user.loginCnt }</span><br>
				작성 글수 : <span id="writeCnt">${user.writeCnt }</span>
			</td></tr>
	</table>
	<button type="button" class="btn btn-success" id="update">변경하기</button>
	<button type="button" class="btn btn-alert" id="resign">탈퇴하기</button>
</body>
</html>