package com.pablotorregrosapaez.smsforwarder;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.pablotorregrosapaez.smsforwarder.adapter.ForwardedItemRecyclerViewAdapter;
import com.pablotorregrosapaez.smsforwarder.config.AppDatabase;
import com.pablotorregrosapaez.smsforwarder.factory.AppDatabaseFactory;
import com.pablotorregrosapaez.smsforwarder.fragment.ForwardedItemFragment;
import com.pablotorregrosapaez.smsforwarder.model.Message;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends Activity implements LifecycleOwner, ForwardedItemFragment.OnListFragmentInteractionListener {

    private static final int MY_PERMISSIONS_REQUEST_RECEIVE_SMS = 10;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 20;
    private static final int MY_PERMISSIONS_REQUEST_READ_SMS = 30;
    private static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 40;
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
        RecyclerView fragmentList = findViewById(R.id.forwared_items_fragment);
        AppDatabase db = AppDatabaseFactory.build(this, AppDatabaseFactory.MESSAGES_DB_NAME);
        db.messageDao().getAll().observe(this, msg -> {
            if (msg != null) {
                msg.forEach(m -> {
                    if (fragmentList != null) {
                        ForwardedItemRecyclerViewAdapter adapter = (ForwardedItemRecyclerViewAdapter) fragmentList.getAdapter();
                        adapter.setData(msg);
                    }
                });
            }
        });
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

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
        }
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return lifecycleRegistry;
    }

    @Override
    public void onListFragmentInteraction(Message item) {
        System.out.println("item pressed");
    }
}
