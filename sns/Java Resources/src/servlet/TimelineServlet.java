package servlet;

import dao.PostDao;
import model.Post;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

public class TimelineServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
    	

        PostDao postDao = new PostDao();
        List<Post> posts = postDao.findAll();
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            resp.sendRedirect("index.jsp");
            return;
        }

        req.setAttribute("posts", posts);
        RequestDispatcher rd = req.getRequestDispatcher("/timeline.jsp");
        rd.forward(req, resp);
    }
}
