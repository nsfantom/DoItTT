package tm.fantom.doittesttask.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by fantom on 01-Oct-17.
 */

public class ImageParameters {

    @Expose
    @SerializedName("longitude")
    private float longitude;
    @Expose
    @SerializedName("latitude")
    private float latitude;
    @Expose
    @SerializedName("address")
    private String address;
    @Expose
    @SerializedName("weather")
    private String weather;

    public ImageParameters(){

    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    @Override
    public String toString() {
        return "ImageParameters{" +
                "longitude=" + longitude +
                ", latitude=" + latitude +
                ", address='" + address + '\'' +
                ", weather='" + weather + '\'' +
                '}';
    }
}
