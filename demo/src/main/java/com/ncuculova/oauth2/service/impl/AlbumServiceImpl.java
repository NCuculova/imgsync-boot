package com.ncuculova.oauth2.service.impl;

import com.ncuculova.oauth2.model.Album;
import com.ncuculova.oauth2.model.AlbumDTO;
import com.ncuculova.oauth2.model.Image;
import com.ncuculova.oauth2.repository.AlbumRepository;
import com.ncuculova.oauth2.repository.ImageRepository;
import com.ncuculova.oauth2.service.AlbumService;
import com.ncuculova.oauth2.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ncuculova on 28.10.15.
 */
@Service
public class AlbumServiceImpl extends BaseEntityCrudServiceImpl<Album, AlbumRepository> implements AlbumService {

    @Autowired
    private AlbumRepository repository;

    @Override
    protected AlbumRepository getRepository() {
        return repository;
    }

    @Override
    public List<Album> findByUserId(Long userId) {
        return repository.findByUserId(userId);
    }

    @Override
    public List<AlbumDTO> findAlbums(long userId) {
        List<Object[]> result = repository.findAlbums(userId);
        List<AlbumDTO> albums = new ArrayList<>();
        for(Object[] ob : result) {
            albums.add(new AlbumDTO((BigInteger) ob[1], ob[0].toString(),(BigInteger) ob[2], (BigInteger) ob[3], (Date) ob[4]));
        }
        return  albums;
    }
}
