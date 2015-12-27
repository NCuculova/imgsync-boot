package com.ncuculova.oauth2.service;

import com.ncuculova.oauth2.model.User;

/**
 * Created by ncuculova on 28.10.15.
 */
public interface UserService extends BaseEntityCrudService<User>{

    User findByEmail(String email);
    User findCurrentUser();
}
