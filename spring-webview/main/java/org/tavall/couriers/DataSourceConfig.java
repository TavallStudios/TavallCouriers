package org.tavall.couriers;


import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class DataSourceConfig {


    @Bean(destroyMethod = "close")
    public HikariDataSource dataSource(Environment env) {

        // Prefer explicit app env vars to avoid stale spring.datasource.* values.
        String url = firstNonBlank(
                env.getProperty("TAVALL_POSTGRES_URL"),
                env.getProperty("NOVUS_POSTGRES_URL"),
                env.getProperty("spring.datasource.url"));

        String user = firstNonBlank(
                env.getProperty("TAVALL_POSTGRES_USER"),
                env.getProperty("NOVUS_POSTGRES_USER"),
                env.getProperty("spring.datasource.username"));

        String pass = firstNonBlank(
                env.getProperty("TAVALL_POSTGRES_PASS"),
                env.getProperty("NOVUS_POSTGRES_PASS"),
                env.getProperty("spring.datasource.password"));

        if (url == null) {
            throw new IllegalStateException(
                    "Missing DB URL. Set TAVALL_POSTGRES_URL or NOVUS_POSTGRES_URL (or spring.datasource.url)");
        }

        if (user == null) {
            throw new IllegalStateException(
                    "Missing DB USER. Set TAVALL_POSTGRES_USER or NOVUS_POSTGRES_USER (or spring.datasource.username)"
            );
        }

        HikariConfig cfg = new HikariConfig();
        cfg.setJdbcUrl(url);
        cfg.setUsername(user);
        cfg.setPassword(pass);

        return new HikariDataSource(cfg);
    }

    private String firstNonBlank(String... values) {
        for (String v : values) {
            if (v != null && !v.isBlank()) {
                return v;
            }
        }
        return null;
    }
}