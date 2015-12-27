package com.ncuculova.oauth2.service.impl;

import com.ncuculova.oauth2.model.User;
import com.ncuculova.oauth2.repository.UserRepository;
import com.ncuculova.oauth2.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Created by ncuculova on 28.10.15.
 */
@Service
public class UserServiceImpl extends BaseEntityCrudServiceImpl<User, UserRepository> implements UserService {

    @Autowired
    private UserRepository repository;

    @Override
    protected UserRepository getRepository() {
        return repository;
    }

    @Override
    public User findByEmail(String email) {
        return repository.findByEmail(email);
    }


    static org.springframework.security.core.userdetails.User getCurrentUser() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        if (authentication != null) {
            if (authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.User) {
                return (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
            }
        }
        throw new IllegalStateException("User not found!");
    }

    @Override
    public User findCurrentUser() {
        org.springframework.security.core.userdetails.User signedUser = getCurrentUser();
        String userName = signedUser.getUsername();
        return findByEmail(userName);
    }
}
