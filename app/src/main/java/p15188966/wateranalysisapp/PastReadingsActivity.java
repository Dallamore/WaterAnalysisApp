package p15188966.wateranalysisapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONException;
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
        String ret = "";
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
                ret = stringBuilder.toString();

                setReadingsTextBox(ret);

//                try {
//                    JSONObject json = new JSONObject(ret);
//                    JSONdecoder(json);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
            }
        } catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }
    }

    private void JSONdecoder(JSONObject data) {
        try {
            String redDat = data.getString("red");
            setReadingsTextBox(redDat + " " + "Alex Smells");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setReadingsTextBox(String setMe) {
        TextView tv1 = findViewById(R.id.pastReadingsContentTextView);
        tv1.setText(setMe);

    }


}
