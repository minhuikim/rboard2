<!-- jsp directive -->
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<link rel="stylesheet" href="/rboard2/css/read.css">
<style>
	/* 
	block요소 - 한 줄을 차지. margin: 0 auto;
	inline 요소 - 자신이 가진 내용의 너비만큼 차지
	inline-block - 너비가 적용
	*/
	#attachment a {
		width: 200px;
		display: inline-block;
	}
	.fa-trash-o {
		color: red;
		cursor: pointer;
	}
</style>
<sec:authorize access="isAnonymous()">
	<script>
		var isLogin = false;
		var loginId = undefined;
	</script>
</sec:authorize>
<sec:authorize access="isAuthenticated()">
	<script>
		var isLogin = true;
		var loginId = "${sessionScope.SPRING_SECURITY_CONTEXT.authentication.principal.username}";
	</script>
</sec:authorize>
<script src="/rboard2/ckeditor/ckeditor.js"></script>
<script>
	console.log(isLogin);
	console.log(loginId);
	function printAttachment(attachments) {
		var $attachment = $("#attachment");
		$attachment.empty();
		$.each(attachments, function(index, a) {
			$("<li>").appendTo($attachment);
			var href = "/rboard2/attachment/download?ano=" + a.ano; 
			$("<a>").attr("href",href).text(a.originalFileName).appendTo($attachment);
			$("<i>").attr("class","fa fa-trash-o delete_attachment")
				.attr("data-ano",a.ano).attr("data-bno", a.bno).appendTo($attachment);
		});
	}

	function printComment(comments) {
		var $comments = $("#comments");
		$comments.empty();
		$.each(comments, function(index, comment) {
			var $comment = $("<div>").attr("class","comment").appendTo($comments);
			$("<div>").text(comment.writer).appendTo($comment);
			var $div = $("<div>").appendTo($comment);
			$("<img>").attr("src",comment.profile).css("width","40px").appendTo($div);
			$("<span>").text(comment.content).appendTo($div);
			if(comment.writer==loginId) {
				$("<button>").attr("class","delete_comment").attr("data-cno",comment.cno)
					.css("float","right").text("삭제").appendTo($div);
			}
			$("<div>").text(comment.writeTime).appendTo($comment);
			$("<hr>").appendTo($comment);
		});
	}

	$(function() {
		// 화면 기본값 : 비로그인 상태 - 글 편집 불가능, 변경/삭제 버튼 안보임, 추천/비추 불가능, 첨부파일 삭제 버튼 안보이게
		$("#btn_area").hide();
		$(".delete_attachment").hide();

		var writer = '${board.writer}';

		// 로그인했을 때 화면 변경
		// 로그인 했는데 작성자 : 변경/삭제 버튼 보이기, 제목 편집 가능, 내용 편집 가능, 댓글 작성 가능, 첨부파일 삭제 가능
		if(isLogin==true && loginId==writer) {
			$("#btn_area").show();
			$("#title").prop("disabled", false);
			var ck = CKEDITOR.replace("content", {
				filebrowserUploadUrl : '/rboard2/ckImageUpload'
			});
			$("#comment_textarea").prop("disabled", false);
			$("#comment_write").prop("disabled", false);
			$(".delete_attachment").show();
		}
		// 로그인했는데 작성자가 아님 : 추천/비추 활성화, 변경/삭제 보이지않음, 댓글 작성 가능
		if(isLogin==true && loginId!=writer) {
			$("#good_btn").prop("disabled", false);
			$("#bad_btn").prop("disabled", false);
			$("#comment_textarea").prop("disabled", false);
			$("#comment_write").prop("disabled", false);
		}

		$("#update").on("click", function() {
			// 폼을 만들고 글번호, 제목, 내용, csrf을 추가한 다음 서버로 보낸다
			var $form = $("<form>").attr("action","/rboard2/board/update").attr("method","post");
			$("<input>").attr("type","hidden").attr("name","bno").val('${board.bno}').appendTo($form);
			$("<input>").attr("type","hidden").attr("name","_csrf").val('${_csrf.token}').appendTo($form);
			$("<input>").attr("type","hidden").attr("name","title").val($('#title').val()).appendTo($form);
			$("<input>").attr("type","hidden").attr("name","content").val(CKEDITOR.instances['content'].getData()).appendTo($form);
			$form.appendTo("body").submit();
		});

		$("#delete").on("click", function() {
			// 폼을 만들고 글번호, csrf를 추가한 다음 서버로 보낸다
			var $form = $("<form>").attr("action","/rboard2/board/delete").attr("method","post");
			$("<input>").attr("type","hidden").attr("name","bno").val('${board.bno}').appendTo($form);
			$("<input>").attr("type","hidden").attr("name","_csrf").val('${_csrf.token}').appendTo($form);
			$form.appendTo("body").submit();
		});

		$("#good_btn").on("click", function() {
			var params = {
				bno : '${board.bno}',
				_csrf: '${_csrf.token}'
			}
			$.ajax({
				url: "/rboard2/board/good",
				method: "post",
				data: params,
				success:function(result) {
					$("#good_cnt").text(result);
				}, error: function(response) {
					console.log(response.status);
				}
			})
		});

		$("#bad_btn").on("click", function() {
			var params = {
				bno: '${board.bno}',
				_csrf: '${_csrf.token}'
			}
			$.ajax({
				url: "/rboard2/board/bad",
				method: "post",
				// 자바스크립트 객체를 주면 $.ajax가 자동으로 bno=11&age=22 형식으로 자동변환한다
				data: params,
				success:function(result) {
					$("#bad_cnt").text(result);
				}, error:function(response) {
					console.log(response.status);
				}
			})
		});

		$("#attachment").on("click", ".delete_attachment", function() {
			var params = {
				ano: $(this).attr("data-ano"),
				bno: $(this).attr("data-bno"),
				_csrf: '${_csrf.token}'
			};
			$.ajax({
				url: "/rboard2/attachment/delete",
				data: params,
				method: "post",
				success: function(attachments) {
					console.log(attachments);
					printAttachment(attachments);
				}, error: function(response) {
					console.log(response.status);
				}
			})
		});

		$("#comment_write").on("click", function() {
			var params= {
				content: $("#comment_textarea").val(),
				bno : '${board.bno}',
				_csrf: '${_csrf.token}'
			};
			$.ajax({
				url: "/rboard2/comment/write",
				method: "post",
				data: params,
				success:function(comments) {
					$("#comment_textarea").val("");
					printComment(comments);
				}, error: function(response) {
					console.log(response.status);
				}
			});
		});

		// 댓글 삭제 버튼 클릭하면 댓글 번호를 alert()으로 출력하시오
		$("#comments").on("click", ".delete_comment", function() {
			var params ={
				cno : $(this).attr("data-cno"),
				bno : '${board.bno}',
				_csrf: '${_csrf.token}'
			};
			$.ajax({
				url: "/rboard2/comment/delete",
				method: "post",
				data: params,
				success:function(comments) {
					console.log(comments);
					printComment(comments);
				}, error: function(response) {
					console.log(response.status);
				}
			});
		});
	})
