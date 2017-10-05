package tm.fantom.doittesttask.util;

import android.content.Context;
import android.graphics.Bitmap;

import java.io.File;

/**
 * Created by fantom on 01-Oct-17.
 */

public final class ImageOps {

        public static Bitmap scaleDown(Bitmap realImage, float maxImageSize, boolean filter) {
            float ratio = Math.min(
                    (float) maxImageSize / realImage.getWidth(),
                    (float) maxImageSize / realImage.getHeight()
            );
            int width = Math.round((float) ratio * realImage.getWidth());
            int height = Math.round((float) ratio * realImage.getHeight());

            return Bitmap.createScaledBitmap(realImage, width, height, filter);
        }

        public static File createFile(File imageFolder, String childId) {
            if (!imageFolder.exists()) imageFolder.mkdirs();
            return new File(imageFolder, childId);
        }


        public static File getImageDir(Context context){
           return new File(context.getFilesDir(), "images");
        }

}
