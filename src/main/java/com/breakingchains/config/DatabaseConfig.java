package com.breakingchains.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Configuration
public class DatabaseConfig {

    @Value("${spring.datasource.url}")
    private String databaseUrl;

    @Value("${spring.datasource.username:}")
    private String username;

    @Value("${spring.datasource.password:}")
    private String password;

    @Bean
    public DataSource dataSource() {
        String jdbcUrl = databaseUrl;
        String dbUsername = username;
        String dbPassword = password;

        // Auto-convert standard cloud URIs (postgres:// or postgresql://) to JDBC format
        if (databaseUrl.startsWith("postgres://") || databaseUrl.startsWith("postgresql://")) {
            try {
                URI uri = new URI(databaseUrl);
                String host = uri.getHost();
                int port = uri.getPort() == -1 ? 5432 : uri.getPort();
                String path = uri.getPath();
                String query = uri.getQuery();

                // Always prioritize credentials embedded inside the cloud URI if present
                if (uri.getUserInfo() != null) {
                    String[] userInfo = uri.getUserInfo().split(":", 2);
                    if (userInfo.length > 0) {
                        dbUsername = URLDecoder.decode(userInfo[0], StandardCharsets.UTF_8);
                    }
                    if (userInfo.length > 1) {
                        dbPassword = URLDecoder.decode(userInfo[1], StandardCharsets.UTF_8);
                    }
                }

                jdbcUrl = "jdbc:postgresql://" + host + ":" + port + path + (query != null ? "?" + query : "");
            } catch (Exception ex) {
                jdbcUrl = databaseUrl.replace("postgres://", "jdbc:postgresql://")
                                     .replace("postgresql://", "jdbc:postgresql://");
            }
        }

        return DataSourceBuilder.create()
                .url(jdbcUrl)
                .username(dbUsername)
                .password(dbPassword)
                .driverClassName("org.postgresql.Driver")
                .build();
    }
}
