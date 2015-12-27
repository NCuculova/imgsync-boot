package com.ncuculova.oauth2.repository;

import com.ncuculova.oauth2.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by ncuculova on 4.12.15.
 */
public interface ImageRepository extends JpaRepository<Image, Long> {
}
