package com.ncuculova.oauth2.service;

import com.ncuculova.oauth2.model.AlbumImage;
import com.ncuculova.oauth2.model.Image;

import java.util.List;

/**
 * Created by ncuculova on 4.12.15.
 */
public interface AlbumImageService extends BaseEntityCrudService<AlbumImage>{
    List<Image> findImagesByAlbumId(Long albumId);
    List<AlbumImage> findByAlbumId(Long albumId);
    List<AlbumImage> findByAlbumIdAndImageId(Long albumId, Long imageId);
}
