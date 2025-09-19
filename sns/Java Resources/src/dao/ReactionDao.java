package dao;

import util.DbUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ReactionDao {

    // いいね追加（失敗したら何もしない）
    public boolean addReaction(int userId, int postId) {
        String sql = "INSERT INTO reactions (user_id, post_id) VALUES (?, ?)";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, postId);
            stmt.executeUpdate();

            // 成功したら posts.reactionc を +1
            updateReactionCount(conn, postId);
            return true;
        } catch (SQLException e) {
            // UNIQUE 制約違反の時（すでにいいね済み）は例外が出る
            System.out.println("すでにいいね済み");
            return false;
        }
    }

    private void updateReactionCount(Connection conn, int postId) throws SQLException {
        String sql = "UPDATE posts SET reactionc = reactionc + 1 WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, postId);
            stmt.executeUpdate();
        }
    }
}
