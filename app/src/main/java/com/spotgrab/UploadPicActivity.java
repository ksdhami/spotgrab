package com.spotgrab;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;


public class UploadPicActivity extends AppCompatActivity {

    private static final String TAG = "UploadPicActivity";

    private Button addPicButton, doneButton;
    private ImageView profilePic;

    private Uri filePath;

    private String mProfileImageUrl, userID;
    private Uri resultUri;

    private FirebaseStorage mStorage;
    private StorageReference mStorageRef;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    //private DatabaseReference myRef;
    private DocumentReference myRef;

    private FirebaseAuth.AuthStateListener mAuthListener;

    private Context mContext;

    private final int PICK_IMAGE_REQUEST = 71;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_pic);

        mContext = UploadPicActivity.this;

        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReference();
        mAuth = FirebaseAuth.getInstance();

//        mAuthListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                FirebaseUser user = firebaseAuth.getCurrentUser();
//
//                if (user != null) {
//                    // User is signed in
//                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
//
////                    myRef.addListenerForSingleValueEvent(new ValueEventListener() {
////                        @Override
////                        public void onDataChange(DataSnapshot dataSnapshot) {
////                            checkIfUsernameExists(username);
////                        }
////
////                        @Override
////                        public void onCancelled(DatabaseError databaseError) {
////
////                        }
////                    });
//
//                    finish();
//
//                } else {
//                    // User is signed out
//                    Log.d(TAG, "onAuthStateChanged:signed_out");
//                }
//            }
//        };

        userID = mAuth.getCurrentUser().getUid();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        //myRef = mFirebaseDatabase.getReference().child("user").child(userID);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        myRef = db.collection("user").document(userID);

        addPicButton = findViewById(R.id.addPicButton);
        doneButton = findViewById(R.id.doneButton);
        profilePic = findViewById(R.id.profileImage);

        //getUserInfo();

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
//                DatabaseReference mDataRef = FirebaseDatabase.getInstance().getReference().child("user").child(userID).child("account");
//
//                if(mDataRef.equals("Spotter")) {
//                    Intent intent = new Intent(mContext, HomeEmployerActivity.class);
//                    startActivity(intent);
//                    finish();
//                } else {
//                    Intent intent = new Intent(mContext, HomeSpotterActivity.class);
//                    startActivity(intent);
//                    finish();
//                }
            }
        });


        addPicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });


    }

//    private void getUserInfo(){
//        myRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
//            @Override
//            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
//                if (e != null) {
//                    System.err.println("Listen failed: " + e);
//                    return;
//                }
//
//                if (documentSnapshot != null && documentSnapshot.exists()) {
//                    System.out.println("Current data: " + documentSnapshot.getData());
//                    mProfileImageUrl = documentSnapshot.get("profile_image").toString();
//                    Glide.with(getApplication()).load(mProfileImageUrl).into(profilePic);
//                } else {
//                    System.out.print("Current data: null");
//                }
//            }
//        });
////        myRef.addValueEventListener(new ValueEventListener() {
////            @Override
////            public void onDataChange(DataSnapshot dataSnapshot) {
////                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
////                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
////                    if(map.get("profileImageUrl")!=null){
////                        mProfileImageUrl = map.get("profile_image").toString();
////                        Glide.with(getApplication()).load(mProfileImageUrl).into(profilePic);
////                    }
////                }
////            }
////
////            @Override
////            public void onCancelled(DatabaseError databaseError) {
////            }
////        });
//    }

    private void uploadImage() {

//        if(resultUri != null) {
//
//            final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("images").child(userID);
//            Bitmap bitmap = null;
//            try {
//                bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), resultUri);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
//            byte[] data = baos.toByteArray();
//            UploadTask uploadTask = filePath.putBytes(data);
//
//            uploadTask.addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    finish();
//                    return;
//                }
//            });
//            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                        @Override
//                        public void onSuccess(Uri uri) {
//                            Map newImage = new HashMap();
//                            newImage.put("profile_image", uri.toString());
//                            myRef.set(newImage);
//
//                            finish();
//                            return;
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception exception) {
//                            finish();
//                            return;
//                        }
//                    });
//                }
//            });
//        }else{
//            finish();
//        }



        if(filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading ...");
            progressDialog.show();

            StorageReference ref = mStorageRef.child("images/" + UUID.randomUUID().toString());
            //String profile_url = ref.getDownloadUrl().toString();
            ref.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    Toast.makeText(mContext, "Uploaded", Toast.LENGTH_SHORT).show();
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
                    double progess = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                    progressDialog.setMessage("Uploaded " + (int)progess + "%");
                }
            });

        }

    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

//        Intent intent = new Intent(Intent.ACTION_PICK);
//        intent.setType("image/*");
//        startActivityForResult(intent, 1);

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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


//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(requestCode == 1 && resultCode == Activity.RESULT_OK){
//            final Uri imageUri = data.getData();
//            resultUri = imageUri;
//            profilePic.setImageURI(resultUri);
//        }
//    }
}