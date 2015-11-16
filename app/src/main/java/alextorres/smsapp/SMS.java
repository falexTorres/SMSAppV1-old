package alextorres.smsapp;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SMS extends AppCompatActivity {

    private Button btnSendSMS;
    public static EditText txtPhoneNo;
    public EditText txtMessage;
    public View auto_reply_off = null;
    Toolbar mToolbar;

    public SMS(String number) {
        if((number.length()==10) || (number.length()==11)) {
            txtPhoneNo.setText(number);
        }

        else txtMessage.setText(number);
    }

    public SMS(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN); //To stop keyboard from automatically popping up

        mToolbar = (Toolbar) findViewById(R.id.toolbar_sms);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mToolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Home");

        btnSendSMS = (Button) findViewById(R.id.btnSendSMS);
        txtPhoneNo = (EditText) findViewById(R.id.txtPhoneNo);
        txtMessage = (EditText) findViewById(R.id.txtMessage);


        try{
            Bundle extras = getIntent().getExtras();
            if (!(extras.isEmpty())) {
                if(extras.containsKey("ContactName")){
                    String number = extras.getString("ContactName");
                    txtPhoneNo.setText(number);
                    extras.clear();
                }else{
                    String message = extras.getString("smsMessage");
                    txtMessage.setText(message);
                    extras.clear();
                }
            }
        }catch(Exception e){

        }

        btnSendSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendSMS();
            }
        });

    }


    public void sendSMS()
    {
        String number = txtPhoneNo.getText().toString();
        String multiNumbers[] = number.split(", *");

        for(String n : multiNumbers) {
            try {

                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(n, null, txtMessage.getText().toString(), null, null);
                Toast.makeText(getApplicationContext(), "message sent", Toast.LENGTH_LONG).show();
            } catch (Exception ex) {
                Toast.makeText(getApplicationContext(), "message failed", Toast.LENGTH_LONG).show();
                ex.printStackTrace();
            }
        }


    }

    public void sendSMS(String number)
    {
        number = txtPhoneNo.getText().toString();
        String multiNumbers[] = number.split(", *");

        for(String n : multiNumbers) {
            try {

                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(n, null, txtMessage.getText().toString(), null, null);
                Toast.makeText(getApplicationContext(), "message sent", Toast.LENGTH_LONG).show();
            } catch (Exception ex) {
                Toast.makeText(getApplicationContext(), "message failed", Toast.LENGTH_LONG).show();
                ex.printStackTrace();
            }
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sm, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void toDrafts(View view){
        //Store the message in the draft folder so that it shows in Messaging apps.
        ContentValues values = new ContentValues();
        if(txtPhoneNo.getText().toString().trim().length() == 0){
            values.put("address", " ");
        }else {
            // Message address.
            values.put("address", txtPhoneNo.getText().toString());
        }

        if(txtMessage.getText().toString().trim().length() == 0){
            values.put("body", "Draft");
        }else{
            // Message body.
            values.put("body", txtMessage.getText().toString() + " DRAFT");
        }
        // Date of the draft message.
        values.put("date", String.valueOf(System.currentTimeMillis()));
        values.put("type", "3");
        // Put the actual thread id here. 0 if there is no thread yet.
        values.put("thread_id", "0");
        getContentResolver().insert(Uri.parse("content://sms/draft"), values);
        Intent intent = new Intent(this, DisplayDraftActivity.class);
        startActivity(intent);
    }

    public void toMessages(View view){
        Intent intent = new Intent(this, SmsRecieve.class);
        startActivity(intent);
    }

    public void toContacts(View view){
        Intent intent = new Intent(this, ContactListActivity.class);
        startActivity(intent);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void showHelpBox(View view)
    {
        DialogFragment help = new HelpBox();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            help.show(getFragmentManager(), "What are those?");
        }
    }

    public void autoReplyOn(View view)
    {
        SmsRecieve.auto_reply_status = true;
        Toast.makeText(getApplicationContext(), "auto reply turned on", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, SmsRecieve.class);
        startActivity(intent);

    }

    public void autoReplyOff(View view)
    {
        SmsRecieve.auto_reply_status = false;
        Toast.makeText(getApplicationContext(), "auto reply turned off", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, SmsRecieve.class);
        startActivity(intent);
    }

}