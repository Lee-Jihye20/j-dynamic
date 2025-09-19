package dao;
import java.sql.*;
import model.User;

public class UserDAO {
    private Connection getConnection() throws Exception {
        Class.forName("org.h2.Driver");
        // メモリDBなら jdbc:h2:mem:snsdb
        // 永続化DBなら jdbc:h2:~/snsdb
        return DriverManager.getConnection(
            "jdbc:h2:~/snsdb", "sa", "");
    }

    public boolean insert(User user) {
        try (Connection con = getConnection()) {
            String sql = "INSERT INTO users(username, password) VALUES(?, ?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.executeUpdate();
            return true;
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public User login(String username, String password) {
        try (Connection con = getConnection()) {
            String sql = "SELECT * FROM users WHERE username=? AND password=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new User(rs.getInt("id"),
                                rs.getString("username"),
                                rs.getString("password"));
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
