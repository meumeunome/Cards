package com.example.pedrotlf.cards.ui;

import android.app.ProgressDialog;
import android.net.Uri;
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
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
    private ImageView image;
    private Spinner set;
    private String selectedSet;
    private Spinner condition;
    private String selectedCondition;
    private EditText comment;
    private EditText quantity;
    private Button saveButton;

    ArrayList<String> setSpinnerArray;
    ArrayList<String> recommendedNameArray;
    ArrayList<Carta> cartasList;
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
        configureConditionSpinner();
        configureNameText();
    }

    private void configureConditionSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.conditionOptions, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        condition.setAdapter(adapter);

        condition.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0)
                    selectedCondition = "";
                else
                    selectedCondition = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //do nothing
            }
        });
    }

    private void populateRecommendedText() {
        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line,
                recommendedNameArray);
        name.setAdapter(adapter);
    }

    private void configureNameText() {
        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!setSpinnerArray.isEmpty()){
                    setSpinnerArray.clear();
                    populateSpinner();
                }
                setSpinnerIsPopulated = false;

                if(s.length() < 3){
                    if(cartasRecommendedNameCall != null)
                        cartasRecommendedNameCall.cancel();
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

        name.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String uri = "";
                for(Carta aux : cartasList){
                    if(aux.getName() == parent.getItemAtPosition(position))
                        uri = aux.getImage_uris();
                }
                loadImage(uri);
            }
        });

    }

    private void loadImage(String uri) {
        Glide.with(this).load(uri).into(image);
    }

    private void makeNomeCartasRequest(CharSequence s) {
        Toast.makeText(FormCardActivity.this,
                "Searching...",
                Toast.LENGTH_SHORT).show();
        cartasRecommendedNameCall = new RetrofitConfig().getScryfallService().buscarCarta(s.toString());
        cartasRecommendedNameCall.enqueue(new Callback<CartasResponse>() {
            @Override
            public void onResponse(Call<CartasResponse> call, Response<CartasResponse> response) {
                recommendedNameArray.clear();
                cartasList.clear();
                CartasResponse cartas = response.body();

                int code = response.code();
                if(code == 200){
                    for(Carta aux : cartas.getData()){
                        cartasList.add(aux);
                        recommendedNameArray.add(aux.getName());
                    }

                    if(cartas.hasNext_page()){
                        Toast.makeText(FormCardActivity.this,
                                "Query response is too long, type more to reduce the response",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        previousCardNameRequestSize = name.getText().length();
                    }

                } else{
                    Toast.makeText(FormCardActivity.this,
                            "No cards found",
                            Toast.LENGTH_SHORT).show();
                }


                populateRecommendedText();
                name.showDropDown();
            }

            @Override
            public void onFailure(Call<CartasResponse> call, Throwable t) {
                recommendedNameArray.clear();
                populateRecommendedText();
                Log.d("HMM", "What happened: " + t.getMessage() + " / " + t.getLocalizedMessage());
                if(t.getMessage().compareTo("timeout") == 0){
                    Toast.makeText(FormCardActivity.this,
                            "Timeout\nTry typing more things or retyping",
                            Toast.LENGTH_SHORT).show();
                }
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
                        setSpinnerArray.clear();
                        populateSpinner();
                        return true;
                    }

                    setSpinnerArray.clear();
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
                        setSpinnerArray.add(aux.getSet_name());
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
        ArrayList<String> hint;
        ArrayAdapter<String> spinnerArrayAdapter;
        if(setSpinnerArray.isEmpty()) {
            hint = new ArrayList<>();
            hint.add("Select set...");
            spinnerArrayAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_dropdown_item_1line,
                    hint);
        } else {
            spinnerArrayAdapter = new ArrayAdapter<>
                    (this,
                            android.R.layout.simple_spinner_item,
                            setSpinnerArray);
        }
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        set.setAdapter(spinnerArrayAdapter);
    }

    private void configureSaveButton() {
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(FormCardActivity.this,
                        name.getText() + " - " + selectedSet + " - " + selectedCondition + " - " + comment.getText() + " - " + quantity.getText(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void inicializar() {
        name = findViewById(R.id.activity_form_card_name);
        image = findViewById(R.id.activity_form_card_image);
        set = findViewById(R.id.activity_form_card_set);
        condition = findViewById(R.id.activity_form_card_condition);
        comment = findViewById(R.id.activity_form_card_comment);
        quantity = findViewById(R.id.activity_form_card_quantity);

        saveButton = findViewById(R.id.activity_form_card_save_button);

        setSpinnerArray = new ArrayList<>();
        setSpinnerIsPopulated = false;
        populateSpinner();

        recommendedNameArray = new ArrayList<>();
        cartasList = new ArrayList<>();
        previousCardNameRequestSize = 0;

    }
}
