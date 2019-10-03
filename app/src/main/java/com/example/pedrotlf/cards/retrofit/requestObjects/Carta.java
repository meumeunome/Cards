package com.example.pedrotlf.cards.retrofit.requestObjects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

@JsonIgnoreProperties({"object", "id", "oracle_id", "mtgo_id", "arena_id",
        "tcgplayer_id", "lang", "released_at", "uri", "scryfall_uri", "layout", "highres_image",
        "image_uris", "mana_cost", "cmc", "type_line", "oracle_text", "colors", "color_identity",
        "legalities", "games", "reserved", "foil", "nonfoil", "oversized", "promo", "reprint",
        "variation", "set", "set_type", "set_uri", "set_search_uri", "scryfall_set_uri",
        "rulings_uri", "prints_search_uri", "collector_number", "digital", "rarity", "flavor_text",
        "card_back_id", "artist", "artist_ids", "illustration_id", "border_color", "frame",
        "full_art", "textless", "booster", "story_spotlight", "edhrec_rank", "prices",
        "related_uris", "purchase_uris", "mtgo_foil_id", "watermark", "preview", "power",
        "toughness", "all_parts", "frame_effects", "promo_types", "printed_name", "card_faces",
        "loyalty"})
public class Carta {

    private String name;
    private String set_name;
    private ArrayList<Integer> multiverse_ids = new ArrayList<>();

    public String getSet_name() {
        return set_name;
    }
    public void setSet_name(String set_name) {
        this.set_name = set_name;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public int getMultiverse_id() { return this.multiverse_ids.get(0); }
    public void setMultiverse_ids(ArrayList<Integer> multiverse_ids) { this.multiverse_ids = multiverse_ids; }

    @Override
    public String toString() {
        return this.set_name;
    }
}
