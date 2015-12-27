package com.ncuculova.oauth2.model;


import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "album_image")
public class AlbumImage extends BaseEntity{

    @ManyToOne
    private Album album;

    @ManyToOne
    private Image image;

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }
}
