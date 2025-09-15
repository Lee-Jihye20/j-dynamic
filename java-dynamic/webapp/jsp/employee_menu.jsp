<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <title>従業員メニュー</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style.css">
</head>
<body>
<div class="container">
    <h1>従業員メニュー</h1>
    <p>ようこそ, ${user.username} さん</p>

    <!-- メッセージ表示 -->
    <c:if test="${not empty sessionScope.successMessage}">
        <p class="success-message"><c:out value="${sessionScope.successMessage}" /></p>
        <c:remove var="successMessage" scope="session"/>
    </c:if>
    <c:if test="${not empty errorMessage}">
        <p class="error-message"><c:out value="${errorMessage}" /></p>
    </c:if>

    <!-- 出勤/退勤ボタン -->
    <div class="button-group">
        <form action="attendance" method="post" style="display:inline;">
            <input type="hidden" name="action" value="check_in">
            <input type="submit" value="出勤" class="button">
        </form>
        <form action="attendance" method="post" style="display:inline;">
            <input type="hidden" name="action" value="check_out">
            <input type="submit" value="退勤" class="button">
        </form>
    </div>

    <!-- 勤怠履歴 -->
    <h2>あなたの勤怠履歴</h2>
    <table>
        <thead>
        <tr>
            <th>出勤時刻</th>
            <th>退勤時刻</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="att" items="${attendanceRecords}">
            <tr>
                <td>${att.checkInTime}</td>
                <td>${att.checkOutTime}</td>
            </tr>
        </c:forEach>
        <c:if test="${empty attendanceRecords}">
            <tr><td colspan="2">勤怠記録がありません。</td></tr>
        </c:if>
        </tbody>
    </table>

    <!-- ログアウト -->
    <div class="button-group">
        <a href="logout" class="button secondary">ログアウト</a>
    </div>
</div>
</body>
</html>
