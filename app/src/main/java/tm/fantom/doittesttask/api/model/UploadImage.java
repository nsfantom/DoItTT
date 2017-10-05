package tm.fantom.doittesttask.api.model;

import java.io.File;

/**
 * Created by fantom on 03-Oct-17.
 */

public final class UploadImage {
    private float latitude;
    private float longitude;
    private String description;
    private String hashTag;
    private File imageFile;

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHashTag() {
        return hashTag;
    }

    public void setHashTag(String hashTag) {
        this.hashTag = hashTag;
    }

    public File getImageFile() {
        return imageFile;
    }

    public void setImageFile(File imageFile) {
        this.imageFile = imageFile;
    }

    @Override
    public String toString() {
        return "UploadImage{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", description='" + description + '\'' +
                ", hashTag='" + hashTag + '\'' +
                ", imageFile=" + imageFile +
                '}';
    }
}
