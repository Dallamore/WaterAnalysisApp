package p15188966.wateranalysisapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Start screen on the app
 */
public class StartMenuActivity extends AppCompatActivity {

    /**
     * Called every time the acitvity is opened, sets listeners to buttons
     *
     * @param savedInstanceState saves instance of activity, can be used to survive orientation change for example
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.startmenuactivity);
        //adds listeners
        findViewById(R.id.photoButton).setOnClickListener(toPhotoActivityOnclickListener);
        findViewById(R.id.pastButton).setOnClickListener(toPastReadingsOnclickListener);
    }

    /**
     * Takes user to ImageTouchActivity
     */
    private final Button.OnClickListener toPhotoActivityOnclickListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(StartMenuActivity.this, ImageTouchActivity.class));
        }
    };

    /**
     * Takes user to PastReadingsActivity
     */
    private final Button.OnClickListener toPastReadingsOnclickListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(StartMenuActivity.this, PastReadingsActivity.class));
        }
    };
}