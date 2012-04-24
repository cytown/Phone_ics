/*
 * Copyright (C) 2012 Cytown(cytown@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.phone;

import android.app.ActionBar;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.SystemProperties;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

/**
 * Helper class to manage the advenced settings feature to Phone app,
 */
public class AdvancedSettings {
    private static final String TAG = "AdvancedSettings";
    private static final boolean DBG =
            (PhoneApp.DBG_LEVEL >= 1) && (SystemProperties.getInt("ro.debuggable", 0) == 1);
    // Do not check in with VDBG = true, since that may write PII to the system log.
    private static final boolean VDBG = false;

    /** SharedPreferences file name for our persistent settings. */
    private static final String SHARED_PREFERENCES_NAME = "advenced_settings_prefs";

    /** The prefence key used in xml and Java */
    private static final String BUTTON_VIBRATE_ANSWER   = "button_vibrate_on_answer";
    private static final String BUTTON_VIBRATE_45       = "button_vibrate_on_45";
    private static final String BUTTON_VIBRATE_HANGUP   = "button_vibrate_on_hangup";
    private static final String BUTTON_VIBRATE_CALL_WAITING = "button_vibrate_on_call_waiting";
    private static final String BUTTON_SHOW_ORGAN       = "button_show_organ";
    private static final String BUTTON_SILENCE_TURNOVER = "button_silence_on_turnover";
    private static final String BUTTON_ENABLE_BLACK     = "button_enable_blacklist";
    private static final String BUTTON_ADD_BLACK        = "button_add_black";

    public static boolean isVibrateOnAnswer;
    public static boolean isVibrateOn45;
    public static boolean isVibrateOnHangup;
    public static boolean isVibrateOnCallWaiting;
    public static boolean isShowOrgan;
    public static boolean isSilenceTurnover;
    public static boolean enableBlacklist;

    private static SharedPreferences sPref = null;
    private static boolean sIsInit = false;

    /**
     * AdvancedSettings constructor.
     */
    private AdvancedSettings() {
    }

    public static void init() {
        initSettings(false);
    }

    public static void initSettings(boolean force) {
        if (sPref == null) {
            PhoneApp app = PhoneApp.getInstance();
            sPref = app.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        }
        if (force || !sIsInit) {
            isVibrateOnAnswer = sPref.getBoolean(BUTTON_VIBRATE_ANSWER, true);
            isVibrateOn45 = sPref.getBoolean(BUTTON_VIBRATE_45, false);
            isVibrateOnHangup = sPref.getBoolean(BUTTON_VIBRATE_HANGUP, true);
            isVibrateOnCallWaiting = sPref.getBoolean(BUTTON_VIBRATE_CALL_WAITING, true);
            isShowOrgan = sPref.getBoolean(BUTTON_SHOW_ORGAN, false);
            isSilenceTurnover = sPref.getBoolean(BUTTON_SILENCE_TURNOVER, false);
            enableBlacklist = sPref.getBoolean(BUTTON_ENABLE_BLACK, false);
            sIsInit = true;
        }
    }

    public static void save() {
        init();
        Editor outState = sPref.edit();
        outState.putBoolean(BUTTON_VIBRATE_ANSWER, isVibrateOnAnswer);
        outState.putBoolean(BUTTON_VIBRATE_45, isVibrateOn45);
        outState.putBoolean(BUTTON_VIBRATE_HANGUP, isVibrateOnHangup);
        outState.putBoolean(BUTTON_VIBRATE_CALL_WAITING, isVibrateOnCallWaiting);
        outState.putBoolean(BUTTON_SHOW_ORGAN, isShowOrgan);
        outState.putBoolean(BUTTON_SILENCE_TURNOVER, isSilenceTurnover);
        outState.putBoolean(BUTTON_ENABLE_BLACK, enableBlacklist);
        outState.apply();
    }

