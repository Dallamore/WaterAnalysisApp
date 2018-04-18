//Android 7.0 API 24

package p15188966.wateranalysisapp;

import android.Manifest;
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
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Date;

/**
 * Uses Intent to take a photo, provides tapping abilitiy to get RGB values and uses to calculate Nitrate values.
 */
public class ImageTouchActivity extends AppCompatActivity {
    /**
     * Location the photo is saved
     */
    private String mCurrentPhotoPath;
    /**
     * An individual Reading contain RGB values, Date and time, App calculated Nitrate ppm, and user selected Nitrate ppm
     */
    private final Reading readingItem = new Reading();

    /**
     * Called every time the acitvity is opened, begins by requesting camera permission if needed and setting listeners
     *
     * @param savedInstanceState saves instance of activity, can be used to survive orientation change for example
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.imagetouch);
        requestCameraPermission();
        setListeners();
    }

    /**
     * Sets all the activity's listeners if they do not require specific timing
     */
    private void setListeners() {
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

    /**
     * On Click Listener to take a new photo, begins process by requesting camera permission
     */
    private final Button.OnClickListener newCaptureButtonListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            requestCameraPermission();
        }
    };

    /**
     * Listener only attached when ImageView contains and image, begins JSON read/write process.
     * Also sets the User's selected Nitrate value in the Reading object.
     */
    private final Button.OnClickListener analyseResultsButtonListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            EditText et = findViewById(R.id.userColourTextBox);
            readingItem.setUserNitrate(Integer.parseInt(et.getText().toString()));

            //Checks if file exists, if it does start read sequence, if not start creating it
            JSONReadWriteTools jrw = new JSONReadWriteTools(getApplicationContext(),readingItem);
            if (jrw.isJSONFilePresent(getApplicationContext())) {
                jrw.readFromFile();
            } else {
                //File created with no content
                jrw.writeToFile("");
            }
        }
    };

    /**
     * On Click listener enabling user to select what they believe to be the correct Nitrate value from
     * the numbers besides the gradient slider in the layout
     */
    private final TextView.OnClickListener zeroTouch = new TextView.OnClickListener() {
        @Override
        public void onClick(View v) {
            TextView colourTextBox = findViewById(R.id.userColourTextBox);
            TextView colourSampleBox = findViewById(R.id.userColourSampleBox);
            colourTextBox.setText(R.string.zero);
            colourSampleBox.setBackgroundColor(Color.rgb(217, 192, 162));
        }
    };

    /**
     * On Click listener enabling user to select what they believe to be the correct Nitrate value from
     * the numbers besides the gradient slider in the layout
     */
    private final TextView.OnClickListener twentyTouch = new TextView.OnClickListener() {
        @Override
        public void onClick(View v) {
            TextView colourTextBox = findViewById(R.id.userColourTextBox);
            TextView colourSampleBox = findViewById(R.id.userColourSampleBox);
            colourTextBox.setText(R.string.twenty);
            colourSampleBox.setBackgroundColor(Color.rgb(214, 179, 149));
        }
    };

    /**
     * On Click listener enabling user to select what they believe to be the correct Nitrate value from
     * the numbers besides the gradient slider in the layout
     */
    private final TextView.OnClickListener fourtyTouch = new TextView.OnClickListener() {
        @Override
        public void onClick(View v) {
            TextView colourTextBox = findViewById(R.id.userColourTextBox);
            TextView colourSampleBox = findViewById(R.id.userColourSampleBox);
            colourTextBox.setText(R.string.fourty);
            colourSampleBox.setBackgroundColor(Color.rgb(208, 160, 140));
        }
    };

    /**
     * On Click listener enabling user to select what they believe to be the correct Nitrate value from
     * the numbers besides the gradient slider in the layout
     */
    private final TextView.OnClickListener eightyTouch = new TextView.OnClickListener() {
        @Override
        public void onClick(View v) {
            TextView colourTextBox = findViewById(R.id.userColourTextBox);
            TextView colourSampleBox = findViewById(R.id.userColourSampleBox);
            colourTextBox.setText(R.string.eighty);
            colourSampleBox.setBackgroundColor(Color.rgb(209, 151, 127));
        }
    };

    /**
     * On Click listener enabling user to select what they believe to be the correct Nitrate value from
     * the numbers besides the gradient slider in the layout
     */
    private final TextView.OnClickListener oneSixtyTouch = new TextView.OnClickListener() {
        @Override
        public void onClick(View v) {
            TextView colourTextBox = findViewById(R.id.userColourTextBox);
            TextView colourSampleBox = findViewById(R.id.userColourSampleBox);
            colourTextBox.setText(R.string.oneSixty);
            colourSampleBox.setBackgroundColor(Color.rgb(208, 134, 112));
        }
    };

    /**
     * On Click listener enabling user to select what they believe to be the correct Nitrate value from
     * the numbers besides the gradient slider in the layout
     */
    private final TextView.OnClickListener twoHundredTouch = new TextView.OnClickListener() {
        @Override
        public void onClick(View v) {
            TextView colourTextBox = findViewById(R.id.userColourTextBox);
            TextView colourSampleBox = findViewById(R.id.userColourSampleBox);
            colourTextBox.setText(R.string.twoHundred);
            colourSampleBox.setBackgroundColor(Color.rgb(202, 118, 100));
        }
    };


    /**
     * Requests permission to use the device's camera
     */
    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 0);
    }

    /**
     * Activated if request camera permission is accepted or denied and deals with accordingly
     *
     * @param requestCode  int request code
     * @param permissions  array permissions requested
     * @param grantResults array permission granted
     */
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

    /**
     * Only ever called if camera permission has been granted, creates new Intent to use camera.
     */
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

    /**
     * Activited after picture has been taken successfully
     *
     * @param requestCode request code
     * @param resultCode  result code
     * @param data        photo data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (mCurrentPhotoPath != null) {
                scaleAndSetPic();
                mCurrentPhotoPath = null;
            }
        }
    }

    /**
     * Used if photo taken successfully, scales the image according to the ImageView's dimensions on the devices screen
     */
    private void scaleAndSetPic() {
        ImageView mImageView = findViewById(R.id.capturePhotoImageView);

//        Get the size of the ImageView
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

//        Get the size of the image
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

//        Figure out which way needs to be reduced less
        int scaleFactor = 1;
        if ((targetW > 0) || (targetH > 0)) {
            scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        }

//        Set bitmap options to scale the image decode target
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

//        Decode the JPEG file into a Bitmap
        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

//        Associate the Bitmap to the ImageView
        Bitmap mPhoto = rotateImage(bitmap);
        mImageView.setImageBitmap(mPhoto);
        mImageView.setOnTouchListener(mainViewTouchListener);
    }

    /**
     * Rotates image to always be portrait in ImageView.
     * Primarily for use with Samsung Galaxy Phones
     *
     * @param bitmap photo data
     * @return rotated photo
     */
    private Bitmap rotateImage(Bitmap bitmap) {
        ExifInterface exifInterface;
        int orientation = 0;
        try {
            //retrieves photo
            exifInterface = new ExifInterface(mCurrentPhotoPath);
            orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Matrix matrix = new Matrix();
        //Makes sure image is rotated to portrait
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
        //creates new Bitmap image with new width, height, and rotated matrix data
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    /**
     * Creates a blank file for image to populate, created at the photopath variable's location
     *
     * @return Blank file for an image
     * @throws IOException Incase a temporary file cannot be created
     */
    private File createImageFile() throws IOException {
        String imageFileName = "JPEG_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    /**
     * OnTouch Listener, when users touches the image, the RGB values from the touched Pixel are calculated
     * account for the image's scale on the ImageView.
     */
    private final ImageView.OnTouchListener mainViewTouchListener = new ImageView.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            //Retrieves touched pixel's coordinates
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
            setReadingObjectAndLayout(touchedRGB);
            return true;
        }
    };

    /**
     * Uses the RGB values at the touched point to set the Reading Object and populate informative boxes in
     * the layout.
     *
     * @param touchedRGB Int RGB values of touched Image pixels
     */
    private void setReadingObjectAndLayout(int touchedRGB) {
        //sets Reading object now the information is available
        Date date = new Date();
        readingItem.setDate(DateFormat.getDateTimeInstance().format(date));
        readingItem.setRed(Color.red(touchedRGB));
        readingItem.setGreen(Color.green(touchedRGB));
        readingItem.setBlue(Color.blue(touchedRGB));

        //Sets data to respective places in layout
        TextView colourTextBox = findViewById(R.id.colourTextBox);
        TextView colourSampleBox = findViewById(R.id.colourSampleBox);
        String colourBoxString = "R = " + readingItem.getRed() + "\nG = " + readingItem.getGreen() + "\nB = " + readingItem.getBlue();
        colourTextBox.setText(colourBoxString);
        colourSampleBox.setBackgroundColor(Color.rgb(readingItem.getRed(), readingItem.getGreen(), readingItem.getBlue()));

        //Enables analyse button
        Button analsyeResultsButton = findViewById(R.id.analyseResultsButton);
        analsyeResultsButton.setOnClickListener(analyseResultsButtonListener);

        calculatePPM();
    }

    /**
     * Uses the RGB values and RGB to Nitrate ppm conversion formula to output Nitrate value.
     */
    private void calculatePPM() {
        TextView ppmText = findViewById(R.id.nitratePPMText);
        //uses the RGB to Nitrate conversion
        double nitratePPM = ((readingItem.getGreen() - 135.5) / -0.29375);
        //limits double to two decimal places
        DecimalFormat df = new DecimalFormat("#.##");
        nitratePPM = Double.valueOf(df.format(nitratePPM));
        //Sets to layout
        String titleText = this.getString(R.string.nitrateTitle);
        String fullText = titleText + "\n" + nitratePPM;
        ppmText.setText(fullText);
        //sets data in Reading object
        readingItem.setAppNitrate(nitratePPM);
    }
}