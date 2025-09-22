package com.galapea.techblog.jobboardgriddbcloud.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class HttpSecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            final AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain configure(final HttpSecurity http) throws Exception {
        return http.cors(Customizer.withDefaults())
                .csrf(Customizer.withDefaults())
                .authorizeHttpRequests(
                        authorize ->
                                authorize
                                        .requestMatchers("/css/**", "/js/**", "/images/**")
                                        .permitAll())
                .authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll())
                .formLogin(
                        form ->
                                form.loginPage("/login")
                                        .usernameParameter("email")
                                        .failureUrl("/login?loginError=true")
                                        .defaultSuccessUrl("/jobs"))
                .logout(
                        logout ->
                                logout.logoutSuccessUrl("/login?logoutSuccess=true")
                                        .deleteCookies("JSESSIONID"))
                .exceptionHandling(
                        exception ->
                                exception.authenticationEntryPoint(
                                        new LoginUrlAuthenticationEntryPoint(
                                                "/login?loginRequired=true")))
                .build();
    }
}
