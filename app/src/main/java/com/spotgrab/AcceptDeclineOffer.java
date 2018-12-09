package com.spotgrab;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

public class AcceptDeclineOffer extends AppCompatDialogFragment {

    private static final String TAG = "AcceptDeclineOffer";

    private AcceptDeclineOfferListener listener;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mUser;
    private FirebaseFirestore mFirestore;


    @Override
    public Dialog onCreateDialog (Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Looks Like You Got A Job Offer").setMessage("Do you want to accept/decline the offer").setNegativeButton("Decline", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).setPositiveButton("Accept", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        return builder.create();
    }

    private String shiftInformation () {
        String shift;

        // Get shift information from firebase
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

                    DocumentReference userRef = mFirestore.collection("user").document(mUser.getUid()).collection("shift").document();

                    userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()) {
                                Log.d(TAG, "onComplete: successfully set the user client." + mUser.getUid());


                            }
                        }
                    });
                }
            }
        };

        return "";
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

    public interface AcceptDeclineOfferListener {
        // Set fields in schedule document for shift information, ShiftAccepted, EmployerUid for spotter
        // Set SpotterUid and Filled fields in shift for employer
        // Shift will move to active
        void onAcceptClicked();

        // Reset OfferSent for employer
        void onDeclineClicked();
    }

    @Override
    public void onAttach (Context context) {
        super.onAttach(context);

        try {
            listener = (AcceptDeclineOfferListener) context;

        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()+" must implement AcceptDeclineOfferListener");

        }

    }
}
