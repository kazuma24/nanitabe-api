package com.fact.nanitabe.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.fact.nanitabe.infrastructure.entity.Users;

public interface UsersRepository extends JpaRepository<Users, Integer> {

	// emailで検索するクエリ
	@Query("SELECT u FROM Users u WHERE u.email = :email")
	Users findByEmail(String email);
	
	// emailとパスワードで検索するクエリ
	@Query("SELECT u.userId FROM Users u WHERE u.email = :email AND  u.password = :password")
	Users findByEmailAndPassword(String email, String password);

}
