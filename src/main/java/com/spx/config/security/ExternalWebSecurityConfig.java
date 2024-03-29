package com.spx.config.security;

import com.spx.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.AuthProvider;
import java.util.Arrays;

/**
 * Created by Bogdan1 on 09.09.2016.
 */
@Configuration
@EnableOAuth2Sso
@Order(1)
public class ExternalWebSecurityConfig extends WebSecurityConfigurerAdapter {

/*    @Autowired
    private static UserDao userDao;

    @Bean
    public AuthFilter filter() {
        return new AuthFilter(userDao);
    }*/

    @Override
    protected void configure(final HttpSecurity http) throws Exception {

        http.antMatcher("/**").authorizeRequests().antMatchers("/", "/login**", "/resources/**").permitAll()
                .antMatchers("/register/**").permitAll()
                .antMatchers("/construct/**").permitAll()
                .antMatchers("/rest/user/register/**").permitAll()
                .antMatchers("/rest/user/formlogin/**").permitAll()
                .antMatchers("/rest/user/activate/**").permitAll()
                .anyRequest()
                .authenticated().and().logout().logoutSuccessUrl("/").permitAll()
                .and().exceptionHandling().authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/"))
                .and().csrf().csrfTokenRepository(csrfTokenRepository()).and()//.addFilterAfter(filter(), SecurityContextPersistenceFilter.class)
                .addFilterAfter(csrfHeaderFilter(), CsrfFilter.class);

    }

    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        return new CustomAuthenticationManager();
    }


    private Filter csrfHeaderFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request,
                                            HttpServletResponse response, FilterChain filterChain)
                    throws ServletException, IOException {
                CsrfToken csrf = (CsrfToken) request
                        .getAttribute(CsrfToken.class.getName());
                if (csrf != null) {
                    Cookie cookie = WebUtils.getCookie(request, "XSRF-TOKEN");
                    String token = csrf.getToken();
                    if (cookie == null
                            || token != null && !token.equals(cookie.getValue())) {
                        cookie = new Cookie("XSRF-TOKEN", token);
                        cookie.setPath("/");
                        response.addCookie(cookie);
                    }
                }
                filterChain.doFilter(request, response);
            }
        };
    }

    private CsrfTokenRepository csrfTokenRepository() {
        HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
        repository.setHeaderName("X-XSRF-TOKEN");
        return repository;
    }
}
