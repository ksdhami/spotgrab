package com.spotgrab;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;
import android.support.annotation.NonNull;
import android.content.Intent;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.*;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.spotgrab.models.*;

import java.io.IOException;


public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    private Context mContext;

    private Spinner spinnerAccount;
    private String accountType, email, password, confirmPassword, fName, lName, phone, address, franchise, userID;

    // default profile image icon
    private String defaultProfileImageUrl = "https://firebasestorage.googleapis.com/v0/b/spotgrab-4f2cf.appspot.com/o/images%2Fprofile.png?alt=media&token=8fe8b4bf-1df0-4e82-bbce-35be6090a5c4";

    private Button nextButton, addPicButton;
    private ImageView profilePic;
    private EditText mEmail, mPassword, mConfirmPassword, mFName, mLName, mPhone, mAddress, mFranchise;

    private final int PICK_IMAGE_REQUEST = 71;
    private Uri filePath;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseStorage mStorage;
    private StorageReference mStorageRef;
    private FirebaseFirestore db;

    private User user;

    //private UUID imageStorageID = UUID.randomUUID();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mContext = RegisterActivity.this;

        Log.d(TAG, "\nonCreate: started.\n");

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        setupFirebaseAuth();

        db = FirebaseFirestore.getInstance();

        init();

    }

    private void init() {
        Log.d(TAG, "\ninit: started.\n");

        // Next Button
        nextButton = findViewById(R.id.nextButton);
        addPicButton = findViewById(R.id.addPicButton);

        // Image View
        profilePic = findViewById(R.id.profileImage);

        // Edit Text Fields
        mEmail = findViewById(R.id.etEmail);
        mPassword = findViewById(R.id.etPassword);
        mConfirmPassword = findViewById(R.id.etConfirmPassword);
        mFName = findViewById(R.id.etFName);
        mLName = findViewById(R.id.etLName);
        mPhone = findViewById(R.id.etPhone);
        mAddress = findViewById(R.id.etAddress);
        mFranchise = findViewById(R.id.etFranchise);

        // Dropdown Menu Created
        spinnerAccount = findViewById(R.id.accountTypeSpinner);
        ArrayAdapter<String> accountAdapter = new ArrayAdapter<>(RegisterActivity.this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.Account));
        accountAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAccount.setAdapter(accountAdapter);
