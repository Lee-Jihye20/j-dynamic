package servlet;

import java.io.IOException;
import javax.servlet.*;
import javax.servlet.http.*;
import dao.UserDao;
import model.User;

public class RegisterServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        UserDao dao = new UserDao();
        boolean success = dao.insert(new User(username, password));

        if (success) {
            response.sendRedirect("index.jsp?registered=true");
        } else {
            request.setAttribute("error", "登録に失敗しました（ユーザー名重複の可能性あり）");
            RequestDispatcher rd = request.getRequestDispatcher("register.jsp");
            rd.forward(request, response);
        }
    }
}
