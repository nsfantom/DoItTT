package tm.fantom.doittesttask.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by fantom on 01-Oct-17.
 */

public final class SignIn {

    @Expose
    @SerializedName("token")
    private String token;
    @Expose
    @SerializedName("avatar")
    private String avatarLink;
    @Expose
    @SerializedName("creation_time")
    private String creationTime;

    public SignIn() {

    }

    public SignIn(String creationTime, String token, String avatarLink) {
        this.creationTime = creationTime;
        this.token = token;
        this.avatarLink = avatarLink;
    }

    public String getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAvatarLink() {
        return avatarLink;
    }

    public void setAvatarLink(String avatarLink) {
        this.avatarLink = avatarLink;
    }

    @Override
    public String toString() {
        return "SignInResponse{" +
                "creationTime='" + creationTime + '\'' +
                ", token='" + token + '\'' +
                ", avatarLink='" + avatarLink + '\'' +
                '}';
    }
}
