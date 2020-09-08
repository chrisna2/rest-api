package com.hyunkee.account;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AccountService implements UserDetailsService{
	
	
	@Autowired
	AccountReopository accountReopository;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	//패스워드를 인코딩해서 사용자 계정을 저장하는 서비스
	public Account saveAccount(Account account) {
		//비밀번호 인코딩
		account.setPassword(this.passwordEncoder.encode(account.getPassword()));
		return this.accountReopository.save(account);
	}
	
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		Account account = accountReopository.findByEmail(username)
											//예외 처리
											.orElseThrow(() -> new UsernameNotFoundException(username));
		
		return new User(account.getEmail(), account.getPassword(), authorities(account.getRoles()));
	}

	private Collection<? extends GrantedAuthority> authorities(Set<AccountRole> roles) {
		return roles.stream()
				.map(r -> new SimpleGrantedAuthority("ROLE"+r.name()))
				.collect(Collectors.toSet());
	}

}
