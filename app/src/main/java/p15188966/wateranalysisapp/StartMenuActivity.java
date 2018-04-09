package p15188966.wateranalysisapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class StartMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.startmenuactivity);
        findViewById(R.id.photoButton).setOnClickListener(toPhotoActivityOnclickListener);
        findViewById(R.id.pastButton).setOnClickListener(toPastReadingsOnclickListener);
    }

    Button.OnClickListener toPhotoActivityOnclickListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(StartMenuActivity.this, ImageTouchActivity.class));
        }
    };

    Button.OnClickListener toPastReadingsOnclickListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(StartMenuActivity.this, PastReadingsActivity.class));
//            Toast.makeText(this, R.string.notReadyYet, Toast.LENGTH_SHORT).show();

        }
    };

}
