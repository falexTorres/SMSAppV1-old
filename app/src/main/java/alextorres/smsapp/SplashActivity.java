package alextorres.smsapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Thread myThread = new Thread() {
            public void run() {
                try {
                    sleep(2000);

                } catch (Exception e) {
                } finally {

                    Intent intent = new Intent(SplashActivity.this,
                            SMS.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
        myThread.start();
    }
}
