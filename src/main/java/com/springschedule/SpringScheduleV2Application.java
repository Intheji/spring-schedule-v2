package com.springschedule;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SpringScheduleV2Application {

    public static void main(String[] args) {
        SpringApplication.run(SpringScheduleV2Application.class, args);
    }

}
