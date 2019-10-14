package com.example.pedrotlf.cards.ui;

import android.annotation.SuppressLint;
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
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.pedrotlf.cards.R;
import com.example.pedrotlf.cards.retrofit.RetrofitConfig;
import com.example.pedrotlf.cards.retrofit.requestObjects.Carta;
import com.example.pedrotlf.cards.retrofit.requestObjects.CartasResponse;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

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

    private Call<CartasResponse> cartasRecommendedNameCall;
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
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
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
        adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_dropdown_item_1line,
                recommendedNameArray);
        name.setAdapter(adapter);
    }

    private void configureNameText() {
        name.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }

            private Timer timer = new Timer();
            private final long DELAY = 1000;

            @Override
            public void afterTextChanged(final Editable s) {
                if(!setSpinnerArray.isEmpty()){
                    setSpinnerArray.clear();
                    cartasList.clear();
                    populateSpinner();
                }
                setSpinnerIsPopulated = false;

                timer.cancel();
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if(s.length() < 3){
                            if(cartasRecommendedNameCall != null)
                                cartasRecommendedNameCall.cancel();
                            previousCardNameRequestSize = 0;
                            return;
                        }

                        if(previousCardNameRequestSize == 0 || s.length() < previousCardNameRequestSize) {
                            if(cartasRecommendedNameCall != null)
                                cartasRecommendedNameCall.cancel();
                            makeNomeCartasRequest(s);
                        }
                    }
                }, DELAY);


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

    private void loadImage(String uri) {
        Glide.with(getContext()).load(uri).into(image);
    }

    private void makeNomeCartasRequest(CharSequence s) {
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(getContext(),
                        "Searching...",
                        Toast.LENGTH_SHORT).show();            }
        });
        cartasRecommendedNameCall = new RetrofitConfig().getScryfallService().buscarCarta(s.toString());
        cartasRecommendedNameCall.enqueue(new Callback<CartasResponse>() {
            @Override
            public void onResponse(@NonNull Call<CartasResponse> call, @NonNull Response<CartasResponse> response) {
                recommendedNameArray.clear();
                CartasResponse cartas = response.body();

                int code = response.code();
                if(code == 200){
                    for(Carta aux : cartas.getData()){
                        recommendedNameArray.add(aux.getName());
                    }

                    if(cartas.hasNext_page()){
                        Toast.makeText(getContext(),
                                "Query response is too long, type more to reduce the response",
                                Toast.LENGTH_LONG).show();
                    } else {
                        previousCardNameRequestSize = name.getText().length();
                    }

                } else{
                    Toast.makeText(getContext(),
                            "No cards found",
                            Toast.LENGTH_SHORT).show();
                }


                populateRecommendedText();
                name.showDropDown();
            }

            @Override
            public void onFailure(@NonNull Call<CartasResponse> call, @NonNull Throwable t) {
                recommendedNameArray.clear();
                populateRecommendedText();
                Log.d("HMM", "What happened: " + t.getMessage() + " / " + t.getLocalizedMessage());
                if(t.getMessage().compareTo("timeout") == 0){
                    Toast.makeText(getContext(),
                            "Timeout\nTry typing more or retyping",
                            Toast.LENGTH_SHORT).show();
                }
                //do nothing
            }
        });
    }

    @NonNull
    private FormCardActivity getContext() {
        return FormCardActivity.this;
    }

    private void hideKeyboard(View v) {
        if(v != null){
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(getContext().INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    private void configureSelectSetDropdown() {

        set.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                hideKeyboard(v);

                if(event.getActionMasked() == MotionEvent.ACTION_UP) {

                    if (setSpinnerIsPopulated)
                        return false;

                    if (name.getText().toString().isEmpty()) {
                        Toast.makeText(getContext(),
                                "Nenhuma carta especificada!",
                                Toast.LENGTH_SHORT).show();
                        setSpinnerArray.clear();
                        cartasList.clear();
                        populateSpinner();
                        return true;
                    }

                    setSpinnerArray.clear();
                    cartasList.clear();
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
                if(cartasList.size() > 0)
                    loadImage(cartasList.get(position).getImage_uris());
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
            public void onResponse(@NonNull Call<CartasResponse> call, @NonNull Response<CartasResponse> response) {
                CartasResponse cartas = response.body();

                int code = response.code();
                if (code == 200) {
                    for(Carta aux : cartas.getData()){
                        setSpinnerArray.add(aux.getSet_name());
                        cartasList.add(aux);
                    }
                } else {
                    Toast.makeText(getContext(),
                            "Carta não encontrada!",
                            Toast.LENGTH_LONG).show();
                }

                populateSpinner();
                setSpinnerIsPopulated = true;
                nDialog.dismiss();
            }


            @Override
            public void onFailure(Call<CartasResponse> call, Throwable t) {
                Toast.makeText(getContext(),
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
        nDialog = new ProgressDialog(getContext());
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
            spinnerArrayAdapter = new ArrayAdapter<>(getContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    hint);
        } else {
            spinnerArrayAdapter = new ArrayAdapter<>
                    (getContext(),
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
                Toast.makeText(getContext(),
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
