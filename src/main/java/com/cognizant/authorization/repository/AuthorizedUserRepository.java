package com.cognizant.authorization.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cognizant.authorization.entity.AuthorizedUsers;

public interface AuthorizedUserRepository extends JpaRepository<AuthorizedUsers, String > {

}
