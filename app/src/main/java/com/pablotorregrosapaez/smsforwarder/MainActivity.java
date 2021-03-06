package com.pablotorregrosapaez.smsforwarder;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.pablotorregrosapaez.smsforwarder.adapter.ForwardedItemRecyclerViewAdapter;
import com.pablotorregrosapaez.smsforwarder.config.AppDatabase;
import com.pablotorregrosapaez.smsforwarder.factory.AppDatabaseFactory;
import com.pablotorregrosapaez.smsforwarder.fragment.ForwardedItemFragment;
import com.pablotorregrosapaez.smsforwarder.model.Message;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends Activity implements LifecycleOwner, ForwardedItemFragment.OnListFragmentInteractionListener {

    private static final String[] PERMISSIONS_NEEDED = {
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.READ_SMS,
            Manifest.permission.SEND_SMS,
            Manifest.permission.READ_PHONE_STATE
    };
    private static final int PERMISSIONS_REQUEST_ID = 10;
    private final LifecycleRegistry lifecycleRegistry = new LifecycleRegistry(this);
    private BatteryLevelReceiver batteryLevelReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        askForPermissions();
        setContentView(R.layout.activity_main);

        // Register the low battery receiver
        batteryLevelReceiver = new BatteryLevelReceiver();
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_LOW);
        registerReceiver(batteryLevelReceiver, intentFilter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings_button:
                Intent intent = new Intent(this, SettingsActivity.class);
                this.startActivity(intent);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        displayDbContent();
    }

    private void displayDbContent() {
        RecyclerView fragmentList = findViewById(R.id.forwarded_items_fragment);
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
        if (!hasPermissions(PERMISSIONS_NEEDED)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS_NEEDED, PERMISSIONS_REQUEST_ID);
        }
    }

    private boolean hasPermissions(String[] permissionsNeeded) {
        AtomicReference<Boolean> permissionsGranted = new AtomicReference<>(true);
        Arrays.asList(permissionsNeeded).forEach(permission -> {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsGranted.set(false);
            }
        });
        return permissionsGranted.get();
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return lifecycleRegistry;
    }

    @Override
    public void onListFragmentInteraction(Message item) {
        Intent intent = new Intent(this, MessageDetails.class);
        intent.putExtra("messageId", item.getId());
        startActivity(intent);
        System.out.println("item pressed");
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (batteryLevelReceiver != null) {
            unregisterReceiver(batteryLevelReceiver);
        }
    }
}
