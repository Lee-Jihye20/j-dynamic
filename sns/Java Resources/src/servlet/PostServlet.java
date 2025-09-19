package servlet;

import dao.PostDao;
import model.Post;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/PostServlet")
public class PostServlet extends HttpServlet {
    private PostDao postDao = new PostDao();

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String content = request.getParameter("content");

        // ログインユーザーのIDをセッションから取得
        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");

        // ログインしていない場合はログインページへリダイレクト
        if (userId == null) {
            response.sendRedirect("index.jsp");
            return;
        }

        if (content != null && !content.trim().isEmpty()) {
            postDao.insert(userId, content);
        }

        response.sendRedirect("TimelineServlet");
    }
}
