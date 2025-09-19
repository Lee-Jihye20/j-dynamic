<%@ page contentType="text/html; charset=UTF-8" %>
<html>
<head><title>新規登録</title></head>
<body>
<h2>新規アカウント登録</h2>
<form action="RegisterServlet" method="post">
    ユーザー名: <input type="text" name="username"><br>
    パスワード: <input type="password" name="password"><br>
    <input type="submit" value="登録">
</form>
<a href="index.jsp">ログインページに戻る</a>
<br>
<% if (request.getAttribute("error") != null) { %>
    <p style="color:red;"><%= request.getAttribute("error") %></p>
<% } %>
</body>
</html>
