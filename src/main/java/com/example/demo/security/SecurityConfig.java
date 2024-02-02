package com.example.demo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration /*
                * Tells Spring to use this class to configure Spring and Spring Boot itself.
                * Any Beans specified in this class will now be available to Spring's Auto
                * Configuration engine
                */
class SecurityConfig {

    @Bean /*
           * All HTTP requests to cashcards/ endpoints are required to be authenticated
           * using HTTP Basic Authentication security (username and password).
           * 
           * Also, do not require CSRF security.
           */
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(request -> request
                        .requestMatchers("/cashcards/**")
                        .authenticated())
                .csrf(csrf -> csrf.disable())
                .httpBasic(Customizer.withDefaults()); /*
                                                        * enabled basic authentication, requiring that requests must
                                                        * supply a username and password
                                                        */

        return http.build();
    }

    @Bean
    UserDetailsService onlyTestUsers(PasswordEncoder encoder) {
        User.UserBuilder users = User.builder();
        UserDetails user = users.username("LeudiX1").password(encoder.encode("leo123")).roles().build();
    
        return new InMemoryUserDetailsManager(user);
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
