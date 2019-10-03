package com.example.pedrotlf.cards.retrofit;

import com.example.pedrotlf.cards.retrofit.requestObjects.Carta;
import com.example.pedrotlf.cards.retrofit.requestObjects.CartasResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ScryfallService {

    @GET("cards/search")
    Call<CartasResponse> buscarCarta(@Query("q") String nome);

    @GET("cards/search?unique=prints&order=released")
    Call<CartasResponse> buscarCartaTodosOsSets(@Query("q") String nome);
}
