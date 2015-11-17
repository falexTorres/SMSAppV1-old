package alextorres.smsapp;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

public class SaveContactActivity extends AppCompatActivity {

    EditText name, number;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_contact);

        number = (EditText) findViewById(R.id.newContactNumber);
        name = (EditText) findViewById(R.id.newContactName);

        try{
            Bundle extras = getIntent().getExtras();
            if (!(extras.isEmpty())) {
                if(extras.containsKey("CONTACT_NUMBER")){
                    String phoneNumber = extras.getString("CONTACT_NUMBER");
                    number.setText(phoneNumber);
                    extras.clear();
                }
            }
        }catch(Exception e){
        }
    }



    public void addContact(View view){
        ContentValues values = new ContentValues();
        values.put("display_name_primary", name.getText().toString());
       // getContentResolver().insert(ContactsContract.Contacts.CONTENT_URI, values);
        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
        intent.putExtra(ContactsContract.Intents.Insert.NAME, name.getText().toString());
        intent.putExtra(ContactsContract.Intents.Insert.PHONE, number.getText().toString());
        startActivityForResult(intent,1);
    }


}
