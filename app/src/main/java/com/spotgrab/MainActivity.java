package com.spotgrab;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.spotgrab.client.UserClient;
import com.spotgrab.models.User;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    //public static final User user = new User();

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseFirestore mFirestore;
    private DocumentReference userRef;
    private FirebaseUser user;

    private Context mContext;
    private ProgressBar mProgressBar;
    private EditText mEmail, mPassword;

    private String email, password, userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEmail = findViewById(R.id.etEmail);
        mPassword = findViewById(R.id.etPassword);
        mProgressBar = findViewById(R.id.progressBar);
        mContext = MainActivity.this;

        Log.d(TAG, "\nonCreate: started.\n");

        mProgressBar.setVisibility(View.GONE);

        setupFirebaseAuth();
        init();
    }


    private boolean isStringNull(String string){
        Log.d(TAG, "\nisStringNull: checking string if null.\n");

        return string.equals("");
    }


    private void init(){

        //initialize the button for logging in
        Button loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "\nonClick: attempting to log in.\n");

                 email = mEmail.getText().toString();
                 password = mPassword.getText().toString();

                if(isStringNull(email) && isStringNull(password)){
                    Toast.makeText(mContext, "Enter both your email and password", Toast.LENGTH_SHORT).show();
                } else{
                    mProgressBar.setVisibility(View.VISIBLE);

                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "\nsignInWithEmail:onComplete:" + task.isSuccessful() + "\n");

                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful()) {
                                Log.w(TAG, "\nsignInWithEmail:failed\n", task.getException());

                                Toast.makeText(mContext, "Failed to Authenticate", Toast.LENGTH_SHORT).show();
                                mProgressBar.setVisibility(View.GONE);
                            }
                            else{
                                userID = mAuth.getCurrentUser().getUid();
                                mFirestore = FirebaseFirestore.getInstance();
                                FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                                        .setTimestampsInSnapshotsEnabled(true)
                                        .build();
                                mFirestore.setFirestoreSettings(settings);

                                DocumentReference userRef = mFirestore.collection("user").document(userID);

                                userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if(task.isSuccessful()) {
                                            Log.d(TAG, "onComplete: successfully set the user client." + user.getUid().toString());
                                            User userLoggedIn = task.getResult().toObject(User.class);
                                            String userAccountType = userLoggedIn.getAccount();
                                            if (userAccountType.equals("Spotter")) {
                                                Intent intent = new Intent(MainActivity.this, HomeSpotterActivity.class);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                Intent intent = new Intent(MainActivity.this, HomeEmployerActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }
                                    }
                                });


                            }
                        }
                    });
                }
            }
        });

        Button createAccountButton = findViewById(R.id.registerButton);
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "\nonClick: navigating to register screen\n");
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });


         /*
         If the user is logged in then navigate to HomeActivity and call 'finish()'
          */
        if(mAuth.getCurrentUser() != null){

            // ADD IF ELSE FOR TYPE OF USER

            userID = mAuth.getCurrentUser().getUid();
            mFirestore = FirebaseFirestore.getInstance();
            FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                    .setTimestampsInSnapshotsEnabled(true)
                    .build();
            mFirestore.setFirestoreSettings(settings);

            DocumentReference userRef = mFirestore.collection("user").document(userID);

            userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()) {
                        Log.d(TAG, "onComplete: successfully set the user client." + user.getUid().toString());
                        User userLoggedIn = task.getResult().toObject(User.class);
                        String userAccountType = userLoggedIn.getAccount();
                        if (userAccountType.equals("Spotter")) {
                            Intent intent = new Intent(MainActivity.this, HomeSpotterActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Intent intent = new Intent(MainActivity.this, HomeEmployerActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                }
            });

        }
    }

    /**
     * Setup the firebase auth object
     */
    private void setupFirebaseAuth(){
        Log.d(TAG, "\nsetupFirebaseAuth: setting up firebase auth.\n");

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "\nonAuthStateChanged:signed_in:" + user.getUid() + "\n");
                    mFirestore = FirebaseFirestore.getInstance();
                    FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                            .setTimestampsInSnapshotsEnabled(true)
                            .build();
                    mFirestore.setFirestoreSettings(settings);

                    userRef = mFirestore.collection("user").document(user.getUid());

                    userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                Log.d(TAG, "onComplete: successfully set the user client.");
                                User userLoggedIn = task.getResult().toObject(User.class);
                            }
                        }
                    });
                } else {
                    // User is signed out
                    Log.d(TAG, "\nonAuthStateChanged:signed_out\n");
                }
                // ...
            }
        };
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



