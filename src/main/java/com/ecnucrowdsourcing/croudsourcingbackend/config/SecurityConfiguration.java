package com.ecnucrowdsourcing.croudsourcingbackend.config;

import com.ecnucrowdsourcing.croudsourcingbackend.service.SecurityUserDetailService;
import com.ecnucrowdsourcing.croudsourcingbackend.util.ResponseUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.annotation.Resource;
import java.io.PrintWriter;
import java.util.List;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

  public static final String ROLE_USER = "USER";

  public static final String ROLE_ADMIN = "ADMIN";

  @Resource
  private SecurityUserDetailService securityUserDetailService;

  @Resource
  private MyAccessDeniedHandler myAccessDeniedHandler;

  @Resource
  private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

  @Resource
  private ResponseUtil responseUtil;

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.inMemoryAuthentication().passwordEncoder(new BCryptPasswordEncoder())
            .withUser("user").password(new BCryptPasswordEncoder().encode("pwd")).roles(ROLE_USER);

    auth.inMemoryAuthentication().passwordEncoder(new BCryptPasswordEncoder())
            .withUser("admin").password(new BCryptPasswordEncoder().encode("pwd")).roles(ROLE_USER,ROLE_ADMIN);

    auth.userDetailsService(securityUserDetailService).passwordEncoder(new BCryptPasswordEncoder());
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedHeaders(List.of("*"));
    configuration.setAllowedOriginPatterns(List.of("http://localhost:3000"));
    configuration.setAllowedMethods(List.of("*"));
    configuration.setAllowCredentials(true);
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.authorizeRequests()
            .antMatchers("/demo/greet", "/", "/home", "/login", "/roles", "/user/**").permitAll()
            .and()
            .formLogin()
            .loginPage("/login")
            .usernameParameter("username")
            .passwordParameter("password")
            .successHandler((req, res, auth) -> res.setStatus(HttpStatus.OK.value()))
            .failureHandler((req, response, e) -> {
              response.setStatus(HttpStatus.UNAUTHORIZED.value());
              response.setContentType("application/json;charset=UTF-8");
              PrintWriter writer = response.getWriter();
              writer.write(new ObjectMapper().writeValueAsString(responseUtil.fail("Authentication failed")));
              writer.flush();
              writer.close();
            })
            .and()
            .logout()
            .logoutUrl("/logout")
            .logoutSuccessHandler((req, response, au) -> {
              response.setStatus(HttpStatus.OK.value());
              response.setContentType("application/json;charset=UTF-8");
              PrintWriter writer = response.getWriter();
              writer.write(new ObjectMapper().writeValueAsString(responseUtil.success()));
              writer.flush();
              writer.close();
            })
            .and()
            .cors()
            .and()
            .csrf()
            .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            .and()
            .exceptionHandling().accessDeniedHandler(myAccessDeniedHandler)
            .authenticationEntryPoint(customAuthenticationEntryPoint)
            .and()
            .sessionManagement()
            .maximumSessions(1)
            .maxSessionsPreventsLogin(false)
            .expiredUrl("/login");
  }
}
