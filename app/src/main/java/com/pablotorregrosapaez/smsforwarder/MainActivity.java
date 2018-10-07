package com.pablotorregrosapaez.smsforwarder;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.pablotorregrosapaez.smsforwarder.config.AppDatabase;
import com.pablotorregrosapaez.smsforwarder.model.Message;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.room.Room;

public class MainActivity extends Activity implements LifecycleOwner {

    private static final int MY_PERMISSIONS_REQUEST_RECEIVE_SMS = 10;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 20;
    private static final int MY_PERMISSIONS_REQUEST_READ_SMS = 30;
    private final LifecycleRegistry lifecycleRegistry = new LifecycleRegistry(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        askForPermissions();
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();

        displayDbContent();
    }

    private void displayDbContent() {
        final TextView textField = findViewById(R.id.main_text_field);
        if (textField != null) {
            AppDatabase db = Room.databaseBuilder(this, AppDatabase.class, "db-messages").build();

            db.messageDao().getAll().observe(this, new Observer<List<Message>>() {
                @Override
                public void onChanged(@Nullable List<Message> msg){
                    if (msg != null) {
                        msg.forEach(m -> {
                            String currentText = textField.getText().toString();
                            currentText += m.getContent() + "\n";
                            textField.setText(currentText);
                        });

                    }
                }
            });
        }
    }

    private void askForPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS}, MY_PERMISSIONS_REQUEST_RECEIVE_SMS);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, MY_PERMISSIONS_REQUEST_SEND_SMS);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS}, MY_PERMISSIONS_REQUEST_READ_SMS);
        }
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return lifecycleRegistry;
    }
}
