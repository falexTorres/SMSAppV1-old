package alextorres.smsapp;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class EditDraftActivity extends AppCompatActivity {

    EditText num, mess;
    String mID;
    Button sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_draft);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        num = (EditText) findViewById(R.id.editPhoneNo);
        mess = (EditText) findViewById(R.id.editMessage);
        sendButton = (Button) findViewById(R.id.sendButtonEdit);

        try{
            Bundle extras = getIntent().getExtras();
            if (!(extras.isEmpty())) {
                if(extras.containsKey("MESSAGE")){
                    String message = extras.getString("MESSAGE");
                    String[] holder = message.split(":");
                    mess.setText(holder[1]);
                }

                if(extras.containsKey("MESSAGE_ID")){
                    mID = extras.getString("MESSAGE_ID");
                }

                if(extras.containsKey("NUMBER")){
                    String number = extras.getString("NUMBER");
                    num.setText(number);
                }
            }
            extras.clear();
        }catch(Exception e){
            System.out.println("THERE WAS AN ISSUE");
        }


        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendSMS();
            }
        });

    }

    public void sendSMS()
    {
        String number = num.getText().toString();
        String multiNumbers[] = number.split(", *");
        for(String n : multiNumbers) {
            try {
                String query = "_id="+mID;
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(n, null, mess.getText().toString(), null, null);
                Toast.makeText(getApplicationContext(), "message sent", Toast.LENGTH_LONG).show();
                ContentValues values = new ContentValues();
                values.put("address", n);
                values.put("body", mess.getText().toString());
                // Date of the draft message.
                values.put("date_sent", String.valueOf(System.currentTimeMillis()));
                values.put("type", "2");
                // Put the actual thread id here. 0 if there is no thread yet.
                values.put("thread_id", "0");
                getContentResolver().insert(Uri.parse("content://sms/"), values);
                getContentResolver().delete(Uri.parse("content://sms/"),query,null);
            } catch (Exception ex) {
                Toast.makeText(getApplicationContext(), "message failed", Toast.LENGTH_LONG).show();
                ex.printStackTrace();
            }
        }

    }


    public void deleteMessage(View view){
        int numberDeleted = 0;
        String query = "_id="+mID;
        try {
            numberDeleted = getApplicationContext().getContentResolver().delete(Uri.parse("content://sms/"),query,null);
            System.out.println("NUMBER DELETED: "+ numberDeleted);
        }catch(Exception e){
            System.out.println("ERROR in deleting a sms message");
        }

        Intent intent = new Intent(EditDraftActivity.this, SmsRecieve.class);
        startActivity(intent);
    }

}
