package com.ncuculova.oauth2.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Blob;

/**
 * Created by ncuculova on 4.12.15.
 */
@Entity
@Table(name = "image")
public class Image extends BaseEntity {

    @JsonIgnore
    private Blob image;

    @JsonIgnore
    private Blob thumbnail;

    private String fileName;

    private String fileType;

    public Blob getImage() {
        return image;
    }

    public void setImage(Blob image) {
        this.image = image;
    }

    public Blob getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Blob thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

}
