package p15188966.wateranalysisapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class PastReadingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_readings);
        readFromFile(this);

    }

    private void readFromFile(Context context) {
        try {
            InputStream inputStream = context.openFileInput("waa_data.json");

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                String ret = stringBuilder.toString();

                jsonDecoder(ret);
            }
        } catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }
    }

    private void  jsonDecoder(String yolo){
        TextView textView1 = findViewById(R.id.pastReadingsContentTextView);
        try{
            JSONObject data=(new JSONObject(yolo)).getJSONObject("readings");
            String date = data.getString("date");
            int rValue = data.getInt("red");
            int gValue = data.getInt("green");
            int bValue = data.getInt("blue");

            String str="Date: " + date + "\n" +
                    "Red: " + rValue + "\n" +
                    "Green: " + gValue + "\n" +
                    "Blue: " + bValue;
            textView1.setText(str);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}