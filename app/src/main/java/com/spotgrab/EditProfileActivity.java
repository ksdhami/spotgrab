package com.spotgrab;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.spotgrab.models.User;
import com.spotgrab.module.GlideApp;

import java.io.IOException;

public class EditProfileActivity extends AppCompatActivity {

    private static final String TAG = "EditProfileActivity";

    private Context mContext;

    private EditText mFName, mLName, mPhone, mAddress, mFranchise;
    private String fName, lName, phone, address, franchise, userID;
    private ImageView profileImage;
    private Button nextButton, addPicButton, cancelButton, menuButton;

    private final int PICK_IMAGE_REQUEST = 71;
    private Uri filePath;

    private String accountType;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mUser;
    private FirebaseStorage mStorage;
    private StorageReference mStorageRef;
    private FirebaseFirestore mFirestore;

    private User loggedInUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mContext = EditProfileActivity.this;

        init();

    }


    private void init() {
        Log.d(TAG, "\ninit: started.\n");

        // Image View
        profileImage = findViewById(R.id.profileImage);

        // Edit Text Fields
        mFName = findViewById(R.id.etFName);
        mLName = findViewById(R.id.etLName);
        mPhone = findViewById(R.id.etPhone);
        mAddress = findViewById(R.id.etAddress);
        mFranchise = findViewById(R.id.etFranchise);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                mUser = firebaseAuth.getCurrentUser();

                if(mUser != null) {
                    Log.d(TAG, "\nonAuthStateChanged:signed_in:" + mUser.getUid() + "\n");

                    mFirestore = FirebaseFirestore.getInstance();
                    FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                            .setTimestampsInSnapshotsEnabled(true)
                            .build();
                    mFirestore.setFirestoreSettings(settings);

                    DocumentReference userRef = mFirestore.collection("user").document(mUser.getUid());

                    userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                Log.d(TAG, "onComplete: successfully set the user client." + mUser.getUid());
                                loggedInUser = task.getResult().toObject(User.class);

                                //accountType = loggedInUser.getAccount();
                                accountType = task.getResult().get("account").toString();
                                Log.d(TAG, "current user account type: "+accountType);

                                // Set hints to current user data
                                mFName.setHint(loggedInUser.getfName());
                                mLName.setHint(loggedInUser.getlName());
                                mPhone.setHint(loggedInUser.getPhone());
                                mAddress.setHint(loggedInUser.getAddress());
                                mFranchise.setHint(loggedInUser.getFranchise());

                            }
                        }
                    });

                    // Get current profile image
//                    mStorageRef = FirebaseStorage.getInstance().getReference().child("images/").child(mUser.getUid());
//                    GlideApp.with(mContext).load(mStorageRef).into(profileImage);
                }

            }
        };


        // Update data with image
        nextButton = findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fName = mFName.getText().toString();
                lName = mLName.getText().toString();
                phone = mPhone.getText().toString();
                address = mAddress.getText().toString();
                franchise = mFranchise.getText().toString();

                checkInput(fName, "fName");
                checkInput(lName, "lName");
                checkInput(phone, "phone");
                checkInput(address, "address");
                checkInput(franchise, "franchise");

                Toast toast = Toast.makeText(mContext, "Your Profile was Updated", Toast.LENGTH_SHORT);
                View view = toast.getView();
                view.setBackgroundColor(Color.parseColor("#36454f"));
                TextView toastMessage = (TextView) toast.getView().findViewById(android.R.id.message);
                toastMessage.setTextColor(Color.parseColor("#0BDAD0"));
                toast.show();

                recreate();

            }
        });

        // Get pic from phone
//        addPicButton = findViewById(R.id.addPicButton);
//        addPicButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                chooseImage();
//            }
//        });


        // Clear everything entered
        cancelButton = findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFName.getText().clear();
                mLName.getText().clear();
                mPhone.getText().clear();
                mAddress.getText().clear();
                mFranchise.getText().clear();

                //GlideApp.with(mContext).load(mStorageRef).into(profileImage);
            }
        });

        menuButton = findViewById(R.id.menuEmpButton);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(accountType.equals("Spotter")) {

                            Intent intent = new Intent(mContext, SpotNavDrawerActivity.class);
                            startActivity(intent);

                } else if (accountType.equals("Employer")){

                            Intent intent = new Intent(mContext, EmpNavDrawerActivity.class);
                            startActivity(intent);
                }
            }
        });
    }


//    private boolean checkInputs(String fName, String lName, String phone, String address, String franchise){
//        Log.d(TAG, "checkInputs: checking inputs for null values.");
//
//        if(fName.equals("") || lName.equals("") || phone.equals("") || address.equals("") || franchise.equals("")){
//            Toast.makeText(mContext, "All fields must be filled out.", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//        return true;
//    }



    private void checkInput(final String input, String field) {
        Log.d(TAG, "checkInput: checking inputs to see if any value changed to update database.");

        mFirestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        mFirestore.setFirestoreSettings(settings);

        DocumentReference userRef = mFirestore.collection("user").document(mUser.getUid());

        if (!(input.equals("")) && !(input.equals(" "))) {
            switch (field) {
                case "fName":
                    userRef.update("fName", input).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "first name updated to " + input);
                        }
                    });
                    break;

                case "lName":
                    userRef.update("lName", input).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "last name updated to " + input);
                        }
                    });
                    break;

                case "phone":
                    userRef.update("phone", input).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "phone updated to " + input);
                        }
                    });
                    break;

                case "address":
                    userRef.update("address", input).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "address updated to " + input);
                        }
                    });
                    break;

                case "franchise":
                    userRef.update("franchise", input).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "franchise updated to " + input);
                        }
                    });
                    break;

                default:
                    break;

            }
        }

    }


    // ------------------------------------------------- UPLOAD PROFILE PIC ------------------------------------------------- //


    //private void deleteImage()

    private void uploadImage(String uid) {
        Log.d(TAG, "uploadedImage");
        if(filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading ...");
            progressDialog.show();

            final StorageReference imageRef = mStorageRef.child("images/" + uid);

            imageRef.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    Toast.makeText(mContext, "Uploaded", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "image uploaded");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(mContext, "Failure to Upload", Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                    progressDialog.setMessage("Uploaded " + (int)progress + "%");
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
                profileImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }





    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
