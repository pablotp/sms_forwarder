package com.pablotorregrosapaez.smsforwarder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;

import java.util.List;

public class SettingsActivity extends PreferenceActivity {
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = (preference, value) -> {
        String stringValue = value.toString();
        preference.setSummary(stringValue);
        return true;
    };

    private static void bindPreferenceSummaryToValue(Preference preference) {
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || GeneralPreferenceFragment.class.getName().equals(fragmentName)
                || AboutPreferenceFragment.class.getName().equals(fragmentName);
    }

    public static class GeneralPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            setHasOptionsMenu(true);

            // Phone number
            Preference preference = findPreference(getString(R.string.pref_key_phone_number));
            bindPreferenceSummaryToValue(preference);

            // Sim list
            SubscriptionManager subscriptionManager = (SubscriptionManager) this.getContext().getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
            @SuppressLint("MissingPermission") List<SubscriptionInfo> subscriptionInfoList = subscriptionManager.getActiveSubscriptionInfoList();
            String[] simNames = subscriptionInfoList.stream()
                    .map(SubscriptionInfo::getDisplayName)
                    .toArray(String[]::new);
            String[] simIds = subscriptionInfoList.stream()
                    .map(SubscriptionInfo::getSubscriptionId)
                    .map(Object::toString)
                    .toArray(String[]::new);
            ListPreference simList = (ListPreference) findPreference(getString(R.string.pref_key_sim_list));
            simList.setEntries(simNames);
            simList.setEntryValues(simIds);
            simList.setDefaultValue("1");
            bindPreferenceSummaryToValue(simList);
        }
    }

    public static class AboutPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_about);
            setHasOptionsMenu(true);

            Preference preference = findPreference(getString(R.string.pref_key_app_version));
            bindPreferenceSummaryToValue(preference);
            preference.setSummary(BuildConfig.VERSION_NAME);
        }
    }
}
