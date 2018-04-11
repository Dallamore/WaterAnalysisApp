package p15188966.wateranalysisapp;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
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
        setContentView(R.layout.pastreadings);
        readFromFile();
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
                jsonDecoder(ret);
            }
        } catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }
    }

//    public static final String JSON_STRING="{\"Readings\":[{\"date\":\"02.12.1992\",\"red\":50,\"green\":60,\"blue\":70},{\"date\":\"02.12.1992\",\"red\":50,\"green\":60,\"blue\":70},{\"date\":\"22.02.1997\",\"red\":345,\"green\":769,\"blue\":246}]}\n";
//    public static final String JSON_STRING="{\"Readings\":[{\"date\":\"02.12.1992\",\"red\":50,\"green\":60,\"blue\":70},{\"date\":\"02.12.1992\",\"red\":50,\"green\":60,\"blue\":70},{\"date\":\"22.02.1997\",\"red\":345,\"green\":769,\"blue\":246},{\"date\":\"02.12.1992\",\"red\":50,\"green\":60,\"blue\":70},{\"date\":\"02.12.1992\",\"red\":50,\"green\":60,\"blue\":70},{\"date\":\"02.12.1992\",\"red\":50,\"green\":60,\"blue\":70},{\"date\":\"02.12.1992\",\"red\":50,\"green\":60,\"blue\":70},{\"date\":\"02.12.1992\",\"red\":50,\"green\":60,\"blue\":70},{\"date\":\"02.12.1992\",\"red\":50,\"green\":60,\"blue\":70}]}\n";

    private void jsonDecoder(String jsonString) {
        try {
            JSONObject data = new JSONObject(jsonString);
            JSONArray jRay = data.getJSONArray("Readings");

            for (int i = 0; i < jRay.length(); i++) {
                String date = jRay.getJSONObject(i).getString("Date");
                int rValue = jRay.getJSONObject(i).getInt("Red");
                int gValue = jRay.getJSONObject(i).getInt("Green");
                int bValue = jRay.getJSONObject(i).getInt("Blue");
                int bgColour;
                if(i % 2 == 0){
                    bgColour = Color.rgb(245,245,245);
                }
                else bgColour = Color.WHITE;

                addToScrollView("Date: " + date + "\n" +
                        "Red: " + rValue + "\n" +
                        "Green: " + gValue + "\n" +
                        "Blue: " + bValue,
                        rValue, gValue, bValue, bgColour);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addToScrollView(String data, int red, int green, int blue, int bgColour){
        TableLayout tableLayout = findViewById(R.id.pastTableLayout);

        TableRow tableRow = new TableRow(this);
        tableRow.setBackgroundColor(bgColour);
        tableRow.setLayoutParams(new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.MATCH_PARENT));

        TextView resultsText = new TextView(this);
        resultsText.setText(data);
//        resultsText.setPadding(5, 5, 400, 5);
        tableRow.addView(resultsText);

        TextView resultsColour = new TextView(this);
        resultsColour.setBackgroundColor(Color.rgb(red, green, blue));
//        resultsColour.setPadding(5, 5, 50, 150);
        tableRow.addView(resultsColour);

        tableLayout.addView(tableRow, new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.MATCH_PARENT));
    }
}