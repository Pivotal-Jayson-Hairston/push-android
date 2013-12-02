package com.gopivotal.pushlib;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;

public class SettingsActivity extends PreferenceActivity {

    private EditTextPreference gcmSenderIdPreference;
    private EditTextPreference gcmBrowserApiPreference;
    private EditTextPreference releaseUuidPreference;
    private EditTextPreference releaseSecretPreference;
    private EditTextPreference deviceAliasPreference;
    private SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
        // NOTE - many of the method calls in this class show up as deprecated.  However, I still want my
        // app to run on old Android versions, so I'm going to leave them in here.
        addPreferencesFromResource(R.xml.preferences);
        gcmSenderIdPreference = (EditTextPreference) getPreferenceScreen().findPreference(Settings.GCM_SENDER_ID);
        gcmBrowserApiPreference = (EditTextPreference) getPreferenceScreen().findPreference(Settings.GCM_BROWSER_API_KEY);
        releaseUuidPreference = (EditTextPreference) getPreferenceScreen().findPreference(Settings.RELEASE_UUID);
        releaseSecretPreference = (EditTextPreference) getPreferenceScreen().findPreference(Settings.RELEASE_SECRET);
        deviceAliasPreference = (EditTextPreference) getPreferenceScreen().findPreference(Settings.DEVICE_ALIAS);
        preferenceChangeListener = getPreferenceChangeListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        showCurrentPreferences();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(preferenceChangeListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(preferenceChangeListener);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Show the Up button in the action bar.
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private SharedPreferences.OnSharedPreferenceChangeListener getPreferenceChangeListener() {
        return new SharedPreferences.OnSharedPreferenceChangeListener() {

            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                showCurrentPreferences();
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        } else if (id == R.id.action_reset_preferences) {
            resetPreferencesToDefault();
        }
        return super.onOptionsItemSelected(item);
    }

    private void resetPreferencesToDefault() {
        final SharedPreferences prefs = getPreferenceScreen().getSharedPreferences();
        prefs.edit().clear().commit();
        PreferenceManager.setDefaultValues(this, R.xml.preferences, true);
        showCurrentPreferences();
    }

    private void showCurrentPreferences() {
        final SharedPreferences prefs = getPreferenceScreen().getSharedPreferences();
        gcmSenderIdPreference.setSummary(prefs.getString(Settings.GCM_SENDER_ID, null));
        gcmBrowserApiPreference.setSummary(prefs.getString(Settings.GCM_BROWSER_API_KEY, null));
        releaseUuidPreference.setSummary(prefs.getString(Settings.RELEASE_UUID, null));
        releaseSecretPreference.setSummary(prefs.getString(Settings.RELEASE_SECRET, null));
        deviceAliasPreference.setSummary(prefs.getString(Settings.DEVICE_ALIAS, null));
    }
}
