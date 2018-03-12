//Android 7.0 API 24

package p15188966.wateranalysisapp;

import android.Manifest;
import android.content.Context;
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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Date;

public class ImageTouchActivity extends AppCompatActivity {
    private ImageView mImageView;
    private String mCurrentPhotoPath;

    private int redValue, greenValue, blueValue;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.imagetouch);
        mImageView = findViewById(R.id.capturePhotoImageView);
        requestCameraPermission();

        //Android toolbar
        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_analyse:
                writeToFile(redValue, greenValue, blueValue, this);
                return true;

            case R.id.new_capture:
                requestCameraPermission();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    private Bitmap rotateImage(Bitmap bitmap) {
        ExifInterface exifInterface;
        int orientation = 0;
        try {
            exifInterface = new ExifInterface(mCurrentPhotoPath);
            orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Matrix matrix = new Matrix();
        switch (orientation) {
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

    private void scaleAndSetPic() {
        /* Get the size of the ImageView */
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

		/* Get the size of the image */
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

		/* Figure out which way needs to be reduced less */
        int scaleFactor = 1;
        if ((targetW > 0) || (targetH > 0)) {
            scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        }

		/* Set bitmap options to scale the image decode target */
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

		/* Decode the JPEG file into a Bitmap */
        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

		/* Associate the Bitmap to the ImageView */
        Bitmap mPhoto = rotateImage(bitmap);
        mImageView.setImageBitmap(mPhoto);
        mImageView.setOnTouchListener(mainViewTouchListener);
    }

    private void startCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            //Create file where the photo should go
            File photoFile;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
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

    private File createImageFile() throws IOException {
        // Create an image file name
        String imageFileName = "JPEG_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    ImageView.OnTouchListener mainViewTouchListener = new ImageView.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent event) {

            float eventX = event.getX();
            float eventY = event.getY();
            float[] eventXY = new float[]{eventX, eventY};

            Matrix invertMatrix = new Matrix();
            ((ImageView) view).getImageMatrix().invert(invertMatrix);

            invertMatrix.mapPoints(eventXY);
            int x = (int) eventXY[0];
            int y = (int) eventXY[1];

            Drawable imgDrawable = ((ImageView) view).getDrawable();
            Bitmap bitmap = ((BitmapDrawable) imgDrawable).getBitmap();

            //Limit x, y range within bitmap
            if (x < 0) {
                x = 0;
            } else if (x > bitmap.getWidth() - 1) {
                x = bitmap.getWidth() - 1;
            }

            if (y < 0) {
                y = 0;
            } else if (y > bitmap.getHeight() - 1) {
                y = bitmap.getHeight() - 1;
            }

            int touchedRGB = bitmap.getPixel(x, y);

            redValue = Color.red(touchedRGB);
            blueValue = Color.blue(touchedRGB);
            greenValue = Color.green(touchedRGB);
            TextView colourTextBox = findViewById(R.id.colourTextBox);
            TextView colourSampleBox = findViewById(R.id.colourSampleBox);
            String colourBoxString = "R = " + redValue + "\nG = " + greenValue + "\nB = " + blueValue;
            colourTextBox.setText(colourBoxString);
            colourSampleBox.setBackgroundColor(Color.rgb(redValue, greenValue, blueValue));

            return true;
        }

    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (mCurrentPhotoPath != null) {
                scaleAndSetPic();
                mCurrentPhotoPath = null;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 0) {
            // Request for camera permission.
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission has been granted. Start camera preview Activity.
                startCamera();
            } else {
                // Permission request was denied.
                Snackbar.make(findViewById(R.id.capturePhotoImageView), R.string.camera_permission_denied,
                        Snackbar.LENGTH_SHORT)
                        .show();
            }
        }
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA}, 0);
    }

    private void writeToFile(int red, int green, int blue, Context context) {
        boolean isFilePresent = isFilePresent(this);
        if(isFilePresent) {
            try {
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("waa_data.json", Context.MODE_PRIVATE));
                outputStreamWriter.write(JSONmaker(red,green,blue).toString());
                outputStreamWriter.close();
                Toast.makeText(context, "Successfully analysed", Toast.LENGTH_SHORT).show();
            }
            catch (IOException e) {
                Log.e("Exception", "File write failed: " + e.toString());
            }
        } else {
            createJsonFile(JSONmaker(red,green,blue).toString());
        }
    }

    private JSONObject JSONmaker(int red, int green, int blue) {
        JSONObject readings = new JSONObject();
        Date date = new Date();
        try {
            readings.put("Date", date);
            readings.put("Red", red);
            readings.put("Green", green);
            readings.put("Blue", blue);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(readings);
        JSONObject finalObj = new JSONObject();
        try {
            finalObj.put("readings", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return finalObj;
    }

    public boolean isFilePresent(Context context) {
        String path = context.getFilesDir().getAbsolutePath() + "/" + "waa_data.json";
        File file = new File(path);
        return file.exists();
    }

    private void createJsonFile(String jsonString){
        try {
            FileOutputStream fos = openFileOutput("waa_data.json",Context.MODE_PRIVATE);
            if (jsonString != null) {
                fos.write(jsonString.getBytes());
            }
            fos.close();

        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
}