package com.example.demo;

import com.zaxxer.hikari.HikariDataSource;
import net.truej.sql.bindings.AsObjectReadWrite;
import net.truej.sql.config.CompileTimeChecks;
import net.truej.sql.config.Configuration;
import net.truej.sql.config.TypeBinding;
import net.truej.sql.source.DataSourceW;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;
import java.sql.Types;
import java.util.UUID;

@SpringBootApplication public class PetClinic {

//    public static class UuidReadWrite extends AsObjectReadWrite<UUID> {
//        @Override public Class<UUID> aClass() { return UUID.class; }
//        @Override public int sqlType() { return Types.OTHER; }
//    }

    @Configuration(
        checks = @CompileTimeChecks(
            url = "jdbc:postgresql://localhost:5432/petclinic",
            username = "sa",
            password = "1234"
        )
//        , typeBindings = @TypeBinding(
//            compatibleSqlTypeName = "uuid",
//            rw = UuidReadWrite.class
//        )
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
