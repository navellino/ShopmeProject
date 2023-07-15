package it.shopme.admin.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SuppressWarnings("deprecation")
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Bean
	public UserDetailsService userDetailsService() {
		return new ShopmeUserDetailService();
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userDetailsService());
		authProvider.setPasswordEncoder(passwordEncoder());
		return authProvider;
	}
	
	
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(authenticationProvider());
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
       // http.authorizeRequests(requests -> requests.anyRequest().authenticated()).formLogin(login -> login.loginPage("/login").permitAll());
       
		//http.authorizeRequests().anyRequest().permitAll();
		http.authorizeRequests()
		.antMatchers("/users/**").hasAuthority("Admin")
		.antMatchers("/categories/**").hasAnyAuthority("Admin","Editor")
		.anyRequest().authenticated()
		.and().formLogin().loginPage("/login").usernameParameter("email").permitAll()
		.and().logout().permitAll().and().rememberMe() //di default il coockie scade dopo 2 settimane e il token quando restarto il server viene resettato e quindi bisogna comunque riloggarsi
		.key("AbcDefGhilmnoPQrs_123456789")//aggiungendo la key statica anche restartando il cookie rimane
		.tokenValiditySeconds(7*24*60*60);//così è possibile settare la scadenza del cookie

	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/images/**", "/js/**", "/webjars/**");
	}
	
}
