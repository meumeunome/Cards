package com.example.pedrotlf.cards.retrofit.requestObjects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CardFaces {

    private ImageURIS image_uris;

    public ImageURIS getImage_uris() { return image_uris; }
    public void setImage_uris(ImageURIS image_uris) { this.image_uris = image_uris; }
}
