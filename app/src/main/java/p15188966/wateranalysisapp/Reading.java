package p15188966.wateranalysisapp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

class Reading {

    private String date;
    private int red, green, blue, userNitrate;
    private double appNitrate;

    Reading() {
        this.date = "";
        this.red = 999;
        this.green = 999;
        this.blue = 999;
        this.userNitrate = 999;
        this.appNitrate = 999;
    }

    Reading(String date, int red, int green, int blue, int userNitrate, double appNitrate) {
        this.date = date;
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.userNitrate = userNitrate;
        this.appNitrate = appNitrate;
    }

    public String getDate() {
        return date;
    }

    public int getRed() {
        return red;
    }

    public int getGreen() {
        return green;
    }

    public int getBlue() {
        return blue;
    }

    public int getUserNitrate() {
        return userNitrate;
    }

    public double getAppNitrate() {
        return appNitrate;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setRed(int red) {
        this.red = red;
    }

    public void setGreen(int green) {
        this.green = green;
    }

    public void setBlue(int blue) {
        this.blue = blue;
    }

    public void setUserNitrate(int userNitrate) {
        this.userNitrate = userNitrate;
    }

    public void setAppNitrate(double appNitrate) {
        this.appNitrate = appNitrate;
    }

    public String toJSONString() {
        JSONObject readings = new JSONObject();
        try {
            readings.put("Date", date);
            readings.put("Red", red);
            readings.put("Green", green);
            readings.put("Blue", blue);
            readings.put("App Nitate", appNitrate);
            readings.put("User Nitrate", userNitrate);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(readings);
        JSONObject finalObj = new JSONObject();
        try {
            finalObj.put("Readings", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return finalObj.toString();
    }
}
