package br.com.projetogestao.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

public class DataSourceProvider {
    private static DataSourceProvider instance;
    private final HikariDataSource dataSource;

    private DataSourceProvider() {
        AppProperties props = AppProperties.getInstance();
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(props.get("app.datasource.url"));
        config.setUsername(props.get("app.datasource.username"));
        config.setPassword(props.get("app.datasource.password"));
        config.setMaximumPoolSize(props.getInt("app.datasource.pool.size", 10));
        config.setPoolName("GestaoProjetosPool");
        this.dataSource = new HikariDataSource(config);
    }

    public static synchronized DataSourceProvider getInstance() {
        if (instance == null) {
            instance = new DataSourceProvider();
        }
        return instance;
    }

    public DataSource getDataSource() {
        return dataSource;
    }
}


