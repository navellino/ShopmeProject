package it.shopme.admin.user;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoderTest {
	@Test
	public void testEncoderPassword() {
		BCryptPasswordEncoder passEncoder = new BCryptPasswordEncoder();
		String rawPassword = "nicola123456";
		String encodePassword = passEncoder.encode(rawPassword);
		System.out.println(encodePassword);
		boolean matches = passEncoder.matches(rawPassword, encodePassword);
		assertThat(matches).isTrue();
	}
}
