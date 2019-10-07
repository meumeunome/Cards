package com.example.pedrotlf.cards.retrofit.requestObjects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Carta {

    private String name;
    private String set_name;
    private String lang;
    private ImageURIS image_uris;
    private ArrayList<Integer> multiverse_ids = new ArrayList<>();

    public String getSet_name() {
        if(this.lang.equals("ja"))
            return set_name + " (Japanese)";
        else
            return set_name;
    }
    public void setSet_name(String set_name) { this.set_name = set_name; }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getLang() { return lang; }
    public void setLang(String lang) { this.lang = lang; }

    public String getImage_uris() { return image_uris.toString(); }
    public void setImage_uris(ImageURIS image_uris) { this.image_uris = image_uris; }

    public int getMultiverse_id() { return this.multiverse_ids.get(0); }
    public void setMultiverse_ids(ArrayList<Integer> multiverse_ids) { this.multiverse_ids = multiverse_ids; }

    @Override
    public String toString() {
        return this.set_name;
    }
}
