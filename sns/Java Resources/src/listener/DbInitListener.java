package listener;

import javax.servlet.*;
import java.io.*;
import java.sql.*;

public class DbInitListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            Class.forName("org.h2.Driver");
            Connection con = DriverManager.getConnection(
                "jdbc:h2:~/snsdb", "sa", "");
            Statement stmt = con.createStatement();

            // schema.sql 読み込み
            InputStream in = getClass().getClassLoader().getResourceAsStream("schema.sql");
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            StringBuilder sql = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sql.append(line).append("\n");
            }
            for (String s : sql.toString().split(";")) {
                if (!s.trim().isEmpty()) stmt.execute(s);
            }
            br.close(); stmt.close(); con.close();
            System.out.println("✅ DB初期化完了");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void contextDestroyed(ServletContextEvent sce) {}
}
