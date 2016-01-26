package com.ncuculova.oauth2.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.ncuculova.oauth2.model.*;
import com.ncuculova.oauth2.service.AlbumImageService;
import com.ncuculova.oauth2.service.AlbumService;
import com.ncuculova.oauth2.service.ImageService;
import com.ncuculova.oauth2.service.UserService;
import com.ncuculova.oauth2.util.StringUtils;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.sql.rowset.serial.SerialBlob;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * RESTful API for Android mobile client
 */
@RestController
@RequestMapping(value = "/api", produces = "application/json")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private AlbumService albumService;

    @Autowired
    private AlbumImageService albumImageService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public Map<String, String> dummyRequest() {
        return Collections.singletonMap("response", "OK");
    }

    @RequestMapping(value = "/fb_login", method = RequestMethod.POST)
    public User signInWithFb(@RequestParam("token") String token,
                             @RequestParam("userId") String userId,
                             HttpServletResponse response) {
        String getFbNodeURL = "https://graph.facebook.com/v2.5/";
        RestTemplate client = new RestTemplate();
        JsonNode node = client.getForObject(String.format("%s%s?fields=email&access_token=%s", getFbNodeURL,
                        userId, token),
                JsonNode.class);
        JsonNode picture = client.getForObject(String.format("%s%s/picture?type=large&redirect=false&access_token=%s",
                getFbNodeURL, userId, token), JsonNode.class);
        String username = node.get("email").asText();
        String userProfilePictureUrl = picture.get("data").get("url").asText();
        User user = userService.findByEmail(username);
        if (user == null) {
            user = new User();
            user.setEmail(username);
            user.setPassword(StringUtils.generateRandomPassword());
            response.setStatus(HttpServletResponse.SC_CREATED);
        }
        user.setPictureUrl(userProfilePictureUrl);
        userService.save(user);
        return user;
    }

    @RequestMapping(value = "/sign_up", method = RequestMethod.POST)
    public User signUp(@RequestParam("username") String username, @RequestParam("password") String password,
                       HttpServletResponse response) {
        User user = userService.findByEmail(username);
        if (user != null) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            return null;
        } else {
            user = new User();
            user.setEmail(username);
            user.setPassword(password);
            userService.save(user);
            response.setStatus(HttpServletResponse.SC_CREATED);
        }
        return user;
    }

    @RequestMapping(value = "/albums/my", method = RequestMethod.GET)
    public List<AlbumDTO> getUserAlbumsInfo() {
        User user = userService.findCurrentUser();
        List<AlbumDTO> albums = albumService.findAlbums(user.getId());
        return albums;
    }

    @RequestMapping(value = "/albums", method = RequestMethod.POST)
    public Album createAlbum(@RequestParam("name") String name) {
        User user = userService.findCurrentUser();
        Album album = new Album();
        album.setName(name);
        album.setUser(user);
        album.setDateCreated(new Date());
        albumService.save(album);
        return album;
    }

    @RequestMapping(value = "/albums/{albumId}", method = RequestMethod.PUT)
    public Album updateAlbum(@RequestBody MultiValueMap<String, String> body, @PathVariable Long albumId) {
        Album album = albumService.findOne(albumId);
        album.setName(body.getFirst("name"));
        albumService.save(album);
        return album;
    }

    @RequestMapping(value = "/albums/{albumId}", method = RequestMethod.DELETE)
    public Map<String, String> deleteAlbum(@PathVariable Long albumId) {
        List<Image> images = albumImageService.findImagesByAlbumId(albumId);
        List<AlbumImage> albumImages = albumImageService.findByAlbumId(albumId);
        for (AlbumImage ai : albumImages) {
            albumImageService.delete(ai.getId());
        }
        for (Image img : images) {
            imageService.delete(img.getId());
        }
        albumService.delete(albumId);
        return Collections.singletonMap("response", "album deleted");
    }

    @RequestMapping(value = "/albums/{albumId}/images", method = RequestMethod.GET)
    public List<Image> getAlbumImages(@PathVariable Long albumId) {
        List<Image> images = albumImageService.findImagesByAlbumId(albumId);
        return images;
    }

    @RequestMapping(value = "/album/{albumId}/image/{imageId}", method = RequestMethod.DELETE)
    public Map<String, String> deleteImage(@PathVariable Long albumId, @PathVariable Long imageId) {
        List<AlbumImage> albumImages = albumImageService.findByAlbumIdAndImageId(albumId, imageId);
        for (AlbumImage ai : albumImages) {
            albumImageService.delete(ai);
        }
        imageService.delete(imageId);
        return Collections.singletonMap("response", "image deleted");
    }


    @RequestMapping("/image/{id}")
    public Image downloadImageById(@PathVariable("id") Long id,
                                   HttpServletResponse response) {
        System.out.println("Download image");
        Image image = imageService.findOne(id);
        writeFileToResponse(image, image.getImage(), response);
        return image;
    }

    @RequestMapping("/image_thumb/{id}")
    public Image downloadImageThumbById(@PathVariable("id") Long id,
                                        HttpServletResponse response) {
        Image image = imageService.findOne(id);
        writeFileToResponse(image, image.getThumbnail(), response);
        return image;
    }

    private void writeFileToResponse(Image image, Blob blob,
                                     HttpServletResponse response) {
        try {
            OutputStream out = response.getOutputStream();
            response.setContentType(image.getFileType());
            response.setContentLength((int) blob.length());
            response.setHeader("Cache-control", "public,max-age=86400");
            response.setHeader("Pragma", "cache");
            IOUtils.copy(blob.getBinaryStream(), out);
            out.flush();
            //out.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/albums/{albumId}/image", method = RequestMethod.POST)
    // the method return value should be bound to the web response body
    @ResponseBody
    public Image uploadFile(MultipartFile file, @PathVariable Long albumId)
            throws IOException, SQLException {
        Album album = albumService.findOne(albumId);
        Image image = new Image();
        AlbumImage albumImage = new AlbumImage();
        albumImage.setAlbum(album);
        image.setImage(new SerialBlob(file.getBytes()));
        image.setFileName(file.getOriginalFilename());
        image.setFileType(file.getContentType());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Thumbnails.of(file.getInputStream()).size(500, 500).toOutputStream(outputStream);
        image.setThumbnail(new SerialBlob(outputStream.toByteArray()));
        imageService.save(image);
        albumImage.setImage(image);
        albumImageService.save(albumImage);
        return image;
    }
}
