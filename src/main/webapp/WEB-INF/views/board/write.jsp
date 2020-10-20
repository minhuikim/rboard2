<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<script src="/rboard2/ckeditor/ckeditor.js"></script>
<script>
	$(function() {
		var ck = CKEDITOR.replace("content", {
			// 파일 업로드 주소 지정
			filebrowserUploadUrl:'/rboard2/ckImageUpload?_csrf=' + '${_csrf.token}'
		});

		// 첨부파일 추가 버튼을 누르면 <input type='file'>을 하나씩 추가
		var index = 0;
		
		$("#add_attachment").on("click", function() {
			var $div = $("#attachment_div");
			$("<input>").attr("type","file").attr("name","attachments[" + index + "]" )
				.appendTo($div);
			index++;
		});

		$("#write").on("click", function() {
			$("#writeForm").submit();
		});
	});
	
</script>
</head>
<body>
	<form action="/rboard2/board/write" method="post" id="writeForm" enctype="multipart/form-data">
		<input type="hidden" name="_csrf" value="${_csrf.token }">
		<div class="form-group">
			<label for="title">제목:</label>
			<input type="text" class="form-control" id="title" name="title">
		</div>
		<div class="form-group">
			<textarea class="form-control" id="content" name="content"></textarea>
		</div>
		<div class="form-group">
			<button type="button" id="add_attachment">첨부파일 추가</button>
		</div>
		<div id="attachment_div">
		</div>
		<hr>
		<button type="button" class="btn btn-success" id="write">작성</button>
	</form>
</body>
</html>

