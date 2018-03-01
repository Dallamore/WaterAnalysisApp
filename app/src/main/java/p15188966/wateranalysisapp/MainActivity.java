//Android 7.0 API 24

package p15188966.wateranalysisapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.media.ExifInterface;
import android.support.v4.content.FileProvider;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;

public class MainActivity extends Activity {
    private ImageView mImageView;
    private String mCurrentPhotoPath;
    private Bitmap mPhoto;

    private File createImageFile() throws IOException {
        // Create an image file name
        String imageFileName = "JPEG_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName,".jpg",storageDir);
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private Bitmap rotateImage(Bitmap bitmap){
        ExifInterface exifInterface;
        int orientation = 0;
        try{
            exifInterface = new ExifInterface(mCurrentPhotoPath);
            orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
        } catch (IOException e){
            e.printStackTrace();
        }
        Matrix matrix = new Matrix();
        switch (orientation){
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            default:
                break;
        }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    private void setPic() {
		/* There isn't enough memory to open up more than a couple camera photos */
		/* So pre-scale the target bitmap into which the file is decoded */

		/* Get the size of the ImageView */
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

		/* Get the size of the image */
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

		/* Figure out which way needs to be reduced less */
        int scaleFactor = 1;
        if ((targetW > 0) || (targetH > 0)) {
            scaleFactor = Math.min(photoW/targetW, photoH/targetH);
        }

		/* Set bitmap options to scale the image decode target */
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

		/* Decode the JPEG file into a Bitmap */
        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

		/* Associate the Bitmap to the ImageView */
		mPhoto = rotateImage(bitmap);
        mImageView.setImageBitmap(mPhoto);
        mImageView.setVisibility(View.VISIBLE);
        mImageView.setOnTouchListener(mainViewTouchListener);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureIntent.resolveActivity(getPackageManager()) != null) {
            //Create file where the photo should go
            File photoFile;
            try {
                photoFile = createImageFile();
            }catch (IOException ex){
                //Error occurred
                ex.printStackTrace();
                photoFile = null;
                mCurrentPhotoPath = null;
            }
            //Continue only if the file was successfully created
            if (photoFile != null) {
                Uri photoUri = FileProvider.getUriForFile(this, "com.example.android.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictureIntent, 1);
            }
        }
    }

    Button.OnClickListener captureBtnOnclickListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            dispatchTakePictureIntent();
        }
    };

    ImageView.OnTouchListener mainViewTouchListener = new ImageView.OnTouchListener() {
//        @Override
//        public boolean onTouch(View v, MotionEvent event){
//
////            float HeightRatio = (float)mPhoto.getHeight() / (float)mImageView.getHeight();
////            float WidthRatio = (float)mPhoto.getWidth() / (float)mImageView.getWidth();
//            int x = (int)event.getX();
//            int y = (int)event.getY();
//
////            Matrix inverse = new Matrix();
////            mImageView.getImageMatrix().invert(inverse);
////            float[] touchPoint = new float[] {event.getX(), event.getY()};
////            inverse.mapPoints(touchPoint);
////            int x = Integer.valueOf((int)touchPoint[0]);
////            int y = Integer.valueOf((int)touchPoint[1]);
////
////            int XonImage = x * (int) WidthRatio;
////            int YonImage = y * (int) HeightRatio;
//
//            int pixel = ((BitmapDrawable)mImageView.getDrawable()).getBitmap().getPixel(x,y);
////            int pixel = bitmap.getPixel(XonImage,YonImage);
//            int redValue = Color.red(pixel);
//            int blueValue = Color.blue(pixel);
//            int greenValue = Color.green(pixel);
//            TextView colourTextBox = findViewById(R.id.colourTextBox);
//            TextView colourSampleBox = findViewById(R.id.colourSampleBox);
//            String colourBoxString = "R = " + redValue + "\nG = " + greenValue + "\nB = " + blueValue;
//            colourTextBox.setText(colourBoxString);
//            colourSampleBox.setBackgroundColor(Color.rgb(redValue, greenValue, blueValue));
//
//            return false;
//        }

        @Override
        public boolean onTouch(View view, MotionEvent event) {

            float eventX = event.getX();
            float eventY = event.getY();
            float[] eventXY = new float[] {eventX, eventY};

            Matrix invertMatrix = new Matrix();
            ((ImageView)view).getImageMatrix().invert(invertMatrix);

            invertMatrix.mapPoints(eventXY);
            int x = Integer.valueOf((int)eventXY[0]);
            int y = Integer.valueOf((int)eventXY[1]);

//            touchedXY.setText(
//                    "touched position: "
//                            + String.valueOf(eventX) + " / "
//                            + String.valueOf(eventY));
//            invertedXY.setText(
//                    "touched position: "
//                            + String.valueOf(x) + " / "
//                            + String.valueOf(y));

            Drawable imgDrawable = ((ImageView)view).getDrawable();
            Bitmap bitmap = ((BitmapDrawable)imgDrawable).getBitmap();

//            imgSize.setText(
//                    "drawable size: "
//                            + String.valueOf(bitmap.getWidth()) + " / "
//                            + String.valueOf(bitmap.getHeight()));

            //Limit x, y range within bitmap
            if(x < 0){
                x = 0;
            }else if(x > bitmap.getWidth()-1){
                x = bitmap.getWidth()-1;
            }

            if(y < 0){
                y = 0;
            }else if(y > bitmap.getHeight()-1){
                y = bitmap.getHeight()-1;
            }

            int touchedRGB = bitmap.getPixel(x, y);

//            colorRGB.setText("touched color: " + "#" + Integer.toHexString(touchedRGB));
//            colorRGB.setTextColor(touchedRGB);

            int redValue = Color.red(touchedRGB);
            int blueValue = Color.blue(touchedRGB);
            int greenValue = Color.green(touchedRGB);
            TextView colourTextBox = findViewById(R.id.colourTextBox);
            TextView colourSampleBox = findViewById(R.id.colourSampleBox);
            String colourBoxString = "R = " + redValue + "\nG = " + greenValue + "\nB = " + blueValue;
            colourTextBox.setText(colourBoxString);
            colourSampleBox.setBackgroundColor(Color.rgb(redValue, greenValue, blueValue));

            return true;
        }

    };

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mImageView = findViewById(R.id.capturePhotoImageView);
        Button picBtnB = findViewById(R.id.btnCapture);
        picBtnB.setOnClickListener(captureBtnOnclickListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (mCurrentPhotoPath != null) {
                setPic();
                mCurrentPhotoPath = null;
            }
        }
    }
}