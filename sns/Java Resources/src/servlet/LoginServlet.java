package servlet;

import java.io.IOException;
import javax.servlet.*;
import javax.servlet.http.*;
import dao.UserDao;
import model.User;

public class LoginServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        UserDao dao = new UserDao();
        User user = dao.findByLogin(username, password);

        if (user != null) {
            HttpSession session = request.getSession();
            session.setAttribute("user", user);
            session.setAttribute("userId", user.getId());   // ← これ必須
            session.setAttribute("username", user.getUsername()); // 表示用にも便利

            response.sendRedirect("TimelineServlet");
        } else {
            request.setAttribute("error", "ユーザー名またはパスワードが違います");
            RequestDispatcher rd = request.getRequestDispatcher("index.jsp");
            rd.forward(request, response);
        }
    }
}
