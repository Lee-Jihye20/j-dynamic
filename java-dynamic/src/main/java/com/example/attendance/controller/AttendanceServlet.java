package com.example.attendance.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.example.attendance.dao.AttendanceDAO;
import com.example.attendance.dto.Attendance;
import com.example.attendance.dto.User;

@WebServlet("/attendance")
public class AttendanceServlet extends HttpServlet {

    private final AttendanceDAO attendanceDAO = new AttendanceDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String action = req.getParameter("action");
        HttpSession session = req.getSession(false);

        // ログインチェック
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect("login.jsp");
            return;
        }
        User user = (User) session.getAttribute("user");

        // 成功メッセージを一度だけ表示
        String message = (String) session.getAttribute("successMessage");
        if (message != null) {
            req.setAttribute("successMessage", message);
            session.removeAttribute("successMessage");
        }

        if ("export_csv".equals(action) && "admin".equals(user.getRole())) {
            exportCsv(req, resp);
            return;
        }

        if ("filter".equals(action) && "admin".equals(user.getRole())) {
            handleFilter(req, resp);
            return;
        }

        // 通常表示
        if ("admin".equals(user.getRole())) {
            List<Attendance> allRecords = attendanceDAO.findAll();
            req.setAttribute("allAttendanceRecords", allRecords);

            Map<String, Long> totalHoursByUser = calculateTotalHours(allRecords);
            req.setAttribute("totalHoursByUser", totalHoursByUser);

            req.setAttribute("monthlyWorkingHours", attendanceDAO.getMonthlyWorkingHours(null));
            req.setAttribute("monthlyCheckInCounts", attendanceDAO.getMonthlyCheckInCounts(null));

            req.getRequestDispatcher("/jsp/admin_menu.jsp").forward(req, resp);
        } else {
            req.setAttribute("attendanceRecords", attendanceDAO.findByUserId(user.getUsername()));
            req.getRequestDispatcher("/jsp/employee_menu.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);

        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect("login.jsp");
            return;
        }
        User user = (User) session.getAttribute("user");

        String action = req.getParameter("action");

        switch (action) {
            case "check_in" -> {
                attendanceDAO.checkIn(user.getUsername());
                session.setAttribute("successMessage", "出勤を記録しました。");
            }
            case "check_out" -> {
                attendanceDAO.checkOut(user.getUsername());
                session.setAttribute("successMessage", "退勤を記録しました。");
            }
            case "add_manual" -> {
                if ("admin".equals(user.getRole())) {
                    handleAddManual(req, session);
                }
            }
            case "update_manual" -> {
                if ("admin".equals(user.getRole())) {
                    handleUpdateManual(req, session);
                }
            }
            case "delete_manual" -> {
                if ("admin".equals(user.getRole())) {
                    handleDeleteManual(req, session);
                }
            }
        }

        // 更新後リダイレクト
        if ("admin".equals(user.getRole())) {
            resp.sendRedirect("attendance?action=filter"
                    + "&filterUserId=" + safeParam(req, "filterUserId")
                    + "&startDate=" + safeParam(req, "startDate")
                    + "&endDate=" + safeParam(req, "endDate"));
        } else {
            resp.sendRedirect("attendance");
        }
    }

    private void exportCsv(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/csv; charset=UTF-8");
        resp.setHeader("Content-Disposition", "attachment; filename=\"attendance_records.csv\"");

        PrintWriter writer = resp.getWriter();
        writer.println("User ID,Check-in Time,Check-out Time");

        LocalDate startDate = parseDate(req.getParameter("startDate"));
        LocalDate endDate = parseDate(req.getParameter("endDate"));
        String filterUserId = req.getParameter("filterUserId");

        List<Attendance> records = attendanceDAO.findFilteredRecords(filterUserId, startDate, endDate);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (Attendance record : records) {
            writer.printf("%s,%s,%s%n",
                    record.getUserId(),
                    record.getCheckInTime() != null ? record.getCheckInTime().format(formatter) : "",
                    record.getCheckOutTime() != null ? record.getCheckOutTime().format(formatter) : "");
        }
        writer.flush();
    }

    private void handleFilter(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String filterUserId = req.getParameter("filterUserId");
        LocalDate startDate = parseDate(req.getParameter("startDate"));
        LocalDate endDate = parseDate(req.getParameter("endDate"));

        List<Attendance> filteredRecords = attendanceDAO.findFilteredRecords(filterUserId, startDate, endDate);
        req.setAttribute("allAttendanceRecords", filteredRecords);

        Map<String, Long> totalHoursByUser = calculateTotalHours(filteredRecords);
        req.setAttribute("totalHoursByUser", totalHoursByUser);

        req.setAttribute("monthlyWorkingHours", attendanceDAO.getMonthlyWorkingHours(filterUserId));
        req.setAttribute("monthlyCheckInCounts", attendanceDAO.getMonthlyCheckInCounts(filterUserId));

        req.getRequestDispatcher("/jsp/admin_menu.jsp").forward(req, resp);
    }

    private void handleAddManual(HttpServletRequest req, HttpSession session) {
        String userId = req.getParameter("userId");
        try {
            LocalDateTime checkIn = LocalDateTime.parse(req.getParameter("checkInTime"));
            LocalDateTime checkOut = parseDateTime(req.getParameter("checkOutTime"));
            attendanceDAO.addManualAttendance(userId, checkIn, checkOut);
            session.setAttribute("successMessage", "勤怠記録を手動で追加しました。");
        } catch (DateTimeParseException e) {
            session.setAttribute("errorMessage", "日付/時刻の形式が不正です。");
        }
    }

    private void handleUpdateManual(HttpServletRequest req, HttpSession session) {
        String userId = req.getParameter("userId");

        try {
            LocalDateTime oldCheckIn = LocalDateTime.parse(req.getParameter("oldCheckInTime"));
            LocalDateTime oldCheckOut = parseDateTime(req.getParameter("oldCheckOutTime"));
            LocalDateTime newCheckIn = LocalDateTime.parse(req.getParameter("newCheckInTime"));
            LocalDateTime newCheckOut = parseDateTime(req.getParameter("newCheckOutTime"));

            boolean updated = attendanceDAO.updateManualAttendance(userId, oldCheckIn, oldCheckOut, newCheckIn, newCheckOut);
            if (updated) {
                session.setAttribute("successMessage", "勤怠記録を手動で更新しました。");
            } else {
                session.setAttribute("errorMessage", "勤怠記録の更新に失敗しました。");
            }
        } catch (DateTimeParseException e) {
            session.setAttribute("errorMessage", "日付/時刻の形式が不正です。");
        }
    }

    private void handleDeleteManual(HttpServletRequest req, HttpSession session) {
        String userId = req.getParameter("userId");

        try {
            LocalDateTime checkIn = LocalDateTime.parse(req.getParameter("checkInTime"));
            LocalDateTime checkOut = parseDateTime(req.getParameter("checkOutTime"));

            if (attendanceDAO.deleteManualAttendance(userId, checkIn, checkOut)) {
                session.setAttribute("successMessage", "勤怠記録を削除しました。");
            } else {
                session.setAttribute("errorMessage", "勤怠記録の削除に失敗しました。");
            }
        } catch (DateTimeParseException e) {
            session.setAttribute("errorMessage", "日付/時刻の形式が不正です。");
        }
    }

    // ===== ヘルパーメソッド =====

    private Map<String, Long> calculateTotalHours(List<Attendance> records) {
        return records.stream()
                .collect(Collectors.groupingBy(
                        Attendance::getUserId,
                        Collectors.summingLong(att -> {
                            if (att.getCheckInTime() != null && att.getCheckOutTime() != null) {
                                return java.time.temporal.ChronoUnit.HOURS.between(att.getCheckInTime(), att.getCheckOutTime());
                            }
                            return 0L;
                        })
                ));
    }

    private LocalDate parseDate(String dateStr) {
        try {
            return (dateStr != null && !dateStr.isEmpty()) ? LocalDate.parse(dateStr) : null;
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private LocalDateTime parseDateTime(String dateTimeStr) {
        try {
            return (dateTimeStr != null && !dateTimeStr.isEmpty()) ? LocalDateTime.parse(dateTimeStr) : null;
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private String safeParam(HttpServletRequest req, String name) {
        String value = req.getParameter(name);
        return value != null ? value : "";
    }
}
