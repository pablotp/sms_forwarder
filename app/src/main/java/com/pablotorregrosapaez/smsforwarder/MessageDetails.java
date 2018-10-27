package com.pablotorregrosapaez.smsforwarder;

import android.os.Bundle;

import com.pablotorregrosapaez.smsforwarder.fragment.MessageDetailsFragment;

import androidx.fragment.app.FragmentActivity;

public class MessageDetails extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_details_activity);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, MessageDetailsFragment.newInstance())
                    .commitNow();
        }
    }
}
