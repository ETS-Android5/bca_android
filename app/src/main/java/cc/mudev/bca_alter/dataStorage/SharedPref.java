package cc.mudev.bca_alter.dataStorage;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPref {
    public enum SharedPrefKeys {
        // Account related things
        ID("ID"),
        PASSWORD("PASSWORD"),
        EMAIL("EMAIL"),
        NICKNAME("NICKNAME"),
        REFRESH_TOKEN("REFRESH_TOKEN");

        private final String getSharedPrefKeyName;

        SharedPrefKeys(String sharedPrefKeyName) {
            getSharedPrefKeyName = sharedPrefKeyName;
        }

        @Override
        public String toString() {
            return getSharedPrefKeyName;
        }
    }

    public static SharedPref minstance;
    private final SharedPreferences pref;
    private final SharedPreferences.Editor editor;

    private SharedPref(Context context) {
        this.pref = context.getSharedPreferences("pref", Context.MODE_PRIVATE);
        editor = this.pref.edit();
    }

    public static SharedPref getInstance(Context context) {
        if (minstance == null) {
            minstance = new SharedPref(context);
        }
        return minstance;
    }

    public int getInt(SharedPrefKeys k) { return this.pref.getInt(k.getSharedPrefKeyName, -1); }
    public Boolean getBoolean(SharedPrefKeys k) { return this.pref.getBoolean(k.getSharedPrefKeyName, false); }
    public String getString(SharedPrefKeys k) { return this.pref.getString(k.getSharedPrefKeyName, null); }

    public void setPref(SharedPrefKeys k, int v) {
        this.editor.putInt(k.getSharedPrefKeyName, v);
        this.editor.apply();
    }
    public void setPref(SharedPrefKeys k, String v) {
        this.editor.putString(k.getSharedPrefKeyName, v);
        this.editor.apply();
    }
    public void setPref(SharedPrefKeys k, Boolean v) {
        this.editor.putBoolean(k.getSharedPrefKeyName, v);
        this.editor.apply();
    }

    public void removePref(SharedPrefKeys k) {
        this.editor.remove(k.getSharedPrefKeyName);
        this.editor.apply();
    }
    public void removeAllPref() {
        this.editor.clear();
        this.editor.apply();
    }
}
