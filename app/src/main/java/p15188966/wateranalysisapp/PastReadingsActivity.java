package p15188966.wateranalysisapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
        setContentView(R.layout.activity_past_readings);
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

    private void jsonDecoder(String jsonString){
        TextView textView1 = findViewById(R.id.pastReadingsContentTextView);
        try{
            JSONObject data = new JSONObject(jsonString);
            JSONArray jRay = data.getJSONArray("Readings");
            String str = "";
            textView1.setText(str);
            for (int i = 0; i < jRay.length(); i++){
                String date = jRay.getJSONObject(i).getString("Date");
                int rValue = jRay.getJSONObject(i).getInt("Red");
                int gValue = jRay.getJSONObject(i).getInt("Green");
                int bValue = jRay.getJSONObject(i).getInt("Blue");
                String tempString = textView1.getText() + "Date: " + date + "\n" +
                        "Red: " + rValue + "\n" +
                        "Green: " + gValue + "\n" +
                        "Blue: " + bValue + "\n\n";
                textView1.setText(tempString);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}












//            HashMap<Integer, ArrayList<JSONObject>> hashReads = new HashMap<>();
//
//
//            JSONObject data=(new JSONObject(JSON_STRING)).getJSONObject("Readings");
//
//            JSONArray jRay = data.getJSONArray("Readings");
//
//            for (int i = 0; i < data.length(); i++){
//                ArrayList<JSONObject> current = new ArrayList<>();
//                current.add(jRay.getJSONObject(i));
//                hashReads.put(i,current);
//            }
//
////            String date = data.getString("date");
////            int rValue = data.getInt("red");
////            int gValue = data.getInt("green");
////            int bValue = data.getInt("blue");
////
////            String str="Date: " + date + "\n" +
////                    "Red: " + rValue + "\n" +
////                    "Green: " + gValue + "\n" +
////                    "Blue: " + bValue;







//
//    ArrayList<String> contactList;
//
//    JSONObject jsonObj = new JSONObject(JSON_STRING);
//    JSONArray contacts = jsonObj.getJSONArray("Readings");
//
//            Toast.makeText(this, "5", Toast.LENGTH_SHORT).show();
//
//                    String str = " ";
//
//                    for (int i = 0; i < contacts.length(); i++){
//        JSONObject cJO = contacts.getJSONObject(i);
//        Integer cInt = i;
//        String cDat = cJO.getString("date");
////                HashMap<Integer, String> cHas = new HashMap<>();
////                cHas.put(cInt, cDat);
//        str = (cInt.toString() + " " + cDat);
//        }