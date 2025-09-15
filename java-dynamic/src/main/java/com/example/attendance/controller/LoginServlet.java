package com.example.attendance.controller;

import java.io.IOException;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.example.attendance.dao.AttendanceDAO;
import com.example.attendance.dao.UserDAO;
import com.example.attendance.dto.Attendance;
import com.example.attendance.dto.User;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();
    private final AttendanceDAO attendanceDAO = new AttendanceDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        User user = userDAO.findByUsername(username);

        if (user != null && user.isEnabled() && userDAO.verifyPassword(username, password)) {
            HttpSession session = req.getSession();
            session.setAttribute("user", user);
            session.setAttribute("successMessage", "ログインしました。");

            if ("admin".equals(user.getRole())) {
                // 全ユーザーの勤怠記録と合計勤務時間を計算
                var allRecords = attendanceDAO.findAll();
                req.setAttribute("allAttendanceRecords", allRecords);

                Map<String, Long> totalHoursByUser = allRecords.stream()
                        .collect(Collectors.groupingBy(
                                Attendance::getUserId,
                                Collectors.summingLong(att -> {
                                    if (att.getCheckInTime() != null && att.getCheckOutTime() != null) {
                                        return ChronoUnit.HOURS.between(att.getCheckInTime(), att.getCheckOutTime());
                                    }
                                    return 0L;
                                })
                        ));

                req.setAttribute("totalHoursByUser", totalHoursByUser);

                RequestDispatcher rd = req.getRequestDispatcher("/jsp/admin_menu.jsp");
                rd.forward(req, resp);

            } else {
                // 一般ユーザーの勤怠記録表示
                req.setAttribute("attendanceRecords", attendanceDAO.findByUserId(user.getUsername()));

                RequestDispatcher rd = req.getRequestDispatcher("/jsp/employee_menu.jsp");
                rd.forward(req, resp);
            }

        } else {
            // ログイン失敗
            req.setAttribute("errorMessage", "ユーザーID またはパスワードが不正です。またはアカウントが無効です。");
            RequestDispatcher rd = req.getRequestDispatcher("/login.jsp");
            rd.forward(req, resp);
        }
    }
}
