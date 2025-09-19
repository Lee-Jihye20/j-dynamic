package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Post;
import util.DbUtil;

public class PostDao {

    // 投稿一覧を取得
	public List<Post> findAll() {
	    List<Post> posts = new ArrayList<>();
	    String sql = "SELECT p.id, p.user_id, p.content, p.is_success, p.reactionc, p.created_at, u.username " +
	             "FROM posts p INNER JOIN users u ON p.user_id = u.id " +
	             "ORDER BY p.created_at DESC";


	    try (Connection conn = DbUtil.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql);
	         ResultSet rs = stmt.executeQuery()) {

	        while (rs.next()) {
	        	Post post = new Post();
	        	post.setId(rs.getInt("id"));
	        	post.setUserId(rs.getInt("user_id"));
	        	post.setUsername(rs.getString("username"));
	        	post.setContent(rs.getString("content"));
	        	post.setSuccess(rs.getBoolean("is_success")); // ← 追加
	        	post.setReactionc(rs.getInt("reactionc"));
	        	post.setCreatedAt(rs.getTimestamp("created_at"));

	            posts.add(post);

	            // ★ デバッグプリント（サーバーの Console に表示される）
	            System.out.println("Post[id=" + post.getId()
	                    + ", user=" + post.getUsername()
	                    + ", content=" + post.getContent()
	                    + ", createdAt=" + post.getCreatedAt() + "]");
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return posts;
	}
	public void markAsSuccess(int postId) {
	    String sql = "UPDATE posts SET is_success = TRUE WHERE id = ?";
	    try (Connection conn = DbUtil.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {
	        stmt.setInt(1, postId);
	        stmt.executeUpdate();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}



    // 投稿を追加
    public void insert(Integer userId, String content) {
        String sql = "INSERT INTO posts (user_id, content) VALUES (?, ?)";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setString(2, content);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
}
