package br.com.projetogestao.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AppProperties {
    private static final String PROPERTIES_PATH = "/application.properties";
    private static AppProperties instance;
    private final Properties properties = new Properties();

    private AppProperties() {
        try {
            // Primeiro tenta carregar do classpath
            InputStream in = getClass().getResourceAsStream(PROPERTIES_PATH);
            
            // Se não encontrar no classpath, tenta carregar do sistema de arquivos
            if (in == null) {
                try {
                    in = new java.io.FileInputStream("src/main/resources/application.properties");
                } catch (java.io.FileNotFoundException fnfe) {
                    // Tenta outro caminho relativo
                    try {
                        in = new java.io.FileInputStream("./src/main/resources/application.properties");
                    } catch (java.io.FileNotFoundException fnfe2) {
                        // Última tentativa com caminho absoluto
                        String userDir = System.getProperty("user.dir");
                        try {
                            in = new java.io.FileInputStream(userDir + "/src/main/resources/application.properties");
                        } catch (java.io.FileNotFoundException fnfe3) {
                            throw new IllegalStateException("application.properties not found on classpath or filesystem");
                        }
                    }
                }
            }
            
            properties.load(in);
            in.close();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load application.properties", e);
        }
    }

    public static synchronized AppProperties getInstance() {
        if (instance == null) {
            instance = new AppProperties();
        }
        return instance;
    }

    public String get(String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            throw new IllegalArgumentException("Missing property: " + key);
        }
        return value;
    }

    public int getInt(String key, int defaultValue) {
        String v = properties.getProperty(key);
        if (v == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(v.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid integer value for property: " + key, e);
        }
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        String v = properties.getProperty(key);
        if (v == null) return defaultValue;
        String s = v.trim().toLowerCase();
        switch (s) {
            case "true": case "1": case "yes": case "y": case "on": case "sim": case "s":
                return true;
            case "false": case "0": case "no": case "n": case "off": case "nao": case "não":
                return false;
            default:
                return defaultValue;
        }
    }

    /**
     * Verifica se o seeder de dados de exemplo está habilitado
     */
    public boolean isSeedExampleEnabled() {
        return getBoolean("app.seed.example", false);
    }

}
