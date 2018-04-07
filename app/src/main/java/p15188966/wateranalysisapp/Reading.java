package p15188966.wateranalysisapp;

public class Reading {
    final private String date;
    final private int redVal;
    final private int greVal;
    final private int blueVal;

    public Reading(String date, int redVal, int greVal, int blueVal) {
        this.date = date;
        this.redVal = redVal;
        this.greVal = greVal;
        this.blueVal = blueVal;
    }

    public String getDate() {
        return date;
    }

    public int getRedVal() {
        return redVal;
    }

    public int getGreVal() {
        return greVal;
    }

    public int getBlueVal() {
        return blueVal;
    }

    public String toJsonObjectString(){
        return "not yet";
    }

    @Override
    public String toString() {
        return "Reading{" +
                "date='" + date + '\'' +
                ", redVal=" + redVal +
                ", greVal=" + greVal +
                ", blueVal=" + blueVal +
                '}';
    }
}
