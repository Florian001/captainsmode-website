package dev.florian.linz.captainsmode.api;

import dev.florian.linz.captainsmode.game.GameController;
import dev.florian.linz.captainsmode.player.PlayerController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(AbstractHttpConfigurer::disable)
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> {
                auth
                    .requestMatchers(HttpMethod.GET, ConfigurationController.BASE_URL + "health").permitAll() // Allow access to /health
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // Allow pre-flight CORS requests
                    .anyRequest().authenticated(); // All other requests require authentication
            })
            .httpBasic(Customizer.withDefaults());
        
       return http.build();
       
    }

    @Bean
    public UserDetailsService users() {
        // The builder will ensure the passwords are encoded before saving in memory
        User.UserBuilder users = User.withDefaultPasswordEncoder();
        UserDetails flo = users
            .username("Florian")
            .password("12489")
            .roles("USER", "ADMIN")
            .build();
        UserDetails erik = users
            .username("Erik")
            .password("8265")
            .roles("USER", "ADMIN")
            .build();
        UserDetails schnigger = users
            .username("Schnigger")
            .password("5672")
            .roles("USER", "ADMIN")
            .build();
        UserDetails daniel = users
            .username("Daniel")
            .password("9164")
            .roles("USER", "ADMIN")
            .build();
        UserDetails dennis = users
            .username("Dennis")
            .password("7534")
            .roles("USER", "ADMIN")
            .build();
        UserDetails admin = users
            .username("captainsmode")
            .password("erik2")
            .roles("USER")
            .build();
        return new InMemoryUserDetailsManager(flo, erik, schnigger, daniel, dennis, admin);
    }

}