//import android.content.Intent;
//        import android.os.Bundle;
//        import android.support.annotation.NonNull;
//        import android.support.v7.app.AppCompatActivity;
//        import android.util.Log;
//        import android.view.View;
//        import android.view.WindowManager;
//        import android.widget.Button;
//        import android.widget.EditText;
//        import android.widget.ProgressBar;
//        import android.widget.TextView;
//        import android.widget.Toast;
//
//        import com.codingwithmitch.firestorechatapp.R;
//        import com.codingwithmitch.firestorechatapp.UserClient;
//        import com.codingwithmitch.firestorechatapp.models.User;
//        import com.google.android.gms.tasks.OnCompleteListener;
//        import com.google.android.gms.tasks.OnFailureListener;
//        import com.google.android.gms.tasks.Task;
//        import com.google.firebase.auth.AuthResult;
//        import com.google.firebase.auth.FirebaseAuth;
//        import com.google.firebase.auth.FirebaseUser;
//        import com.google.firebase.firestore.DocumentReference;
//        import com.google.firebase.firestore.DocumentSnapshot;
//        import com.google.firebase.firestore.FirebaseFirestore;
//        import com.google.firebase.firestore.FirebaseFirestoreSettings;
//
//        import static android.text.TextUtils.isEmpty;
//
//public class LoginActivity extends AppCompatActivity implements
//        View.OnClickListener
//{
//
//    private static final String TAG = "LoginActivity";
//
//    //Firebase
//    private FirebaseAuth.AuthStateListener mAuthListener;
//
//    // widgets
//    private EditText mEmail, mPassword;
//    private ProgressBar mProgressBar;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_login);
//        mEmail = findViewById(R.id.email);
//        mPassword = findViewById(R.id.password);
//        mProgressBar = findViewById(R.id.progressBar);
//
//        setupFirebaseAuth();
//        findViewById(R.id.email_sign_in_button).setOnClickListener(this);
//        findViewById(R.id.link_register).setOnClickListener(this);
//
//        hideSoftKeyboard();
//    }
//
//
//
//
//    private void showDialog(){
//        mProgressBar.setVisibility(View.VISIBLE);
//
//    }
//
//    private void hideDialog(){
//        if(mProgressBar.getVisibility() == View.VISIBLE){
//            mProgressBar.setVisibility(View.INVISIBLE);
//        }
//    }
//
//    private void hideSoftKeyboard(){
//        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
//    }
//
//    /*
//        ----------------------------- Firebase setup ---------------------------------
//     */
//    private void setupFirebaseAuth(){
//        Log.d(TAG, "setupFirebaseAuth: started.");
//
//        mAuthListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                FirebaseUser user = firebaseAuth.getCurrentUser();
//                if (user != null) {
//                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
//                    Toast.makeText(LoginActivity.this, "Authenticated with: " + user.getEmail(), Toast.LENGTH_SHORT).show();
//
//                    FirebaseFirestore db = FirebaseFirestore.getInstance();
//                    FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
//                            .setTimestampsInSnapshotsEnabled(true)
//                            .build();
//                    db.setFirestoreSettings(settings);
//
//                    DocumentReference userRef = db.collection(getString(R.string.collection_users))
//                            .document(user.getUid());
//
//                    userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                        @Override
//                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                            if(task.isSuccessful()){
//                                Log.d(TAG, "onComplete: successfully set the user client.");
//                                User user = task.getResult().toObject(User.class);
//                                ((UserClient)(getApplicationContext())).setUser(user);
//                            }
//                        }
//                    });
//
//                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    startActivity(intent);
//                    finish();
//
//                } else {
//                    // User is signed out
//                    Log.d(TAG, "onAuthStateChanged:signed_out");
//                }
//                // ...
//            }
//        };
//    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//        FirebaseAuth.getInstance().addAuthStateListener(mAuthListener);
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        if (mAuthListener != null) {
//            FirebaseAuth.getInstance().removeAuthStateListener(mAuthListener);
//        }
//    }
//
//    private void signIn(){
//        //check if the fields are filled out
//        if(!isEmpty(mEmail.getText().toString())
//                && !isEmpty(mPassword.getText().toString())){
//            Log.d(TAG, "onClick: attempting to authenticate.");
//
//            showDialog();
//
//            FirebaseAuth.getInstance().signInWithEmailAndPassword(mEmail.getText().toString(),
//                    mPassword.getText().toString())
//                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                        @Override
//                        public void onComplete(@NonNull Task<AuthResult> task) {
//
//                            hideDialog();
//
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    Toast.makeText(LoginActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
//                    hideDialog();
//                }
//            });
//        }else{
//            Toast.makeText(LoginActivity.this, "You didn't fill in all the fields.", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    @Override
//    public void onClick(View view) {
//        switch (view.getId()){
//            case R.id.link_register:{
//                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
//                startActivity(intent);
//                break;
//            }
//
//            case R.id.email_sign_in_button:{
//                signIn();
//                break;
//            }
//        }
//    }
//}
