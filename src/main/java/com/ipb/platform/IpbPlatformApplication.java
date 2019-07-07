package com.ipb.platform;

import com.ipb.platform.webparser.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class IpbPlatformApplication implements CommandLineRunner {

    @Autowired
    private CategoryWebparser categoryWebparser;

    @Autowired
    private CityWebparser cityWebparser;

    @Autowired
    private LandmarkWebparser landmarkWebparser;

    @Autowired
    private VillageWebparser villageWebparser;

    @Autowired
    private EventsWebparser eventsWebparser;

    public static void main(String[] args) {
        SpringApplication.run(IpbPlatformApplication.class, args);
    }

    @Override
    public void run(String... args) {
//        this.loginUser("admin@ipb.com", "mse2018ipb");
//        System.out.println("============================================ START CATEGORIES  ============================================");
//        categoryWebparser.run();
//        System.out.println("============================================ FINISH CATEGORIES  ============================================");

//        System.out.println("============================================ START CITIES  ============================================");
//        cityWebparser.run();
//        System.out.println("============================================ FINISH CITIES  ============================================");

//        System.out.println("============================================ START VILLAGE  ============================================");
//        villageWebparser.run();
//        System.out.println("============================================ FINISH VILLAGE  ============================================");
//
//        System.out.println("============================================ START EVENTS  ============================================");
//        eventsWebparser.run();
//        System.out.println("============================================ FINISH EVENTS  ============================================");
//
//        System.out.println("============================================ START LANDMARKS  ============================================");
//        landmarkWebparser.run();
//        System.out.println("============================================ FINISH LANDMARKS ============================================");
    }

    private void loginUser(String username, String password){
        Authentication auth =
                new UsernamePasswordAuthenticationToken(username, password, null);

        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    /**
     * A password encoder bean for storing passwords in an encrypted BCrypt format.
     *
     * @return an instance of the bean
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
