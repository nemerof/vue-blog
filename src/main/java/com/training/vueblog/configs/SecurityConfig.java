package com.training.vueblog.configs;

import com.training.vueblog.services.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserService service;

    private final PasswordEncoder encoder;

    public SecurityConfig(UserService service, PasswordEncoder encoder) {
        this.service = service;
        this.encoder = encoder;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .antMatcher("/**")
                .authorizeRequests()
                .antMatchers("/", "/js/**", "/css/**", "/api/message", "/api/user", "/api/login").permitAll()
                .anyRequest().authenticated()
                // spring in-built authorization is not necessary
                //.and()
                //    .formLogin().loginProcessingUrl("/api/login")
                .and()
                    .logout().logoutUrl("/api/logout").permitAll()
                .and()
                    .exceptionHandling().accessDeniedHandler(accessDeniedHandler())
                .and()
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                .and()
                    .csrf().disable();
    }

    //todo Не уверен что это тут нужно :)
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(service)
            .passwordEncoder(encoder);
    }

    @Bean
    @Override
    // for custom authentication
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler(){
        return new CustomAccessDeniedHandler();
    }
}
