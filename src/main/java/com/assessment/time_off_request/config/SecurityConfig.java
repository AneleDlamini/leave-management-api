/* Configuration of Spring security and authentication*/

package com.assessment.time_off_request.config;



import java.io.IOException;
import java.util.Collection;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.assessment.time_off_request.model.Worker;
import com.assessment.time_off_request.repo.WorkerRepo;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    WorkerRepo workerRepository;
    Worker worker;

    // User Logs in - handle login security and what functions get authenticated
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF and CORS for testing purposes
            //.csrf(csrf -> csrf.disable())
            .cors(cors -> cors.disable())

            // Allow frames for H2
            .headers(headers -> headers.frameOptions().disable()) 

            // Define which endpoints require which roles
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/h2-console/**").permitAll() // allow console access
            // allow Swagger UI and OpenAPI docs without login
                .requestMatchers("/v3/api-docs/**","/swagger-ui.html", "/swagger-ui/**").permitAll()
                .requestMatchers("api/request").hasRole("MANAGER")
                .requestMatchers("/home/user").hasRole("WORKER")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
            .loginProcessingUrl("/login")
            .successHandler(customAuthSuccessHandler())
            .permitAll()
            );


        return http.build();
    }

    // Who is the user? - defining API user details to be used for authentication
    @Bean
    public UserDetailsService userDetailsService(WorkerRepo repository) throws UsernameNotFoundException {
        return email -> {
            Worker worker = repository.findByEmail(email);
            if (worker == null) {
                throw new UsernameNotFoundException("User not found");
        }

        // User's authentication object that is stored in the server
        return User.withUsername(worker.getEmail())
                   .password(worker.getPassword())
                   .roles(worker.getRole())
                   .build();
        };
    }

    // Hashing password to be stored
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // What happens after logging in? - handling landing pages
    @Bean
    public AuthenticationSuccessHandler customAuthSuccessHandler() {
        return new AuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws jakarta.servlet.ServletException, java.io.IOException {

                Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

                boolean isManager = false;

                for (GrantedAuthority authority : authorities) {
                    if (authority.getAuthority().equals("ROLE_MANAGER")) {
                        isManager = true;
                    }
                }

                if (isManager) {
                    response.sendRedirect("/home/manager"); // Managers landing page
                } else if (!isManager) {
                    response.sendRedirect("/home/user"); // Other worker's landing page
                } else {
                    response.sendRedirect("/login?error"); // fallback
                }
            }
        };

    }
}
