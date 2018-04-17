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
import android.support.media.ExifInterface;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Date;

public class ImageTouchActivity extends AppCompatActivity {
    private String mCurrentPhotoPath;
    private final Reading readingItem = new Reading();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.imagetouch);
        requestCameraPermission();
        setListeners();
    }

    private void setListeners(){
        Button newPhotoButton = findViewById(R.id.newPhotoButton);
        newPhotoButton.setOnClickListener(newCaptureButtonListener);

        TextView zeroText = findViewById(R.id.textViewZero);
        zeroText.setOnClickListener(zeroTouch);

        TextView twentyText = findViewById(R.id.textViewTwenty);
        twentyText.setOnClickListener(twentyTouch);

        TextView fourtyText = findViewById(R.id.textViewFourty);
        fourtyText.setOnClickListener(fourtyTouch);

        TextView eightyText = findViewById(R.id.textViewEighty);
        eightyText.setOnClickListener(eightyTouch);

        TextView oneSixtyText = findViewById(R.id.textViewOneSixty);
        oneSixtyText.setOnClickListener(oneSixtyTouch);

        TextView twoHundredText = findViewById(R.id.textViewTwoHundred);
        twoHundredText.setOnClickListener(twoHundredTouch);
    }

    private final Button.OnClickListener newCaptureButtonListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            requestCameraPermission();
        }
    };

    private final Button.OnClickListener analyseResultsButtonListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            EditText et = findViewById(R.id.userColourTextBox);
            readingItem.setUserNitrate(Integer.parseInt(et.getText().toString()));

            if (isJSONFilePresent(getApplicationContext())) {
                readFromFile();
            } else {
                writeToFile("");
            }
        }
    };

    private final TextView.OnClickListener zeroTouch = new TextView.OnClickListener(){
        @Override
        public  void onClick(View v){
            TextView colourTextBox = findViewById(R.id.userColourTextBox);
            TextView colourSampleBox = findViewById(R.id.userColourSampleBox);
            colourTextBox.setText(R.string.zero);
            colourSampleBox.setBackgroundColor(Color.rgb(217, 192, 162));
        }
    };

    private final TextView.OnClickListener twentyTouch = new TextView.OnClickListener(){
        @Override
        public  void onClick(View v){
            TextView colourTextBox = findViewById(R.id.userColourTextBox);
            TextView colourSampleBox = findViewById(R.id.userColourSampleBox);
            colourTextBox.setText(R.string.twenty);
            colourSampleBox.setBackgroundColor(Color.rgb(214, 179, 149));
        }
    };

    private final TextView.OnClickListener fourtyTouch = new TextView.OnClickListener(){
        @Override
        public  void onClick(View v){
            TextView colourTextBox = findViewById(R.id.userColourTextBox);
            TextView colourSampleBox = findViewById(R.id.userColourSampleBox);
            colourTextBox.setText(R.string.fourty);
            colourSampleBox.setBackgroundColor(Color.rgb(208, 160, 140));
        }
    };

    private final TextView.OnClickListener eightyTouch = new TextView.OnClickListener(){
        @Override
        public  void onClick(View v){
            TextView colourTextBox = findViewById(R.id.userColourTextBox);
            TextView colourSampleBox = findViewById(R.id.userColourSampleBox);
            colourTextBox.setText(R.string.eighty);
            colourSampleBox.setBackgroundColor(Color.rgb(209, 151, 127));
        }
    };

    private final TextView.OnClickListener oneSixtyTouch = new TextView.OnClickListener(){
        @Override
        public  void onClick(View v){
            TextView colourTextBox = findViewById(R.id.userColourTextBox);
            TextView colourSampleBox = findViewById(R.id.userColourSampleBox);
            colourTextBox.setText(R.string.oneSixty);
            colourSampleBox.setBackgroundColor(Color.rgb(208, 134, 112));
        }
    };

    private final TextView.OnClickListener twoHundredTouch = new TextView.OnClickListener(){
        @Override
        public  void onClick(View v){
            TextView colourTextBox = findViewById(R.id.userColourTextBox);
            TextView colourSampleBox = findViewById(R.id.userColourSampleBox);
            colourTextBox.setText(R.string.twoHundred);
            colourSampleBox.setBackgroundColor(Color.rgb(202, 118, 100));
        }
    };

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 0);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission has been granted. Start camera preview Activity.
                startCamera();
            } else {
                // Permission request was denied.
                Toast.makeText(this, R.string.camera_permission_denied, Toast.LENGTH_SHORT).show();
            }
        }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (mCurrentPhotoPath != null) {
                scaleAndSetPic();
                mCurrentPhotoPath = null;
            }
        }
    }

    private void scaleAndSetPic() {
        ImageView mImageView = findViewById(R.id.capturePhotoImageView);

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

    private File createImageFile() throws IOException {
        String imageFileName = "JPEG_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private final ImageView.OnTouchListener mainViewTouchListener = new ImageView.OnTouchListener() {
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

            Date date = new Date();
            readingItem.setDate(DateFormat.getDateTimeInstance().format(date));
            readingItem.setRed(Color.red(touchedRGB));
            readingItem.setGreen(Color.green(touchedRGB));
            readingItem.setBlue(Color.blue(touchedRGB));

            TextView colourTextBox = findViewById(R.id.colourTextBox);
            TextView colourSampleBox = findViewById(R.id.colourSampleBox);
            String colourBoxString = "R = " + readingItem.getRed() + "\nG = " + readingItem.getGreen() + "\nB = " + readingItem.getBlue();
            colourTextBox.setText(colourBoxString);
            colourSampleBox.setBackgroundColor(Color.rgb(readingItem.getRed(), readingItem.getGreen(), readingItem.getBlue()));

            Button analsyeResultsButton = findViewById(R.id.analyseResultsButton);
            analsyeResultsButton.setOnClickListener(analyseResultsButtonListener);
            calculatePPM();

            return true;
        }
    };

    private void calculatePPM() {
        TextView ppmText = findViewById(R.id.nitratePPMText);
        double nitratePPM = ((readingItem.getGreen()-135.5)/-0.29375);
        DecimalFormat df = new DecimalFormat("#.##");
        nitratePPM = Double.valueOf(df.format(nitratePPM));
        String titleText = this.getString(R.string.nitrateTitle);
        String fullText = titleText + "\n" + nitratePPM;
        ppmText.setText(fullText);
        readingItem.setAppNitrate(nitratePPM);
    }

    private boolean isJSONFilePresent(Context context) {
        String path = context.getFilesDir().getAbsolutePath() + "/" + "waa_data.json";
        File file = new File(path);
        return file.exists();
    }

    private void createJsonFile(String jsonString) {
        try {
            FileOutputStream fos = openFileOutput("waa_data.json", Context.MODE_PRIVATE);
            if (jsonString != null) {
                fos.write(jsonString.getBytes());
            }
            fos.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    private void readFromFile() {
        try {
            InputStream inputStream = openFileInput("waa_data.json");
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString;
                StringBuilder stringBuilder = new StringBuilder();
                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }
                inputStream.close();
                String ret = stringBuilder.toString();
                JSONObject data;
                JSONArray jRay;
                JSONArray finalJray = new JSONArray();
                try {
                    data = new JSONObject(ret);
                    jRay = data.getJSONArray("Readings");
                    JSONObject currentData = new JSONObject();
                    try {
                        currentData.put("Date", readingItem.getDate());
                        currentData.put("Red", readingItem.getRed());
                        currentData.put("Green", readingItem.getGreen());
                        currentData.put("Blue", readingItem.getBlue());
                        currentData.put("App Nitate", readingItem.getAppNitrate());
                        currentData.put("User Nitrate", readingItem.getUserNitrate());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    finalJray.put(currentData);
                    for (int i = 0; i < jRay.length(); i++) {
                        finalJray.put(jRay.get(i));
                    }
                    JSONObject finalObj = new JSONObject();
                    try {
                        finalObj.put("Readings", finalJray);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    writeToFile(finalObj.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }
    }

    private void writeToFile(String jsonData) {
        try {
            if (isJSONFilePresent(this)) {
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(this.openFileOutput("waa_data.json", Context.MODE_PRIVATE));
                outputStreamWriter.write(jsonData);
                outputStreamWriter.close();
                Toast.makeText(this, R.string.analysisSuccess, Toast.LENGTH_SHORT).show();
            } else {
                createJsonFile(readingItem.toJSONString());
                Toast.makeText(this, R.string.analysisSuccess, Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }
}