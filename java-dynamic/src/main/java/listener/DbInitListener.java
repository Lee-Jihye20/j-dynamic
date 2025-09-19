package listener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import javax.servlet.*;

public class DbInitListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            Class.forName("org.h2.Driver");
            Connection con = DriverManager.getConnection(
                "jdbc:h2:~/snsdb", "sa", "");
            Statement stmt = con.createStatement();

            // schema.sql を読み込んで実行
            BufferedReader br = new BufferedReader(
                new InputStreamReader(
                    getClass().getClassLoader().getResourceAsStream("schema.sql"),
                    "UTF-8"
                )
            );
            String line;
            StringBuilder sql = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sql.append(line).append("\n");
            }
            br.close();

            for (String s : sql.toString().split(";")) {
                if (!s.trim().isEmpty()) {
                    stmt.execute(s);
                }
            }

            stmt.close();
            con.close();
            System.out.println("✅ H2 Database 初期化完了");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // アプリ停止時の処理（特になしでOK）
    }
}
