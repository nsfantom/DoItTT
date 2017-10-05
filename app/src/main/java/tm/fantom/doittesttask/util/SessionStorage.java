package tm.fantom.doittesttask.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by fantom on 01/8/17.
 */
public class SessionStorage {

    private static final String KEY_LAST_LOGIN = "last_login";
    private static final String KEY_LAST_PASSWORD = "last_password";
    private static final String KEY_LAST_TOKEN = "last_token";
    private static final String KEY_REFRESH_TOKEN = "refresh_token";
    private static final String KEY_EXPIRED = "expires_in";
    private static final String KEY_IS_LOGGEDIN = "isLoggedIn";
    public static final String KEY_AVATAR_FILE_NAME = "avatar.jpg";
    private static final String KEY_AVATAR_LINK = "avatar";

    private final SharedPreferences preferences;

    private SessionStorage(SharedPreferences preferences) {
        this.preferences = preferences;
    }

    public static SessionStorage get(Context context) {
        return new SessionStorage(context.getSharedPreferences(context.getPackageName() + ".prefs", Context.MODE_PRIVATE));
    }

    private void putOneValue(String key, String value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    private void putOneValue(String key, int value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public void setAuthInfo(String login, String password) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_LAST_LOGIN, login);
        editor.putString(KEY_LAST_PASSWORD, password);
        // commit changes
        editor.apply();
    }

    public String getLastLogin() {
        return preferences.getString(KEY_LAST_LOGIN, "");
    }

    public String getLastPassword() {
        return preferences.getString(KEY_LAST_PASSWORD, "");
    }

    public String getAccessToken() {
        if (System.currentTimeMillis() >= preferences.getLong(KEY_EXPIRED, 0))
            return "";
        return preferences.getString(KEY_LAST_TOKEN, "");
    }

    public String getRefreshToken() {
        return preferences.getString(KEY_REFRESH_TOKEN, "");
    }

    public void logout() {
        setAccessToken("", 0, "");
    }

    public void setAccessToken(String accessToken, int expires, String refreshToken) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_LAST_TOKEN, accessToken);
        editor.putLong(KEY_EXPIRED, System.currentTimeMillis() + expires);
        editor.putString(KEY_REFRESH_TOKEN, refreshToken);
        // commit changes
        editor.apply();
    }

    public void setToken(String accessToken) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_LAST_TOKEN, accessToken);
        // commit changes
        editor.apply();
    }

    public String getToken(){
        return preferences.getString(KEY_LAST_TOKEN, "");
    }

    public void setAvatar(String avatarLink) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_AVATAR_LINK, avatarLink);
        // commit changes
        editor.apply();
    }

    public boolean isLoggedIn(){
        return !preferences.getString(KEY_LAST_TOKEN,"").equals("");
    }
}