package jworkspace;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SuppressWarnings({"PMD", "checkstyle:hideutilityclassconstructor"})
@SpringBootApplication
public class Workspace {

    public static void main(String[] args) {
        SpringApplication.run(Workspace.class, args);
    }
}
