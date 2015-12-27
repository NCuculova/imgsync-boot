package com.ncuculova.oauth2.repository;

import com.ncuculova.oauth2.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by ncuculova on 28.10.15.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);
}
