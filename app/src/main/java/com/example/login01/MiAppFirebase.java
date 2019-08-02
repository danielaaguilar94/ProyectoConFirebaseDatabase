package com.example.login01;

import com.google.firebase.database.FirebaseDatabase;
//clase que nos ayuda a la persistencia de datos con Firebase
public class MiAppFirebase extends android.app.Application {

    @Override
    public void onCreate() {
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        super.onCreate();
    }
}
