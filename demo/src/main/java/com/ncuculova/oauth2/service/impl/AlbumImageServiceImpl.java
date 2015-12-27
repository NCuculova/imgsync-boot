package com.ncuculova.oauth2.service.impl;

import com.ncuculova.oauth2.model.AlbumImage;
import com.ncuculova.oauth2.model.Image;
import com.ncuculova.oauth2.repository.AlbumImageRepository;
import com.ncuculova.oauth2.service.AlbumImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by ncuculova on 28.10.15.
 */
@Service
public class AlbumImageServiceImpl extends BaseEntityCrudServiceImpl<AlbumImage, AlbumImageRepository>
        implements AlbumImageService {

    @Autowired
    private AlbumImageRepository repository;

    @Override
    protected AlbumImageRepository getRepository() {
        return repository;
    }

    @Override
    public List<Image> findImagesByAlbumId(Long albumId) {
        return repository.findImagesByAlbumId(albumId);
    }

    @Override
    public List<AlbumImage> findByAlbumId(Long albumId) {
        return repository.findByAlbumId(albumId);
    }

    @Override
    public List<AlbumImage> findByAlbumIdAndImageId(Long albumId, Long imageId) {
        return repository.findByAlbumIdAndImageId(albumId, imageId);
    }
}
