package p15188966.wateranalysisapp;

import android.content.Context;
import android.util.Log;
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

/**
 * Provides a set of method used for Reading from and Writing too JSON file
 */
class JSONReadWriteTools {

    /**
     * The context from where the reading/writing stems
     */
    private final Context context;
    /**
     * The class containing data of individual reading
     */
    private Reading reading;
    /**
     * Name of the JSON File
     */
    private final String JSONFileName = "waa_data.json";

    /**
     * Constructor utilising the context and the reading object
     *
     * @param context current application context
     * @param reading structure containing Date, RGB, and Nitrate values
     */
    JSONReadWriteTools(Context context, Reading reading) {
        this.context = context;
        this.reading = reading;
    }

    /**
     * Constructor using only the context
     *
     * @param context current application context
     */
    JSONReadWriteTools(Context context) {
        this.context = context;
    }

    /**
     * Checks if the JSON already exists
     *
     * @param context application context
     * @return boolean true if file does exist
     */
    public boolean isJSONFilePresent(Context context) {
        String path = context.getFilesDir().getAbsolutePath() + "/" + JSONFileName;
        File file = new File(path);
        return file.exists();
    }

    /**
     * Called if file does not already exist, uses a FileOutStream to generate file and populate with
     * JSON file in String form.
     *
     * @param jsonString Fully formatted JSON structure in String form
     */
    private void createJsonFile(Context context, String jsonString) {
        try {
            FileOutputStream fos = context.openFileOutput(JSONFileName, Context.MODE_PRIVATE);

            if (jsonString != null) {
                fos.write(jsonString.getBytes());
            }
            fos.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    /**
     * Reads the JSON structure from the file, used when adding new item to the file
     */
    public void readFromFile() {
        try {
            //Retrieves content of file
//            InputStream inputStream = openFileInput(JSONFileName);
            InputStream inputStream = context.openFileInput(JSONFileName);

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString;
                StringBuilder stringBuilder = new StringBuilder();
                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }
                inputStream.close();
                //Converts contents to string
                String ret = stringBuilder.toString();
                JSONObject data;
                JSONArray jRay;
                JSONArray finalJray = new JSONArray();
                try {
                    //converts string to JSONObject
                    data = new JSONObject(ret);
                    jRay = data.getJSONArray("Readings");
                    JSONObject currentData = new JSONObject();
                    //Adds current Reading to object
                    try {
                        currentData.put("Date", reading.getDate());
                        currentData.put("Red", reading.getRed());
                        currentData.put("Green", reading.getGreen());
                        currentData.put("Blue", reading.getBlue());
                        currentData.put("App Nitate", reading.getAppNitrate());
                        currentData.put("User Nitrate", reading.getUserNitrate());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //adds current object to array
                    finalJray.put(currentData);
                    for (int i = 0; i < jRay.length(); i++) {
                        finalJray.put(jRay.get(i));
                    }
                    JSONObject finalObj = new JSONObject();
                    //Array converted back to an object
                    try {
                        finalObj.put("Readings", finalJray);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //writes to the file
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

    /**
     * Writes a JSON structure in String form to an external file
     *
     * @param jsonData fully formatted JSON structure in string form
     */
    public void writeToFile(String jsonData) {
        try {
            //checks for file
            if (isJSONFilePresent(context)) {
                //actually writes data to file
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(JSONFileName, Context.MODE_PRIVATE));
                outputStreamWriter.write(jsonData);
                outputStreamWriter.close();
                //Visual confirmation for the user
                Toast.makeText(context, R.string.analysisSuccess, Toast.LENGTH_SHORT).show();
            } else {
                //creates the empty JSON file and writes data to it
                createJsonFile(context, reading.toJSONString());
                //Visual confirmation for user
                Toast.makeText(context, R.string.analysisSuccess, Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
            //In case of failure, informs user of issue
            Toast.makeText(context, R.string.analysisFailure, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Retrives the JSON data from external file using InputStrem and converts to a String.
     *
     * @return String containing contents of JSON file
     */
    public String fileToString() {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            InputStream inputStream = context.openFileInput(JSONFileName);
            if (inputStream != null) {
                //Reads all file's contents
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString;

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }
                inputStream.close();
            }
        } catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }
        return stringBuilder.toString();
    }
}