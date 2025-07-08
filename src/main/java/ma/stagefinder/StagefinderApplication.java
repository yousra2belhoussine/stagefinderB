package ma.stagefinder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableCaching // <-- L'ANNOTATION J'DIDA LI KHASSNA
@EnableAsync   // <-- HADI 7TA HIYA MZYANA L L'EMAIL
public class StagefinderApplication {

    public static void main(String[] args) {
        SpringApplication.run(StagefinderApplication.class, args);
    }

}
