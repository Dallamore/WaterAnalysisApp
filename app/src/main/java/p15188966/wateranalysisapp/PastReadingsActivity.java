package p15188966.wateranalysisapp;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
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

/**
 * Controls structure and view of Past Readings Activity, allows app user to view all readings saved
 * to the external JSON file.
 */
public class PastReadingsActivity extends AppCompatActivity {

    /**
     * Called every time the acitvity is opened, initiates the reading of the JSON file.
     *
     * @param savedInstanceState saves instance of activity, can be used to survive orientation change for example
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pastreadings);
        readFromFile();
    }

    /**
     * Retrives the JSON data from external file using InputStrem and converts to a String.
     */
    private void readFromFile() {
        try {
            InputStream inputStream = openFileInput("waa_data.json");
            if (inputStream != null) {
                //Reads all file's contents
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString;
                StringBuilder stringBuilder = new StringBuilder();
                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }
                inputStream.close();
                //Converts all file's contents to a single string
                String ret = stringBuilder.toString();
                //passes string to be converts to JSON
                jsonDecoder(ret);
            }
        } catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }
    }

    /**
     * Converts parameter String to multiple JSONObjects inside a JSONArray using a for loop.
     * For loop also adds a background colour value to each item.
     *
     * @param jsonString contents of JSON file in String form
     */
    private void jsonDecoder(String jsonString) {
        try {
            //Converts to object first, then seperates into array
            JSONObject data = new JSONObject(jsonString);
            JSONArray jRay = data.getJSONArray("Readings");

            //iterates through every item in JSON array
            for (int i = 0; i < jRay.length(); i++) {
                Reading singleResult = new Reading(
                        jRay.getJSONObject(i).getString("Date"),
                        jRay.getJSONObject(i).getInt("Red"),
                        jRay.getJSONObject(i).getInt("Green"),
                        jRay.getJSONObject(i).getInt("Blue"),
                        jRay.getJSONObject(i).getInt("User Nitrate"),
                        jRay.getJSONObject(i).getDouble("App Nitate")
                );
                //background colour makes table look nicer
                int bgColour;
                if (i % 2 == 0) {
                    bgColour = Color.rgb(245, 245, 245);
                } else bgColour = Color.WHITE;

                addToScrollView(singleResult, bgColour);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Receives a reading object and a background colour int. Reading object is converted to formatted and
     * placed into table row. Reading's RGB values are used to generate colour box which is also added to view.
     * Background colour is used to increase readability of table by making every other row grey.
     *
     * @param resultItem contains String date, ints red, green, blue, and user nitrate, and double app nitrate
     * @param bgColour   int every second item grey
     */
    private void addToScrollView(Reading resultItem, int bgColour) {
        //Gets TableLayout object from layout
        TableLayout tableLayout = findViewById(R.id.pastTableLayout);

        //Create new row to add to table
        TableRow tableRow = new TableRow(this);
        tableRow.setBackgroundColor(bgColour);

        //The data contained in the Reading Object added to a TextView, which is added to the table row
        TextView resultsText = new TextView(this);
        resultsText.setText(resultItem.toFormattedJSONString());
        tableRow.addView(resultsText);

        //The RGB values are used to make a coloured sample box which is added to the TableRow
        TextView resultsColour = new TextView(this);
        resultsColour.setBackgroundColor(Color.rgb(resultItem.getRed(), resultItem.getGreen(), resultItem.getBlue()));
        tableRow.addView(resultsColour);

        //Gotta put it in the middle nice and neat
        tableRow.setGravity(Gravity.CENTER);

        //TableRow is added to the layout
        tableLayout.addView(tableRow, new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.MATCH_PARENT));
    }
}