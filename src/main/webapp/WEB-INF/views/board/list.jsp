<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
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
});
</script>
</head>
<body>
<div class="alert alert-danger alert-dismissible" id="msg" style="display:none;">
   	<a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a>
    <strong>서버 메시지 </strong><span id="alert"></span>
</div>
<div id="page">
	<div id="list">
		<table class="table table-hover" style="width:800px;">
			<colgroup>
				<col width="10%"><col width="15%"><col width="5%">
				<col width="40%"><col width="20%"><col width="10%">
			</colgroup>
			<tr>
				<th>번호</th><th>글쓴이</th><th></th><th>제목</th><th>날짜</th><th>조회수</th>
			</tr>
			<c:forEach items="${page.list}" var="board">
				<tr>
					<td>${board.bno }</td>
					<td>${board.writer }</td>
					<td>
						<c:if test="${board.attachmentExist==true }">
							<i class="fa fa-paperclip"></i>
						</c:if>
					</td>
					<td>
						<a href="/rboard2/board/read?bno=${board.bno }" >${board.title }</a> [${board.commentCnt}]
					</td>
					<td>${board.writeTimeStr }</td>
					<td>${board.readCnt }</td>
				</tr>
			</c:forEach>
		</table>
	</div>
	<div>
		<ul id="pagination" class="pagination">
			<c:if test="${page.prev>0 }">
				<li><a href="/rboard2/board/list?pageno=${page.prev}">이전으로</a></li>
			</c:if>
			<c:forEach begin="${page.start }" end="${page.end }" var="i">
				<c:choose>
					<c:when test="${i==page.pageno }">
						<li class="active"><a href="/rboard2/board/list?pageno=${i}">${i}</a></li>
					</c:when>
					<c:otherwise>
						<li><a href="/rboard2/board/list?pageno=${i}">${i}</a></li>
					</c:otherwise>
				</c:choose>
			</c:forEach>
			<c:if test="${page.next>0 }">
				<li><a href="/rboard2/board/list?pageno=${page.next}">다음으로</a></li>
			</c:if>
		</ul>
	</div>
</div>
</body>
</html>





