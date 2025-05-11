package com.example.edubridge.School;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.edubridge.R;
import com.example.edubridge.UserRole;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SchoolProfile extends AppCompatActivity {
    private Spinner stateSpinner, citySpinner;
    private ArrayAdapter<String> stateAdapter, cityAdapter;
    private FirebaseAuth mAuth;
    private RequestQueue requestQueue;
    private final String geonamesUsername = "singhaditi2004";
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private ImageView schoolImageView;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private com.google.android.material.button.MaterialButton saveBtn;
    private FirebaseFirestore firestore;
    public static String emailIdAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_school_profile);

        firestore = FirebaseFirestore.getInstance();

        // Initialize views
        stateSpinner = findViewById(R.id.stateSpinnerSchool);
        citySpinner = findViewById(R.id.citySpinnerSchool);
        schoolImageView = findViewById(R.id.schoolImage);
        ImageView addImageSchool = findViewById(R.id.addImageSchool);
        saveBtn = findViewById(R.id.saveBtn);

        // Initialize Firebase components
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference().child("school_images");
        mAuth = FirebaseAuth.getInstance();
        requestQueue = Volley.newRequestQueue(this);

        // Setup spinners
        setupSpinners();

        // Set click listeners
        addImageSchool.setOnClickListener(v -> openFileChooser());
        saveBtn.setOnClickListener(v -> uploadImageToFirebase());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setupSpinners() {
        stateAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>());
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stateSpinner.setAdapter(stateAdapter);

        cityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>());
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citySpinner.setAdapter(cityAdapter);

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
    }

    private void fetchStates() {
        String url = "http://api.geonames.org/childrenJSON?geonameId=1269750&username=" + geonamesUsername;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
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
                            Toast.makeText(SchoolProfile.this, "Error parsing states data", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(SchoolProfile.this, "Error fetching states", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        requestQueue.add(jsonObjectRequest);
    }

    private void fetchCities(String state) {
        String url = "http://api.geonames.org/searchJSON?formatted=true&q=" + state +
                "&adminCode1=&maxRows=100&username=" + geonamesUsername;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
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
                            Toast.makeText(SchoolProfile.this, "Error parsing cities data", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(SchoolProfile.this, "Error fetching cities", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        requestQueue.add(jsonObjectRequest);
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            schoolImageView.setImageURI(imageUri);
        }
    }

    private void uploadImageToFirebase() {
        if (imageUri != null) {
            String email = mAuth.getCurrentUser().getEmail();
            String encodedEmail = encodeEmail(email);
            emailIdAuth = encodedEmail;
            StorageReference fileReference = storageReference.child(encodedEmail + "." + getFileExtension(imageUri));

            fileReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    fileReference.putFile(imageUri)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String imageUrl = uri.toString();
                                            saveImageUrlToDatabase(imageUrl);
                                            Picasso.get().load(imageUrl).into(schoolImageView);
                                            saveSchoolDataToFirestore(imageUrl);
                                        }
                                    });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    saveSchoolDataToFirestore("defaultImageUrl");
                                    Toast.makeText(SchoolProfile.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            });
        } else {
            schoolImageView.setImageResource(R.drawable.schoool);
            Uri defaultImageUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.schoool);
            saveSchoolDataToFirestore(defaultImageUri.toString());
        }
    }

    private void saveImageUrlToDatabase(String imageUrl) {
        String email = mAuth.getCurrentUser().getEmail();
        String encodedEmail = encodeEmail(email);
        databaseReference = FirebaseDatabase.getInstance().getReference("schools").child(encodedEmail);
        databaseReference.child("profileImageUrl").setValue(imageUrl);
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();

        String email = mAuth.getCurrentUser().getEmail();
        String encodedEmail = encodeEmail(email);
        emailIdAuth = encodedEmail;
        databaseReference = FirebaseDatabase.getInstance().getReference("schools").child(encodedEmail);

        databaseReference.child("profileImageUrl").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String savedImageUrl = dataSnapshot.getValue(String.class);
                if (savedImageUrl != null && !savedImageUrl.isEmpty()) {
                    Picasso.get()
                            .load(savedImageUrl)
                            .error(R.drawable.schoool)
                            .into(schoolImageView);
                } else {
                    schoolImageView.setImageResource(R.drawable.schoool);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(SchoolProfile.this, "Failed to load school image.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String encodeEmail(String email) {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    private void saveSchoolDataToFirestore(String imageUrl) {
        mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser().getUid();

        // Get all input fields
        TextInputEditText nameSchool = findViewById(R.id.nameSchool);
        TextInputEditText phoneSchool = findViewById(R.id.phoneSchool);
        TextInputEditText addressSchool = findViewById(R.id.addressSchool);
        TextInputEditText webLinkSchool = findViewById(R.id.webLinkSchool);
        EditText bio = findViewById(R.id.bio);

        // Create school data map
        Map<String, Object> schoolData = new HashMap<>();
        schoolData.put("name", nameSchool.getText().toString().trim());
        schoolData.put("phone", phoneSchool.getText().toString().trim());
        schoolData.put("address", addressSchool.getText().toString().trim());
        schoolData.put("website", webLinkSchool.getText().toString().trim());
        schoolData.put("bio", bio.getText().toString().trim());
        schoolData.put("state", stateSpinner.getSelectedItem().toString());
        schoolData.put("city", citySpinner.getSelectedItem().toString());
        schoolData.put("profileImageUrl", imageUrl);

        UserRole.saveUserRole(this, "school");

        // Save to "schools" collection in Firestore
        firestore.collection("schools").document(userId)
                .set(schoolData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "School profile saved successfully!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, SchoolHome.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error saving school profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    public static String getEmailIdAuth() {
        return emailIdAuth;
    }

    public static void setEmailIdAuth(String emailIdAuth) {
        SchoolProfile.emailIdAuth = emailIdAuth;
    }


}