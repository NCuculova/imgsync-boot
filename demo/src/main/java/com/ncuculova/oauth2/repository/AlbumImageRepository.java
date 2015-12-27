package com.ncuculova.oauth2.repository;

import com.ncuculova.oauth2.model.AlbumImage;
import com.ncuculova.oauth2.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by ncuculova on 4.12.15.
 */
public interface AlbumImageRepository extends JpaRepository<AlbumImage, Long> {

    @Query("select ai.image from AlbumImage ai where ai.album.id=:albumId")
    List<Image> findImagesByAlbumId(@Param("albumId") Long albumId);

    List<AlbumImage> findByAlbumId(Long albumId);

    List<AlbumImage> findByAlbumIdAndImageId(Long albumId, Long imageId);
}
