package no.boco.backend.config;

import no.boco.backend.authentication.CustomAuthenticationFilter;
import no.boco.backend.authentication.jwt.JwtUtil;
import no.boco.backend.authentication.jwt.JwtValidationFilter;
import no.boco.backend.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.CookieClearingLogoutHandler;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private PasswordEncoder bCrypt;
    @Autowired
    private UserService userService;
    @Autowired
    private JwtValidationFilter jwtValidationFilter;
    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .headers().frameOptions().disable()
                .and()
                    .cors()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                    .addFilter(customAuthenticationFilter())
                    .addFilterAfter(jwtValidationFilter, CustomAuthenticationFilter.class)
                .logout()
                    .addLogoutHandler(new CookieClearingLogoutHandler(jwtUtil.cookieName()))
                    .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK))
                .and()
                    .authorizeRequests()
                        .antMatchers("/login").permitAll()
                        .antMatchers("/register").permitAll()
                        .antMatchers("/token").permitAll()
                        .antMatchers("/fill").permitAll()
                        .antMatchers("/h2-console/**").permitAll()
                        .antMatchers("/activate/**").permitAll()
                        .antMatchers(HttpMethod.GET, "/categories").permitAll()
                        .antMatchers(HttpMethod.GET, "/posts/**").permitAll()
                        .antMatchers(HttpMethod.GET, "/users/**").permitAll()
                        .antMatchers(HttpMethod.GET, "/organizations/**").permitAll()
                        .antMatchers("/swagger-ui/**").permitAll()
                        .antMatchers("/swagger-ui.html/**").permitAll()
                        .antMatchers("/swagger-ui/**").permitAll()
                        .antMatchers("/swagger-ui.html/**").permitAll()
                .anyRequest().authenticated();
    }

    // Setup
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(daoAuthenticationProvider());
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(bCrypt);
        provider.setUserDetailsService(userService);
        return provider;
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public CustomAuthenticationFilter customAuthenticationFilter() {
        try {
            return new CustomAuthenticationFilter(authenticationManagerBean());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
