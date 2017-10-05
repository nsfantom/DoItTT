package tm.fantom.doittesttask.util;

import android.support.media.ExifInterface;

/**
 * Created by fantom on 03-Oct-17.
 */

public final class ExifParser {
    private boolean valid = false;
    private Float latitude;
    private Float longitude;

    public ExifParser(ExifInterface exif) {
        String attrLATITUDE = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
        String attrLATITUDE_REF = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
        String attrLONGITUDE = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
        String attrLONGITUDE_REF = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);

        if ((attrLATITUDE != null)
                && (attrLATITUDE_REF != null)
                && (attrLONGITUDE != null)
                && (attrLONGITUDE_REF != null)) {
            valid = true;

            if (attrLATITUDE_REF.equals("N")) {
                latitude = convertToDegree(attrLATITUDE);
            } else {
                latitude = 0 - convertToDegree(attrLATITUDE);
            }

            if (attrLONGITUDE_REF.equals("E")) {
                longitude = convertToDegree(attrLONGITUDE);
            } else {
                longitude = 0 - convertToDegree(attrLONGITUDE);
            }

        }
    }


    private Float convertToDegree(String stringDMS) {
        Float result = null;
        String[] DMS = stringDMS.split(",", 3);

        String[] stringD = DMS[0].split("/", 2);
        Double D0 = Double.valueOf(stringD[0]);
        Double D1 = Double.valueOf(stringD[1]);
        Double FloatD = D0 / D1;

        String[] stringM = DMS[1].split("/", 2);
        Double M0 = Double.valueOf(stringM[0]);
        Double M1 = Double.valueOf(stringM[1]);
        Double FloatM = M0 / M1;

        String[] stringS = DMS[2].split("/", 2);
        Double S0 = Double.valueOf(stringS[0]);
        Double S1 = Double.valueOf(stringS[1]);
        Double FloatS = S0 / S1;

        result = new Float(FloatD + (FloatM / 60) + (FloatS / 3600));

        return result;


    }


    public boolean isValid() {
        return valid;
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return (String.valueOf(latitude)
                + ", "
                + String.valueOf(longitude));
    }

    public Float getLatitude() {
        return latitude;
    }

    public Float getLongitude() {
        return longitude;
    }
}
