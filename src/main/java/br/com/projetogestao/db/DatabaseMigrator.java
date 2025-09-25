package br.com.projetogestao.db;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseMigrator {
    private final DataSource dataSource;

    public DatabaseMigrator(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void applySchema() {
        // Aplicar o schema principal
        String sql = loadResourceAsString("/db/schema.sql");
        try (Connection conn = dataSource.getConnection(); Statement st = conn.createStatement()) {
            for (String stmt : sql.split(";\\s*\\n")) {
                String trimmed = stmt.trim();
                if (!trimmed.isEmpty()) {
                    st.execute(trimmed);
                }
            }
            
            // Aplicar migrações
            applyMigrations(conn);
            
        } catch (SQLException e) {
            throw new RuntimeException("Failed to apply schema", e);
        }
    }
    
    private void applyMigrations(Connection conn) throws SQLException {
        // Aplicar migração V1
        try {
            String migrationSql = loadResourceAsString("/db/migration/V1__add_birth_date.sql");
            try (Statement st = conn.createStatement()) {
                for (String stmt : migrationSql.split(";\\s*\\n")) {
                    String trimmed = stmt.trim();
                    if (!trimmed.isEmpty()) {
                        st.execute(trimmed);
                    }
                }
            }
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Resource not found")) {
                // Migração não encontrada, ignorar
                System.out.println("Migration V1 not found, skipping.");
            } else {
                throw e;
            }
        }
    }

    private String loadResourceAsString(String path) {
        try (InputStream in = getClass().getResourceAsStream(path)) {
            if (in == null) throw new IllegalStateException("Resource not found: " + path);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append('\n');
                }
                return sb.toString();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read resource: " + path, e);
        }
    }
}