//        spinnerAccount.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });



        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                accountType = spinnerAccount.getSelectedItem().toString();
                email = mEmail.getText().toString();
                password = mPassword.getText().toString();
                confirmPassword = mConfirmPassword.getText().toString();
                fName = mFName.getText().toString();
                lName = mLName.getText().toString();
                phone = mPhone.getText().toString();
                address = mAddress.getText().toString();
                franchise = mFranchise.getText().toString();



                if(checkInputs(accountType, email, password, confirmPassword, fName, lName, phone, address, franchise)){
                    //uploadImage();
                    registerNewEmail(email, password);

                }


            }
        });

        addPicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

    }


    // ------------------------------------------------- TEXT FIELD INPUT CHECK ------------------------------------------------- //


    private boolean checkInputs(String accountType, String email, String password, String confirmPassword, String fName, String lName, String phone, String address, String franchise){
        Log.d(TAG, "checkInputs: checking inputs for null values.");

        if(accountType.equals("") || email.equals("") || password.equals("") || confirmPassword.equals("") || fName.equals("") || lName.equals("") || phone.equals("") || address.equals("") || franchise.equals("")){
            Toast.makeText(mContext, "All fields must be filled out.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!doStringsMatch(password, confirmPassword)) {
            Toast.makeText(RegisterActivity.this, "Passwords do not Match", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }


    private static boolean doStringsMatch(String s1, String s2){
        return s1.equals(s2);
    }


    // -------------------------------------------------  ------------------------------------------------- //




    // ------------------------------------------------- UPLOAD PROFILE PIC ------------------------------------------------- //


    private void uploadImage(String uid) {
        Log.d(TAG, "uploadedImage");
        if(filePath != null) {
            //final ProgressDialog progressDialog = new ProgressDialog(this);
            //progressDialog.setTitle("Uploading ...");
            //progressDialog.show();

            final StorageReference imageRef = mStorageRef.child("images/" + uid);

            imageRef.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //progressDialog.dismiss();
                    Toast.makeText(mContext, "Uploaded", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "image uploaded");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //progressDialog.dismiss();
                    Toast.makeText(mContext, "Failure to Upload", Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    //double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                    //progressDialog.setMessage("Uploaded " + (int)progress + "%");
                }
            });

        }
    }

    private void chooseImage() {
        Log.d(TAG, "chooseImage");
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "chooseImageActivity");
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                profilePic.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    // -------------------------------------------------  ------------------------------------------------- //




    // ------------------------------------------------- FIREBASE STUFF ------------------------------------------------- //


    /**
     * Setup the firebase auth object
     */
    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");

        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReference();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());

                    userID = user.getUid().toString();

                    finish();

                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
    }



    /**
     * Register a new email and password to Firebase Authentication
     */
    public void registerNewEmail(final String email, final String password){

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        if (task.isSuccessful()){
                            Log.d(TAG, "onComplete: AuthState: " + FirebaseAuth.getInstance().getCurrentUser().getUid());

                            //db = FirebaseFirestore.getInstance();

                            uploadImage(FirebaseAuth.getInstance().getCurrentUser().getUid());

                            //insert user data
                            user = new User();
                            user.setEmail(email);
                            //user.setUser_id(FirebaseAuth.getInstance().getUid());
                            user.setAccount(accountType);
                            user.setAddress(address);
                            user.setPhone(phone);
                            user.setfName(fName);
                            user.setlName(lName);
                            user.setFranchise(franchise);
                            user.setPassword(password);
                            user.setNumJobs(0);
                            user.setRating(null);
                            user.setProfile_image(defaultProfileImageUrl);


                            FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                                    .setTimestampsInSnapshotsEnabled(true)
                                    .build();
                            db.setFirestoreSettings(settings);

                            DocumentReference newUserRef = db.collection("user").document(FirebaseAuth.getInstance().getCurrentUser().getUid());


//                            FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
//                                    .setTimestampsInSnapshotsEnabled(true)
//                                    .build();
//                            mDb.setFirestoreSettings(settings);
//
//                            DocumentReference newUserRef = mDb
//                                    .collection(getString(R.string.collection_users))
//                                    .document(FirebaseAuth.getInstance().getUid());
//
                            newUserRef.set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()){
                                        if(user.getAccount().equals("Employer")) {
                                            // Toast.makeText(mContext, "employer account.", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(RegisterActivity.this, PlanActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            // Toast.makeText(mContext, "spotter account.", Toast.LENGTH_SHORT).show();
                                            //Toast.makeText(mContext, "Welcome to SpotGrab.", Toast.LENGTH_SHORT).show();
                                            Toast toast = Toast.makeText(mContext, "Welcome to SpotGrab", Toast.LENGTH_SHORT);
                                            View view = toast.getView();
                                            view.setBackgroundColor(Color.parseColor("#36454f"));
                                            TextView toastMessage = (TextView) toast.getView().findViewById(android.R.id.message);
                                            toastMessage.setTextColor(Color.parseColor("#0BDAD0"));
                                            toast.show();
                                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }else{
                                        //View parentLayout = findViewById(android.R.id.content);
                                        Toast.makeText(mContext, "Something went wrong.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }
                        else {
                            //View parentLayout = findViewById(android.R.id.content);
                            Toast.makeText(mContext, "Unable to create account.", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
        //mAuth.signInWithEmailAndPassword(mEmail.getText().toString(), mPassword.getText().toString());
    }



//    @Override
//    public void onStart() {
//        super.onStart();
//        mAuth.addAuthStateListener(mAuthListener);
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        if (mAuthListener != null) {
//            mAuth.removeAuthStateListener(mAuthListener);
//        }
//    }


    // -------------------------------------------------  ------------------------------------------------- //



}
