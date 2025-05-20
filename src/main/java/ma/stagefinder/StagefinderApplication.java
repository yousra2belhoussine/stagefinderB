package ma.stagefinder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class StagefinderApplication {

    public static void main(String[] args) {
        SpringApplication.run(StagefinderApplication.class, args);
    }

}
