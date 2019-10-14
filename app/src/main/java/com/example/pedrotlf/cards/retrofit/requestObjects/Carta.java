package com.example.pedrotlf.cards.retrofit.requestObjects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Carta {

    private String name;
    private String set_name;
    private String lang;
//    private boolean foil;
    private ImageURIS image_uris;
    private ArrayList<CardFaces> card_faces;
    private ArrayList<Integer> multiverse_ids = new ArrayList<>();

    public String getSet_name() {
        String finalName = "".concat(set_name);
        if(this.lang.equals("ja"))
            finalName = finalName.concat(" (Japanese)");
//        if(this.foil)
//            finalName = finalName.concat(" (Foil)");

        return finalName;
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

//    public boolean isFoil() { return foil; }
//    public void setFoil(boolean foil) { this.foil = foil; }

    public String getImage_uris() {
        if (this.card_faces == null)
            return image_uris.toString();
        else
            return card_faces.get(0).getImage_uris().toString();
    }
    public void setImage_uris(ImageURIS image_uris) { this.image_uris = image_uris; }

    public ArrayList<CardFaces> getCard_faces() { return card_faces; }
    public void setCard_faces(ArrayList<CardFaces> card_faces) { this.card_faces = card_faces; }

    public int getMultiverse_id() { return this.multiverse_ids.get(0); }
    public void setMultiverse_ids(ArrayList<Integer> multiverse_ids) { this.multiverse_ids = multiverse_ids; }

    @Override
    public String toString() {
        return this.set_name;
    }
}
