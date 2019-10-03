package com.example.pedrotlf.cards.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.pedrotlf.cards.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private View newCardButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inicializar();

        configureNewCardButton(newCardButton);

        List<String> cards = new ArrayList<>(
                Arrays.asList("1", "2", "3")
        );
        ListView activity_main_cards_list = findViewById(R.id.activity_main_cards_list);
        activity_main_cards_list.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, cards));

    }

    private void inicializar() {
        newCardButton = findViewById(R.id.activity_main_fab_new_card);
    }

    private void configureNewCardButton(View newCardButton) {
        newCardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, FormCardActivity.class));
            }
        });
    }

}
