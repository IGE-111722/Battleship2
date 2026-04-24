package battleship;

import java.sql.*;

public class DatabaseManager {
    private static final String URL = "jdbc:h2:./data/battleship_db";
    private static final String USER = "sa";
    private static final String PASS = "";

    public DatabaseManager() {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
            Statement stmt = conn.createStatement();
            // Cria a tabela se não existir
            stmt.execute("CREATE TABLE IF NOT EXISTS JOGADAS (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "num_jogada INT, " +
                    "tiros VARCHAR(255), " +
                    "data_hora TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");
        } catch (SQLException e) {
            System.err.println("Erro ao iniciar BD: " + e.getMessage());
        }
    }

    public void salvarJogada(int numJogada, String jsonShots) {
        String sql = "INSERT INTO JOGADAS (num_jogada, tiros) VALUES (?, ?)";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, numJogada);
            pstmt.setString(2, jsonShots);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao salvar na BD: " + e.getMessage());
        }
    }
}