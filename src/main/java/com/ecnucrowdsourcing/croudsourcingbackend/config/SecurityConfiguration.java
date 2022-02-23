package com.ecnucrowdsourcing.croudsourcingbackend.config;

import com.ecnucrowdsourcing.croudsourcingbackend.service.SecurityUserDetailService;
import com.ecnucrowdsourcing.croudsourcingbackend.util.ResponseUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
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
@EnableGlobalMethodSecurity(prePostEnabled = true)
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

  @Resource
  private IndexPrefixProvider indexPrefixProvider;

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
    //configuration.setAllowedOriginPatterns(List.of("http://localhost:3000"));
    configuration.setAllowedOriginPatterns(List.of("*"));
    configuration.setAllowedMethods(List.of("*"));
    configuration.setAllowCredentials(true);
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.authorizeRequests(authorizeRequests ->
      authorizeRequests
          .antMatchers("/", "/login", "/user/signup", "/test/**", "/kb/**", "/result/**", "/images/**").permitAll()
          .antMatchers("/**").authenticated()
    )
            .formLogin()
            .loginPage("/login")
            .usernameParameter("phone")
            .passwordParameter("password")
            .successHandler((req, res, auth) -> res.setStatus(HttpStatus.OK.value()))
            .failureHandler((req, response, e) -> {
              response.setStatus(HttpStatus.UNAUTHORIZED.value());
              response.setContentType("application/json;charset=UTF-8");
              PrintWriter writer = response.getWriter();
              writer.write(new ObjectMapper().writeValueAsString(responseUtil.fail("用户名或密码错误")));
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
            .exceptionHandling().accessDeniedHandler(myAccessDeniedHandler)
            .authenticationEntryPoint(customAuthenticationEntryPoint)
            .and()
            .sessionManagement()
            .maximumSessions(1)
            .maxSessionsPreventsLogin(false)
            .expiredUrl("/login");

    /* If csrf protection is enabled, request method will be limited without the token
     * If requests don't come from browser, http 403 will occur */
    if (indexPrefixProvider.profile.equals("dev")) http.csrf().disable();
    else http.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());	

    /*http.authorizeRequests()
            .anyRequest().permitAll().and().logout().permitAll();*/

  }

  @Override
  public void configure(WebSecurity web) throws Exception {
    web.ignoring().antMatchers(
        "/swagger-ui.html",
        "/swagger-ui/*",
        "/swagger-resources/**",
        "/v2/api-docs",
        "/v3/api-docs",
        "/webjars/**",
        "/fonts/**",
        "/img/**",
        "/js/**",
        "/favicon.ico"
    );
  }
}
