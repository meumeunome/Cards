package com.example.pedrotlf.cards.retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class RetrofitConfig {

    private final Retrofit retrofit;

    public RetrofitConfig(){
        this.retrofit = new Retrofit.Builder()
                .baseUrl("https://api.scryfall.com/")
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
    }

    public ScryfallService getScryfallService(){
        return this.retrofit.create(ScryfallService.class);
    }
}
