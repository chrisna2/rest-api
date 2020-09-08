package com.hyunkee.account;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class AccountServiceTest {

	@Autowired
	AccountService accountService;
	
	@Autowired
	AccountReopository accountReopository;
	
	@Autowired
	PasswordEncoder passwordEncoder;

	@Test
	public void findByUsername() {
		
		//given
		String password = "hyunkee";
		String username = "hyunkee@gmail.com";
		
		Account account = Account.builder()
								.email(username)
								.password(password)
								.roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
								.build();
		
		this.accountService.saveAccount(account);
		
		//when
		UserDetailsService userDetailsService = (UserDetailsService)accountService;
		UserDetails userDetails = userDetailsService.loadUserByUsername(username);
		
		//then
		//assertThat(userDetails.getPassword()).isEqualTo(password);
		//패스워드 인코딩 상태 확인
		assertThat(this.passwordEncoder.matches(password, userDetails.getPassword())).isTrue();
		
	}
	
	@Test
	public void findByUsernameFail1() {
		String username = "random@gmail.com";
		try {
			accountService.loadUserByUsername(username);
			fail("supposed to be failed");
		} 
		catch (UsernameNotFoundException e) {
			assertThat(e.getMessage()).containsSequence(username);
			// TODO: handle exception
		}
		
	}
	
	@Test
	public void findByUsernameFail2() {
		assertThrows(UsernameNotFoundException.class, () -> accountService.loadUserByUsername("random@gmail.com"));
	}
	
	
}
