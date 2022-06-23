package com.dev175.liveearthmap.services;

import androidx.annotation.NonNull;

import com.google.firebase.iid.FirebaseInstanceId;

public class FirebaseIDService extends FirebaseMessagingService {


    @Override
    public void onNewToken(@NonNull String s) {

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        sendRegistrationToServer(refreshedToken);

        super.onNewToken(s);
    }

    private void sendRegistrationToServer(String token) {

        // Add custom implementation, as needed.
    }


}
