package com.example.edubridge;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
//import com.google.android.libraries.places.api.Places;
//import com.google.android.libraries.places.api.model.AutocompletePrediction;
//import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
//import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
//import com.google.android.libraries.places.api.net.PlacesClient;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class TeacherProfile extends AppCompatActivity {
    private Spinner stateSpinner, citySpinner;
    //private PlacesClient placesClient;
    private ArrayAdapter<String> stateAdapter, cityAdapter;
    private RequestQueue requestQueue;
    private final String geonamesUsername = "singhaditi2004";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_teacher_profile);
       /* if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyBTOP3oytuqzkg9Pu9uERNrBK2PR4ld15Q");
        }*/

       // placesClient = Places.createClient(this);
        stateSpinner = findViewById(R.id.stateSpinner);
        citySpinner = findViewById(R.id.citySpinner);

        stateAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>());
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stateSpinner.setAdapter(stateAdapter);
        cityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>());
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citySpinner.setAdapter(cityAdapter);
        requestQueue = Volley.newRequestQueue(this);
        fetchStates();

        stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedState = stateAdapter.getItem(position);
                if (selectedState != null) {
                    fetchCities(selectedState);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle case when nothing is selected
            }
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private void fetchStates() {
        String url = "http://api.geonames.org/childrenJSON?geonameId=1269750&username=" + geonamesUsername;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("API_RESPONSE", response.toString());
                        try {
                            JSONArray geonames = response.getJSONArray("geonames");

                            List<String> states = new ArrayList<>();
                            for (int i = 0; i < geonames.length(); i++) {
                                JSONObject obj = geonames.getJSONObject(i);
                                String stateName = obj.getString("name");
                                states.add(stateName);
                            }
                            stateAdapter.clear();
                            stateAdapter.addAll(states);
                            stateAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            // Handle JSON parsing error
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.e("JSON_ERROR", error.toString());
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

    private void fetchCities(String state) {
        String url = "http://api.geonames.org/searchJSON?formatted=true&q=" + state + "&adminCode1=&maxRows=100&username=" + geonamesUsername;


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray geonames = response.getJSONArray("geonames");
                            List<String> cities = new ArrayList<>();
                            Set<String> uniqueCities = new HashSet<>();
                            for (int i = 0; i < geonames.length(); i++) {
                                JSONObject obj = geonames.getJSONObject(i);
                                String cityName = obj.getString("name");
                                if (cityName != null && !uniqueCities.contains(cityName)) {
                                    uniqueCities.add(cityName);
                                    cities.add(cityName);
                                }
                            }
                            cityAdapter.clear();
                            cityAdapter.addAll(cities);
                            cityAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            // Handle JSON parsing error
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                // Handle error
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

}