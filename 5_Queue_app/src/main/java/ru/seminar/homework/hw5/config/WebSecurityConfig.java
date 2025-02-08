package ru.seminar.homework.hw5.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Value("${user.username}")
    private String userUsername;

    @Value("${user.password}")
    private String userPassword;

    @Value("${manager.username}")
    private String managerUsername;

    @Value("${manager.password}")
    private String managerPassword;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        InMemoryUserDetailsManager userDetailsManager = new InMemoryUserDetailsManager();
        UserDetails user = User.withDefaultPasswordEncoder()
                .username(userUsername)
                .password(userPassword)
                .roles("USER")
                .build();
        UserDetails manager = User.withDefaultPasswordEncoder()
                .username(managerUsername)
                .password(managerPassword)
                .roles("MANAGER")
                .build();
        userDetailsManager.createUser(user);
        userDetailsManager.createUser(manager);
        return userDetailsManager;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, HandlerMappingIntrospector introspector) throws Exception {
        var matcher = new MvcRequestMatcher(introspector, "/task");
        matcher.setMethod(HttpMethod.POST);
        http
                .authorizeRequests(authorize ->
                        authorize.requestMatchers(new AntPathRequestMatcher("/soap/*")).permitAll()
                        .requestMatchers(matcher).permitAll()
                        .requestMatchers(new MvcRequestMatcher(introspector, "/task/**")).hasRole("USER")
                        .requestMatchers(new MvcRequestMatcher(introspector, "/task")).hasRole("USER")
                        .requestMatchers(new MvcRequestMatcher(introspector, "/tasks")).permitAll()
                        .requestMatchers(new MvcRequestMatcher(introspector, "/times/**")).hasRole("MANAGER")
                        .anyRequest().permitAll()
                )
                .formLogin(withDefaults())
                .httpBasic(withDefaults())
                .csrf(csrf -> csrf.ignoringRequestMatchers(new MvcRequestMatcher(introspector, "/**")));
        return http.build();
    }

}
