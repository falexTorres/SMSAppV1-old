package alextorres.smsapp;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.content.Context;
import android.widget.TextView;
import android.widget.Toast;

/*
This was originally to be the delete functionality but we decided to make this the activity where you could decide to reply, forward, or delete
the message instead of simply deleting it.
 */


public class DeleteSmsActivity extends AppCompatActivity {

    TextView text;
    String mID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_sms);
        String message = "";
        Bundle messageID = getIntent().getExtras();
        if (messageID != null) {
            message = messageID.getString("MESSAGE_ID");
            mID = message;
            messageID.clear();
        }
        message = getMessage(getApplicationContext(),message);
        text = (TextView) findViewById(R.id.messagePlaceHolder);
        text.setText(message);
    }


    public String getMessage(Context context, String ID){
        ContentResolver cr = context.getContentResolver();
        String Query = "_ID="+ID;
        String message;
        Cursor MessageCursor = cr.query(Uri.parse("content://sms/"), null, Query, null, null);

        if(MessageCursor.moveToFirst()){
            message = MessageCursor.getString(MessageCursor.getColumnIndexOrThrow("body"));
            MessageCursor.close();
            return message;
        }

        return "No Message to show, error has occurred.";
    }

    public void deleteMessage(){

    }


    public void reply(View view){
        ContentResolver cr = getContentResolver();
        String toWho; //get the persons number and store it here
        String query = "_id="+mID;
        Cursor cursor = cr.query(Uri.parse("content://sms/"),null,query,null,null);

        if(cursor.moveToFirst()){
            toWho = cursor.getString(cursor.getColumnIndexOrThrow("address"));
            Intent intent = new Intent(DeleteSmsActivity.this, SMS.class);
            intent.putExtra("ContactName", toWho);
            startActivity(intent);
            cursor.close();
        }else{
            cursor.close();
            CharSequence text = "There was an error! Sorry!! :(";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(getBaseContext(), text, duration);
            toast.show();
        }

    }

    public void forward(View view){
        ContentResolver cr = getContentResolver();
        String message; //get the persons number and store it here
        String query = "_id="+mID;
        Cursor cursor = cr.query(Uri.parse("content://sms/"),null,query,null,null);

        if(cursor.moveToFirst()){
            message = cursor.getString(cursor.getColumnIndexOrThrow("body"));
            Intent intent = new Intent(DeleteSmsActivity.this, SMS.class);
            System.out.println("Message ID is: " + mID);
            System.out.println("Message is: "+ message);
            intent.putExtra("smsMessage", message);
            startActivity(intent);
            cursor.close();
        }else{
            cursor.close();
            CharSequence text = "There was an error! Sorry!! :(";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(getBaseContext(), text, duration);
            toast.show();
        }

    }



}
