package it.shopme.site.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import it.shopme.site.security.oauth.CustomerOAuth2UserService;
import it.shopme.site.security.oauth.OAuth2LoginSuccessHandler;

@SuppressWarnings("deprecation")
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private CustomerOAuth2UserService oauth2UserService;
	
	@Autowired
	private OAuth2LoginSuccessHandler oAuth2LoginHandler;
	
	@Autowired
	private DatabaseLoginSuccessHandler databaseLoginHandler;
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
        //http.authorizeRequests(requests -> requests.anyRequest().permitAll());
        
        http.authorizeHttpRequests()
            .antMatchers("/account_details", "/update_account_detail").authenticated()
            .anyRequest().permitAll()
            .and()
            .formLogin()
            	.loginPage("/login")
                .usernameParameter("email")
                .successHandler(databaseLoginHandler)
                .permitAll()
            .and()
            .oauth2Login()
            	.loginPage("/login")
            	.userInfoEndpoint()
            	.userService(oauth2UserService)
            .and()
            	.successHandler(oAuth2LoginHandler)
            .and()
            .logout().permitAll()
            .and()
            .rememberMe()
            	.key("1234567890_aBcDeFgHiJkLmNoPqRsTuWyXz")
            	.tokenValiditySeconds(14*24*60*60);
            
		/*
        http.authorizeHttpRequests()
                .antMatchers("/customers").authenticated()
                .anyRequest().permitAll()
                .and()
                .formLogin(login -> login.loginPage("/login")
                        .usernameParameter("email")
                        .permitAll())
                .logout(logout -> logout.permitAll())
                .rememberMe(me -> me
                        .key("1234567890_aBcDeFgHiJkLmNoPqRsTuWyXz")
                        .tokenValiditySeconds(14 * 24 * 60 * 60));*/
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/images/**", "/js/**", "/webjars/**");
	}

    @Bean
    UserDetailsService userDetailService() {
		return new CustomerUserDetailsService();
	}
    
    @Bean
    DaoAuthenticationProvider authenticationProvider() {
    	DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    	
    	authProvider.setUserDetailsService(userDetailService());
    	authProvider.setPasswordEncoder(passwordEncoder());
    	
    	return authProvider;
    }
}