    /**
     * Settings activity under "Call settings" to let you manage the
     * canned responses; see respond_via_sms_settings.xml
     */
    public static class Settings extends PreferenceActivity
            implements Preference.OnPreferenceChangeListener {
        private CheckBoxPreference mPrefVibrateOnAnswer;
        private CheckBoxPreference mPrefVibrateOn45;
        private CheckBoxPreference mPrefVibrateOnHangup;
        private CheckBoxPreference mPrefVibrateCallWaiting;
        private CheckBoxPreference mPrefShowOrgan;
        private CheckBoxPreference mPrefSilenceTurnover;
        private CheckBoxPreference mPrefEnableBlack;
        private Preference mPrefAddBlack;

        @Override
        protected void onCreate(Bundle icicle) {
            super.onCreate(icicle);
            if (DBG) log("Settings: onCreate()...");

            addPreferencesFromResource(R.xml.advanced_setting);

            init();

            mPrefVibrateOnAnswer = (CheckBoxPreference) findPreference(BUTTON_VIBRATE_ANSWER);
            mPrefVibrateOnAnswer.setChecked(isVibrateOnAnswer);
            mPrefVibrateOn45 = (CheckBoxPreference) findPreference(BUTTON_VIBRATE_45);
            mPrefVibrateOn45.setChecked(isVibrateOn45);
            mPrefVibrateOnHangup = (CheckBoxPreference) findPreference(BUTTON_VIBRATE_HANGUP);
            mPrefVibrateOnHangup.setChecked(isVibrateOnHangup);
            mPrefVibrateCallWaiting = (CheckBoxPreference) findPreference(BUTTON_VIBRATE_CALL_WAITING);
            mPrefVibrateCallWaiting.setChecked(isVibrateOnCallWaiting);
            mPrefShowOrgan = (CheckBoxPreference) findPreference(BUTTON_SHOW_ORGAN);
            mPrefShowOrgan.setChecked(isShowOrgan);
            mPrefSilenceTurnover = (CheckBoxPreference) findPreference(BUTTON_SILENCE_TURNOVER);
            mPrefSilenceTurnover.setChecked(isSilenceTurnover);
            mPrefEnableBlack = (CheckBoxPreference) findPreference(BUTTON_ENABLE_BLACK);
            mPrefEnableBlack.setChecked(enableBlacklist);

            ActionBar actionBar = getActionBar();
            if (actionBar != null) {
                // android.R.id.home will be triggered in onOptionsItemSelected()
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }

        // Preference.OnPreferenceChangeListener implementation
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            if (DBG) log("onPreferenceChange: key = " + preference.getKey());
            if (VDBG) log("  preference = '" + preference + "'");
            if (VDBG) log("  newValue = '" + newValue + "'");

            // Copy the new text over to the title, just like in onCreate().
            // (Watch out: onPreferenceChange() is called *before* the
            // Preference itself gets updated, so we need to use newValue here
            // rather than pref.getText().)
            preference.setTitle((String) newValue);

            return true;  // means it's OK to update the state of the Preference with the new value
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            final int itemId = item.getItemId();
            if (itemId == android.R.id.home) {  // See ActionBar#setDisplayHomeAsUpEnabled()
                CallFeaturesSetting.goUpToTopLevelSetting(this);
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

        @Override
        public void onStop() {
            super.onStop();
            isVibrateOnAnswer = mPrefVibrateOnAnswer.isChecked();
            isVibrateOn45 = mPrefVibrateOn45.isChecked();
            isVibrateOnHangup = mPrefVibrateOnHangup.isChecked();
            isVibrateOnCallWaiting = mPrefVibrateCallWaiting.isChecked();
            isShowOrgan = mPrefShowOrgan.isChecked();
            isSilenceTurnover = mPrefSilenceTurnover.isChecked();
            enableBlacklist = mPrefEnableBlack.isChecked();
            save();
        }
    }

    private static void log(String msg) {
        Log.d(TAG, msg);
    }
}
