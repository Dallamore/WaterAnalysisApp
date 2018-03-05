//Android 7.0 API 24
//TODO ask for permissions on first install

package p15188966.wateranalysisapp;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.media.ExifInterface;
import android.support.v13.app.ActivityCompat;
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
    private static final int PERMISSION_REQUEST_CAMERA = 0;
    private boolean cameraPerm;

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
            requestCameraPermission();
            if (cameraPerm){
                dispatchTakePictureIntent();
            }
            else{
                requestCameraPermission();
//                Snackbar.make(findViewById(R.id.capturePhotoImageView), R.string.global_nope,
//                        Snackbar.LENGTH_SHORT)
//                        .show();
            }
        }
    };

    ImageView.OnTouchListener mainViewTouchListener = new ImageView.OnTouchListener() {
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

            Drawable imgDrawable = ((ImageView)view).getDrawable();
            Bitmap bitmap = ((BitmapDrawable)imgDrawable).getBitmap();

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

















    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CAMERA) {
            // Request for camera permission.
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission has been granted. Start camera preview Activity.
                Snackbar.make(findViewById(R.id.capturePhotoImageView), R.string.camera_permission_granted,
                        Snackbar.LENGTH_SHORT)
                        .show();
                cameraPerm = true;
            } else {
                // Permission request was denied.
                Snackbar.make(findViewById(R.id.capturePhotoImageView), R.string.camera_permission_nope,
                        Snackbar.LENGTH_SHORT)
                        .show();
                cameraPerm = false;
            }
        }
    }

    private void requestCameraPermission() {
        // Permission has not been granted and must be requested.
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // Display a SnackBar with cda button to request the missing permission.


            Snackbar.make(findViewById(R.id.capturePhotoImageView), R.string.camera_access_required,
                    Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Request the permission
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.CAMERA},
                            PERMISSION_REQUEST_CAMERA);
                }
            }).show();

        } else {
            Snackbar.make(findViewById(R.id.capturePhotoImageView), R.string.camera_unavailable, Snackbar.LENGTH_SHORT).show();
            // Request the permission. The result will be received in onRequestPermissionResult().
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
        }
    }



}