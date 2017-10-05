package tm.fantom.doittesttask.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by fantom on 01-Oct-17.
 */

public final class Gif {
    @SerializedName("gif")
    @Expose
    private String url;

    public Gif() {

    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
