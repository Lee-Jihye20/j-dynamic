<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>新規投稿</title>
</head>
<body>
    <h2>新しい投稿を作成</h2>

    <form action="PostServlet" method="post">
        <textarea name="content" rows="5" cols="40" placeholder="いまどうしてる？"></textarea><br>
        <input type="submit" value="投稿">
    </form>

    <p><a href="TimelineServlet">タイムラインに戻る</a></p>
</body>
</html>
