package com.ncuculova.oauth2.service;

import com.ncuculova.oauth2.model.Album;
import com.ncuculova.oauth2.model.AlbumDTO;

import java.util.List;

/**
 * Created by ncuculova on 4.12.15.
 */
public interface AlbumService extends BaseEntityCrudService<Album> {
    List<Album> findByUserId(Long userId);
    List<AlbumDTO> findAlbums(long userId);
}
