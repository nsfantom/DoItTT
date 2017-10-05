package tm.fantom.doittesttask.api.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by fantom on 01-Oct-17.
 */

public final class Image {
    @SerializedName("id")
    private int id;
    @SerializedName("parameters")
    private ImageParameters imageParametersList;
    @SerializedName("smallImagePath")
    private String smallImagePath;
    @SerializedName("bigImagePath")
    private String bigImagePath;

    public Image() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ImageParameters getImageParametersList() {
        return imageParametersList;
    }

    public void setImageParametersList(ImageParameters imageParametersList) {
        this.imageParametersList = imageParametersList;
    }

    public String getSmallImagePath() {
        return smallImagePath;
    }

    public void setSmallImagePath(String smallImagePath) {
        this.smallImagePath = smallImagePath;
    }

    public String getBigImagePath() {
        return bigImagePath;
    }

    public void setBigImagePath(String bigImagePath) {
        this.bigImagePath = bigImagePath;
    }

    @Override
    public String toString() {
        return "Image{" +
                "id=" + id +
                ", imageParametersList=" + imageParametersList +
                ", smallImagePath='" + smallImagePath + '\'' +
                ", bigImagePath='" + bigImagePath + '\'' +
                '}';
    }
}
