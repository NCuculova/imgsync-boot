package com.ncuculova.oauth2.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigInteger;
import java.util.Date;

/**
 * Created by ncuculova on 17.12.15.
 */
public class AlbumDTO {

    public BigInteger id;
    public String name;
    public BigInteger latestImageId;
    public BigInteger numberImages;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    public Date dateCreated;

    public AlbumDTO(BigInteger id, String name, BigInteger latestImageId, BigInteger numberImages, Date dateCreated) {
        this.id = id;
        this.name = name;
        this.latestImageId = latestImageId;
        this.numberImages = numberImages;
        this.dateCreated = dateCreated;
    }
}
