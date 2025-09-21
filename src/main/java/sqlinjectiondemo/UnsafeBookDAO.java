package sqlinjectiondemo;

import java.sql.*;

//za demonstraciju: Executing SQL: INSERT INTO books (title, author, category, imagePath) VALUES ('DemoBook','John Doe','Education','https://example.com/orig.png'),('InjectedBook','Hacker','InjectedCategory','https://attacker.example/img.png')
//Unsafe demo: statement executed. Check DB and console for SQL printed by DAO.

public class UnsafeBookDAO
{
    private final String url = "jdbc:mysql://localhost:3306/books_demo?useSSL=false&serverTimezone=UTC&allowMultiQueries=true";
    private final String user = "root";
    private final String password = "";

    public void init() throws SQLException
    {
        String sql = """
                CREATE TABLE IF NOT EXISTS books (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    title VARCHAR(200) NOT NULL,
                    author VARCHAR(100) NOT NULL,
                    category VARCHAR(64) NOT NULL,
                    imagePath VARCHAR(255) NOT NULL
                )
                """;

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.execute();
        }
    }

    public void addBookUnsafe(String title, String author, String category, String imagePath) throws SQLException
    {
        String sql = "INSERT INTO books (title, author, category, imagePath) VALUES ('"
                + title + "','" + author + "','" + category + "','" + imagePath + "')";

        System.out.println("Executing SQL: " + sql); // For demo purposes

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement())
        {
            stmt.execute(sql);
        }
    }
}
