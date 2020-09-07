package com.hyunkee.account;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
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
		
		this.accountReopository.save(account);
		
		//when
		UserDetailsService userDetailsService = (UserDetailsService)accountService;
		UserDetails userDetails = userDetailsService.loadUserByUsername(username);
		
		//then
		assertThat(userDetails.getPassword()).isEqualTo(password);
		assertThat(userDetails.getUsername()).isEqualTo(username);
		
	}
	
}