</script>
</head>
<body>
<div id="wrap">
	<div>
		<div id="title_div">
			<div id="upper">
				<input type="text" id="title" disabled="disabled" value="${board.title }">
				<span id="writer">${board.writer }</span>
			</div>
			<div id="lower">
				<ul id="lower_left">
					<li>글번호<span id="bno">${board.bno }</span></li>
					<li><span id="write_time">${board.writeTimeStr }</span></li>
				</ul>
				<ul id="lower_right">
					<li>
						<button type="button" class="btn btn-primary" id="good_btn" disabled="disabled">
							추천 <span class="badge" id="good_cnt">${board.goodCnt }</span>
						</button></li>
					<li>
						<button type="button" class="btn btn-success" disabled="disabled">
							조회 <span class="badge" id="read_cnt">${board.readCnt }</span>
						</button></li>    
  					<li><button type="button" class="btn btn-danger" id="bad_btn" disabled="disabled">
  							비추 <span class="badge" id="bad_cnt">${board.badCnt }</span>
  						</button></li>        	
				</ul>
			</div>
		</div>
		<div>
			<ul id="attachment">
				<c:forEach items="${board.attachments}" var="attachment"> 
					<li>
						<a href="/rboard2/attachment/download?ano=${attachment.ano}">${attachment.originalFileName}</a>
						<i class="fa fa-trash-o delete_attachment" data-ano="${attachment.ano}" data-bno="${attachment.bno}"></i>
					</li>
				</c:forEach>
			</ul>
		</div>
		<div id="content_div">
			<div class="form-group">
				<div class="form-control" id="content">${board.content }</div>
			</div>
			<div id="btn_area">
				<button id="update" class="btn btn-info">변경</button>
				<button id="delete" class="btn btn-success">삭제</button>
			</div>
		</div>
		<div>
			<div class="form-group">
				<label for="comment_textarea">댓글을 입력하세요</label>
				<textarea class="form-control" rows="5"	id="comment_textarea" placeholder="욕설이나 모욕적인 댓글은 삭제될 수 있습니다" disabled="disabled"></textarea>
			</div>
			<button type="button" class="btn btn-info" id="comment_write" disabled="disabled">댓글 작성</button>
		</div>
		<hr>
		<div id="comments">
			<c:forEach items="${board.comments}" var="comment" >
				<div class="comments">
					<div>${comment.writer }</div>
					<div>
						<img src="${comment.profile }" width="40px">
						<span>${comment.content }</span>
						<c:if test="${comment.writer == sessionScope.username}">
							<button class="delete_comment" data-cno="${comment.cno}" style="float:right;">삭제</button>
						</c:if>
					</div>
					<div>
						<fmt:formatDate value="${comment.writeTime}" pattern="yyyy-MM-dd hh:mm:ss"/>
					</div>
				</div>
				<hr>
			</c:forEach>
		</div>
	</div>
</div>
</body>




</html>