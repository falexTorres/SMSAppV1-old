package alextorres.smsapp;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class ContactListActivity extends AppCompatActivity {

    ArrayAdapter<String> arrayAdapterNames, arrayAdapterId, arrayAdapterDrafts;
    ListView contactList;

    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        mToolbar = (Toolbar) findViewById(R.id.toolbar_contactList);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mToolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("My Contacts");


        ContentResolver cr = getContentResolver();
        ArrayList<String> ident = new ArrayList<String>();
        ArrayList<String> names = new ArrayList<String>();
        arrayAdapterNames = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, names);
        //arrayAdapterId = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, id);
        contactList = (ListView) findViewById(R.id.ContactList);
        contactList.setAdapter(arrayAdapterNames);
        contactList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ContactListActivity.this, SMS.class);
                String nameAndID = arrayAdapterNames.getItem(position);
                intent.putExtra(ContactsContract.Intents.Insert.PHONE, getNumber(nameAndID));
                startActivity(intent);
            }
        });

        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                ident.add(id);
                String name = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                name = name + " : " + id;
                names.add(name);
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_contact_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public String getNumber(String nameAndID){
        ContentResolver cr = getContentResolver();
        StringTokenizer token = new StringTokenizer(nameAndID);
        token.nextToken(":");
        String query = "_ID=" + token.nextToken(":");
        Cursor curse = cr.query(Uri.parse("content://sms/"), null, query, null,null);
        try {
            if (curse.moveToFirst()) {
                return curse.getString(curse.getColumnIndexOrThrow("address"));
            }
        }catch(Exception e){

        }
        return "Error";

    }

}
