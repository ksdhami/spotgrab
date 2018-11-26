package com.spotgrab.client;

import android.app.Application;
import com.spotgrab.models.User;
import com.google.firebase.firestore.FirebaseFirestore;


public class UserClient extends Application {

    private User user = null;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
