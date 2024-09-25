package com.example.demo;

import com.zaxxer.hikari.HikariDataSource;
import net.truej.sql.config.CompileTimeChecks;
import net.truej.sql.config.Configuration;
import net.truej.sql.source.DataSourceW;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

@SpringBootApplication public class PetClinic {

    @Configuration(
        checks = @CompileTimeChecks(
            url = "jdbc:postgresql://localhost:5432/petclinic",
            username = "sa",
            password = "1234"
        )
    ) public static class MainDb extends DataSourceW {
         public MainDb(DataSource w) { super(w); }
    }

    @Bean MainDb mainDb(
        @Value("${datasource.url}") String url,
        @Value("${datasource.username}") String username,
        @Value("${datasource.password}") String password
    ) {
        return new MainDb(new HikariDataSource() {{
            setJdbcUrl(url);
            setUsername(username);
            setPassword(password);
            setMaximumPoolSize(10);
        }});
    }

    public static void main(String[] args) {
        SpringApplication.run(PetClinic.class, args);
    }
}
