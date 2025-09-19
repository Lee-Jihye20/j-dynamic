package servlet;

import dao.ReactionDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/ReactionServlet")
public class ReactionServlet extends HttpServlet {
    private ReactionDao reactionDao = new ReactionDao();

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");

        if (userId == null) {
            response.sendRedirect("index.jsp");
            return;
        }

        int postId = Integer.parseInt(request.getParameter("postId"));

        reactionDao.addReaction(userId, postId);

        response.sendRedirect("TimelineServlet");
    }
}
