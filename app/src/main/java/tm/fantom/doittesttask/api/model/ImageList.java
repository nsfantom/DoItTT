package tm.fantom.doittesttask.api.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by fantom on 01-Oct-17.
 */

public final class ImageList {
    @SerializedName("images")
    private List<Image> imageList;

    public ImageList() {
    }

    public List<Image> getImageList() {
        return imageList;
    }

    public void setImageList(List<Image> imageList) {
        this.imageList = imageList;
    }
}
