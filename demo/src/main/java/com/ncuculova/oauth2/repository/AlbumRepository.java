package com.ncuculova.oauth2.repository;

import com.ncuculova.oauth2.model.Album;
import com.ncuculova.oauth2.model.AlbumDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.NamedNativeQuery;
import java.util.List;

/**
 * Created by ncuculova on 4.12.15.
 */
public interface AlbumRepository extends JpaRepository<Album, Long>{
    List<Album> findByUserId(Long userId);

    @Query(nativeQuery = true, value = "SELECT a.name as name, a.id as id, " +
            "max(ai.image_id) as latestImageId," +
            " count(ai.image_id) as numberImages," +
            "a.date_created as dateCreated\n"+
            "FROM oauth2.album as a \n" +
            "left join  oauth2.album_image as ai\n" +
            "on a.id = ai.album_id\n" +
            "where a.user_id = ?1\n" +
            "group by a.id", name = "AlbumDTOMapping")
    List<Object[]> findAlbums(long userId);
}
