package com.example.edubridge;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.provider.OpenableColumns;
import android.view.ViewGroup;
import android.graphics.Color;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
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

import de.hdodenhof.circleimageview.CircleImageView;


public class TeacherProfile extends AppCompatActivity {
    private Spinner stateSpinner, citySpinner, experienceSpinner;
    //private PlacesClient placesClient;
    private ArrayAdapter<String> stateAdapter, cityAdapter;
    private FirebaseAuth mAuth;
    private RequestQueue requestQueue;
    private final String geonamesUsername = "singhaditi2004";
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private CircleImageView profileImageView;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private AppCompatButton saveBtn;
    private static final int PICK_PDF_FILE = 2;
    private TextView selectedFileName;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_teacher_profile);
       /* if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyBTOP3oytuqzkg9Pu9uERNrBK2PR4ld15Q");
        }*/
        firestore = FirebaseFirestore.getInstance();

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
        profileImageView = findViewById(R.id.profile_image);
        ImageView addIcon = findViewById(R.id.add_icon);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference().child("profile_images");

        addIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });
        experienceSpinner = findViewById(R.id.experience);
        List<String> experienceLevels = new ArrayList<>();
        experienceLevels.add("Select Experience Level"); // Add hint as the first item
        experienceLevels.add("Fresher");
        experienceLevels.add("1-3 years");
        experienceLevels.add("3-5 years");
        experienceLevels.add("5+ years");
        ArrayAdapter<String> experienceAdapter = new ArrayAdapter<String>(this, R.layout.custom_spinner_item, experienceLevels) {
            @Override
            public boolean isEnabled(int position) {
                // Disable the first item from Spinner
                // First item is display hint
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

// Apply the custom adapter to the Spinner
        experienceAdapter.setDropDownViewResource(R.layout.custom_spinner_item);
        experienceSpinner.setAdapter(experienceAdapter);
        saveBtn = findViewById(R.id.saveBtn);

        TextView uploadResumeButton = findViewById(R.id.upload_resume_button);
        selectedFileName = findViewById(R.id.selected_file_name);

        uploadResumeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFilePicker();
            }
        });
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImageToFirebase();
                UserRole.saveUserRole(TeacherProfile.this, "teacher");
                Intent i=new Intent(TeacherProfile.this, TeacherHome.class);
                startActivity(i);
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
            profileImageView.setImageURI(imageUri);  // Preview the selected image

        } else {
            profileImageView.setImageResource(R.drawable.user);
        }
        if (requestCode == PICK_PDF_FILE && resultCode == RESULT_OK) {
            if (data != null) {
                Uri pdfUri = data.getData();

                // Get the file name from the Uri
                String fileName = getFileNameFromUri(pdfUri);

                // Display the file name in the TextView
                selectedFileName.setText(fileName);
            }
        }
    }

    private void uploadImageToFirebase() {
        if (imageUri != null) {
            mAuth = FirebaseAuth.getInstance();
            String email = mAuth.getCurrentUser().getEmail();
            String encodedEmail = encodeEmail(email);
            StorageReference fileReference = storageReference.child(encodedEmail + "." + getFileExtension(imageUri));

            // Delete the old image before uploading the new one
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
                                            Picasso.get().load(imageUrl).into(profileImageView);
                                            saveProfileDataToFirestore(imageUrl);
                                        }
                                    });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    saveProfileDataToFirestore("defaultImageUrl");
                                    Toast.makeText(TeacherProfile.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            });
        } else {
            profileImageView.setImageResource(R.drawable.user);
            Uri defaultImageUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.user);
            // Save profile with default image URI
            saveProfileDataToFirestore(defaultImageUri.toString());

        }
    }

    private void saveImageUrlToDatabase(String imageUrl) {
        String email = mAuth.getCurrentUser().getEmail();
        String encodedEmail = encodeEmail(email);
        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(encodedEmail);
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

        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(encodedEmail);

        // Fetch the profile image URL from the database
        databaseReference.child("profileImageUrl").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String savedImageUrl = dataSnapshot.getValue(String.class);
                if (savedImageUrl != null && !savedImageUrl.isEmpty()) {
                    Picasso.get()
                            .load(savedImageUrl)
                            .error(R.drawable.user) // Default image if the URL is invalid
                            .into(profileImageView);
                } else {
                    profileImageView.setImageResource(R.drawable.user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(TeacherProfile.this, "Failed to load profile image.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String encodeEmail(String email) {
        return email.replace(".", ",");
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("application/pdf");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, PICK_PDF_FILE);
    }

    // Helper method to extract the file name from Uri
    private String getFileNameFromUri(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (index >= 0) {
                    result = cursor.getString(index);
                }
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private void saveProfileDataToFirestore(String imageUrl) {
        mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser().getUid();
        TextInputEditText naame=findViewById(R.id.name);
        TextInputEditText phone1=findViewById(R.id.phone);
        // Get name and location from EditTexts (or any input source you are using)
        String name = naame.getText().toString().trim();
        String phone = phone1.getText().toString().trim();

        // Create a map to store the user data
        Map<String, Object> userData = new HashMap<>();
        userData.put("name", name);
        userData.put("phone", phone);
        userData.put("profileImageUrl", imageUrl);

        // Reference to Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Save data to Firestore under the user's document
        db.collection("users").document(userId)
                .set(userData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(TeacherProfile.this, "Profile saved successfully!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(TeacherProfile.this, "Error saving profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }



}