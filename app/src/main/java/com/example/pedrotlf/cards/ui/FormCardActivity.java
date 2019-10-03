package com.example.pedrotlf.cards.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.pedrotlf.cards.R;
import com.example.pedrotlf.cards.retrofit.RetrofitConfig;
import com.example.pedrotlf.cards.retrofit.requestObjects.Carta;
import com.example.pedrotlf.cards.retrofit.requestObjects.CartasResponse;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FormCardActivity extends AppCompatActivity {

    private AutoCompleteTextView name;
    private Spinner set;
    private String selectedSet;
    private EditText condition;
    private EditText comment;
    private EditText quantity;
    private Button saveButton;

    ArrayList<String> spinnerArray;
    ArrayList<String> recommendedNameArray;
    private boolean setSpinnerIsPopulated;

    private Call cartasRecommendedNameCall;
    private int previousCardNameRequestSize;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_card);

        setTitle("New Card");

        inicializar();

        configureSaveButton();
        configureSelectSetDropdown();
        configureNameText();
    }

    private void populateRecommendedText(String text) {
        ArrayAdapter<String> adapter;
        if(text == null)
             adapter = new ArrayAdapter<>(this,
                     android.R.layout.simple_dropdown_item_1line,
                     recommendedNameArray);
        else {
            adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_dropdown_item_1line);

            for(String aux : recommendedNameArray){
                if(aux.contains(text)) {
                    adapter.add(aux);
                    Log.d("HMM", aux + " contains: " + text);
                }else Log.d("HMM", aux + " DOES NOT contains: " + text);
            }
        }
        adapter.setNotifyOnChange(true);

        name.setAdapter(adapter);
    }

    private void configureNameText() {
        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!spinnerArray.isEmpty()){
                    spinnerArray.clear();
                    populateSpinner();
                }
                setSpinnerIsPopulated = false;

                if(s.length() < 3){
                    previousCardNameRequestSize = 0;
                    return;
                }

                Log.d("HMM", "onTextChanged: " + start + " " + before + " " + s.length() + " " + previousCardNameRequestSize);

                if(previousCardNameRequestSize == 0 || s.length() < previousCardNameRequestSize) {
                    if(cartasRecommendedNameCall != null)
                        cartasRecommendedNameCall.cancel();
                    makeNomeCartasRequest(s);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    hideKeyboard(v);
                }
            }
        });
    }

    private void makeNomeCartasRequest(CharSequence s) {
        cartasRecommendedNameCall = new RetrofitConfig().getScryfallService().buscarCarta(s.toString());
        cartasRecommendedNameCall.enqueue(new Callback<CartasResponse>() {
            @Override
            public void onResponse(Call<CartasResponse> call, Response<CartasResponse> response) {
                recommendedNameArray.clear();
                CartasResponse cartas = response.body();
                int code = response.code();
                if(code == 200){
                    for(Carta aux : cartas.getData()){
                        recommendedNameArray.add(aux.getName());
                    }
                }
                populateRecommendedText(null);
                previousCardNameRequestSize = name.getText().length();
                name.showDropDown();
            }

            @Override
            public void onFailure(Call<CartasResponse> call, Throwable t) {
                recommendedNameArray.clear();
                populateRecommendedText(null);
                Log.d("HMM", "OIEEEEE: " + t.getMessage());
                //do nothing
            }
        });
    }

    private void hideKeyboard(View v) {
        if(v != null){
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    private void configureSelectSetDropdown() {

        set.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                hideKeyboard(v);

                if(event.getActionMasked() == MotionEvent.ACTION_UP) {

                    if (setSpinnerIsPopulated)
                        return false;

                    if (name.getText().toString().isEmpty()) {
                        Toast.makeText(FormCardActivity.this,
                                "Nenhuma carta especificada!",
                                Toast.LENGTH_SHORT).show();
                        spinnerArray.clear();
                        populateSpinner();
                        return true;
                    }

                    spinnerArray.clear();
                    populateSpinner();
                    makeCartasTodosOsSetsRequest();
                }

                return false;
            }
        });

        set.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedSet = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //To do... or do nothing
            }
        });
    }

    private void makeCartasTodosOsSetsRequest() {

        final ProgressDialog nDialog = getProgressDialog();

        String query = "!\"" + name.getText().toString() + "\"";

        Call<CartasResponse> call = new RetrofitConfig().getScryfallService().buscarCartaTodosOsSets(query);

        call.enqueue(new Callback<CartasResponse>() {
            @Override
            public void onResponse(Call<CartasResponse> call, Response<CartasResponse> response) {
                CartasResponse cartas = response.body();

                int code = response.code();
                if (code == 200) {
                    for(Carta aux : cartas.getData()){
                        spinnerArray.add(aux.getSet_name());
                    }
                } else {
                    Toast.makeText(FormCardActivity.this,
                            "Carta não encontrada!",
                            Toast.LENGTH_LONG).show();
                }

                populateSpinner();
                setSpinnerIsPopulated = true;
                nDialog.dismiss();
            }


            @Override
            public void onFailure(Call<CartasResponse> call, Throwable t) {
                Toast.makeText(FormCardActivity.this,
                        "Scryfall: Erro de comunicaçao!",
                        Toast.LENGTH_LONG).show();
                comment.setText(t.getMessage());//REMOVER ESSA LINHA AO FINALIZAR PROJETO
                nDialog.dismiss();
            }
        });
    }

    @NonNull
    private ProgressDialog getProgressDialog() {
        ProgressDialog nDialog;
        nDialog = new ProgressDialog(this);
        nDialog.setMessage("Loading..");
        nDialog.setTitle("Get Data");
        nDialog.setIndeterminate(false);
        nDialog.setCancelable(false);
        nDialog.show();
        return nDialog;
    }

    private void populateSpinner() {
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>
                (this,
                        android.R.layout.simple_spinner_item,
                        spinnerArray);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        set.setAdapter(spinnerArrayAdapter);
    }

    private void configureSaveButton() {
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(FormCardActivity.this,
                        name.getText() + " - " + selectedSet + " - " + condition.getText() + " - " + comment.getText() + " - " + quantity.getText(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void inicializar() {
        name = findViewById(R.id.activity_form_card_name);
        set = findViewById(R.id.activity_form_card_set);
        condition = findViewById(R.id.activity_form_card_condition);
        comment = findViewById(R.id.activity_form_card_comment);
        quantity = findViewById(R.id.activity_form_card_quantity);

        saveButton = findViewById(R.id.activity_form_card_save_button);

        spinnerArray = new ArrayList<>();
        setSpinnerIsPopulated = false;

        recommendedNameArray = new ArrayList<>();
        previousCardNameRequestSize = 0;

    }
}
