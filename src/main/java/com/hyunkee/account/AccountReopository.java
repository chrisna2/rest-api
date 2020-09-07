package com.hyunkee.account;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountReopository extends JpaRepository<Account, Integer>{

	Optional<Account> findByEmail(String username);

}
