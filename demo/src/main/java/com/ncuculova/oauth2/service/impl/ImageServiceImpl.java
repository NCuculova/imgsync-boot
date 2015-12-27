package com.ncuculova.oauth2.service.impl;

import com.ncuculova.oauth2.model.Image;
import com.ncuculova.oauth2.model.User;
import com.ncuculova.oauth2.repository.ImageRepository;
import com.ncuculova.oauth2.repository.UserRepository;
import com.ncuculova.oauth2.service.ImageService;
import com.ncuculova.oauth2.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by ncuculova on 28.10.15.
 */
@Service
public class ImageServiceImpl extends BaseEntityCrudServiceImpl<Image, ImageRepository> implements ImageService {

    @Autowired
    private ImageRepository repository;

    @Override
    protected ImageRepository getRepository() {
        return repository;
    }

}
