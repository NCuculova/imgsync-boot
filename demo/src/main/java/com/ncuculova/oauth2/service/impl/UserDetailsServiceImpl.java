package com.ncuculova.oauth2.service.impl;

import com.ncuculova.oauth2.model.User;
import com.ncuculova.oauth2.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ncuculova on 29.10.15.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.findByEmail(username);
        System.out.println("load by username: " + user);
        if(user == null){
            throw new UsernameNotFoundException("User does not exist");
        }
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("USER"));
        org.springframework.security.core.userdetails.User result = new org.springframework.security.core.
                userdetails.User(user.getEmail(), user.getPassword(), authorities);
        return result;
    }
}
