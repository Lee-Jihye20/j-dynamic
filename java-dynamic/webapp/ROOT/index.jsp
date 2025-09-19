<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head><title>ログイン</title></head>
<body>
<h2>ログイン</h2>
<form action="LoginServlet" method="post">
  ユーザー名: <input type="text" name="username"><br>
  パスワード: <input type="password" name="password"><br>
  <input type="submit" value="ログイン">
</form>
<a href="register.jsp">新規登録はこちら</a>
</body>
</html>
