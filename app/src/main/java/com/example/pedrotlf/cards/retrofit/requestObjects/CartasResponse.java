package com.example.pedrotlf.cards.retrofit.requestObjects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

@JsonIgnoreProperties({"object", "has_more"})
public class CartasResponse {

    private int total_cards;
    private ArrayList<Carta> data;

    public int getTotal_cards() { return total_cards; }
    public void setTotal_cards(int total_cards) { this.total_cards = total_cards; }

    public ArrayList<Carta> getData() { return data; }
    public void setData(ArrayList<Carta> data) { this.data = data; }
}
