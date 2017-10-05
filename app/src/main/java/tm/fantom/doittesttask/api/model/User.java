package tm.fantom.doittesttask.api.model;

import com.google.gson.annotations.Expose;

import java.io.File;

/**
 * Created by fantom on 01-Oct-17.
 */

public final class User {
    @Expose
    private String username;
    @Expose
    private String email;
    @Expose
    private String password;
    @Expose
    private File avatar;

    public User(){

    }

    public User(String userName, String email, String password, File avatar) {
        this.username = userName;
        this.email = email;
        this.password = password;
        this.avatar = avatar;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public File getAvatar() {
        return avatar;
    }

    public void setAvatar(File avatar) {
        this.avatar = avatar;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", avatar=" + avatar +
                '}';
    }
}
