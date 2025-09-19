<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>タイムライン</title>
</head>
<body>
    <h2>タイムライン</h2>

    <!-- ログアウトリンク -->
    <a href="LogoutServlet">ログアウト</a>
<hr>

    <!-- 投稿ループ -->
    <c:forEach var="post" items="${posts}">
        <div style="border:1px solid #ccc; margin:10px; padding:10px;">
            <p><b>${post.username}</b> さん</p>
            <p>${post.content}</p>
            <small>${post.createdAt}</small><br/>

            <!-- 達成状態 -->
            <c:if test="${post.success}">
                <p style="color:green;">✅ 達成済み</p>
            </c:if>

            <!-- 自分の投稿なら達成ボタンを表示 -->
            <c:if test="${sessionScope.userId == post.userId && not post.success}">
                <form action="${pageContext.request.contextPath}/SuccessServlet" method="post" style="display:inline;">
                    <input type="hidden" name="postId" value="${post.id}" />
                    <button type="submit">達成</button>
                </form>
            </c:if>

            <!-- いいね数 -->
            <p>👍 ${post.reactionc} 件</p>

            <!-- いいねボタン（ログインしてる人だけ） -->
            <c:if test="${not empty sessionScope.userId}">
                <form action="${pageContext.request.contextPath}/ReactionServlet" method="post" style="display:inline;">
                    <input type="hidden" name="postId" value="${post.id}" />
                    <button type="submit">いいね</button>
                </form>
            </c:if>
        </div>
    </c:forEach>

    <a href="post.jsp">新規投稿</a>
</body>
</html>
