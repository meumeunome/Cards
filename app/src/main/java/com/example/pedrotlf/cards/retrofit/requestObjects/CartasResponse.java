package com.example.pedrotlf.cards.retrofit.requestObjects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

@JsonIgnoreProperties({"object", "has_more", })
public class CartasResponse {

    private int total_cards;
    private ArrayList<Carta> data;
    private String next_page;

    public int getTotal_cards() { return total_cards; }
    public void setTotal_cards(int total_cards) { this.total_cards = total_cards; }

    public ArrayList<Carta> getData() { return data; }
    public void setData(ArrayList<Carta> data) { this.data = data; }

    public String getNext_page() { return next_page; }
    public void setNext_page(String next_page) { this.next_page = next_page; }
    public boolean hasNext_page() { return (this.next_page != null); }
}
