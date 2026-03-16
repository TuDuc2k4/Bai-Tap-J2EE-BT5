package com.example.bai5_qlsp.repository;

import com.example.bai5_qlsp.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Integer> {

	@Query("SELECT a FROM Account a WHERE a.loginName = :loginName")
	Optional<Account> findByLoginName(@Param("loginName") String loginName);
}

