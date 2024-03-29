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

@Configuration
/*
 * Tells Spring to use this class to configure Spring and Spring Boot itself.
 * Any Beans specified in this class will now be available to Spring's Auto
 * Configuration engine
 */
class SecurityConfig {

  @Bean
  /*
   * All HTTP requests to cashcards/ endpoints are required to be authenticated
   * using HTTP Basic Authentication security (username and password).
   * Also, RBAC(Role Based Access Control) it's enabled in order to get access to CashCards information
   * Also, do not require CSRF security.
   */
  SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
      .authorizeHttpRequests(request ->
        request.requestMatchers("/cashcards/**").hasRole("CARD-OWNER"))/*enable RBAC: Replaced the .authenticated() call with the hasRole(...) call.*/
      .csrf(csrf -> csrf.disable())
      .httpBasic(Customizer.withDefaults());
    /*
     * enabled basic authentication, requiring that requests must
     * supply a username and password
     */

    return http.build();
  }

  @Bean
  /*
   * Added pre-defined users and roles for authentication and authorization
   * management
   */
  UserDetailsService onlyTestUsers(PasswordEncoder encoder) {
    User.UserBuilder users = User.builder();
    UserDetails user = users
      .username("LeudiX1")
      .password(encoder.encode("leo123"))
      .roles("CARD-OWNER")
      .build();

    UserDetails user2 = users
      .username("Sarah")
      .password(encoder.encode("sara123"))
      .roles("CARD-OWNER")
      .build();

      UserDetails user3 = users
      .username("Lucy2")
      .password(encoder.encode("lucy123"))
      .roles("NON-OWNER")
      .build();

    return new InMemoryUserDetailsManager(user, user2, user3);
  }
  /*Password encoder */
  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
