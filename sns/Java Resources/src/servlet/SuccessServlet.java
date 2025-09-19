package servlet;

import dao.PostDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/SuccessServlet")
public class SuccessServlet extends HttpServlet {
    private PostDao postDao = new PostDao();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        int postId = Integer.parseInt(request.getParameter("postId"));
        postDao.markAsSuccess(postId);

        // 更新後にタイムラインへ戻る
        response.sendRedirect("TimelineServlet");
    }
}
