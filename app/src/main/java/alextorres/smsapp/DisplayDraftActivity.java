package alextorres.smsapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.content.ContentResolver;
import java.util.ArrayList;


import android.database.Cursor;
import android.content.Context;

public class DisplayDraftActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    ArrayList<String> draftMessages = new ArrayList<String>();
    ArrayList<String> draftNumbers = new ArrayList<String>();
    ArrayList<String> draftIDs = new ArrayList<String>();
    ListView draftListView;
    ArrayAdapter adapterForDraft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_draft);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
/*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */
        draftListView = (ListView) findViewById(R.id.SMSDraftList2);
        adapterForDraft = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, draftMessages);
        draftListView.setAdapter(adapterForDraft);
        draftListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    String message = draftMessages.get(position).toString();
                    String messageID = draftIDs.get(position).toString();
                    String person = draftNumbers.get(position).toString();
                    Intent intent = new Intent(DisplayDraftActivity.this, EditDraftActivity.class);
                    intent.putExtra("MESSAGE_ID", messageID);
                    intent.putExtra("NUMBER", person);
                    intent.putExtra("MESSAGE", message);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }
        });
        getDraftMessages(getApplicationContext());
    }


    public void getDraftMessages(Context context){
        ContentResolver content = context.getContentResolver();
        String body, number, id;
        Cursor curse = content.query(Uri.parse("content://sms/draft"),null,null,null,null);
        if(curse.getCount() > 0){
            while(curse.moveToNext()) {
                try {
                    number = curse.getString(curse.getColumnIndex("address"));
                    body = curse.getString(curse.getColumnIndexOrThrow("body"));
                    id = curse.getString(curse.getColumnIndexOrThrow("_id"));
                    draftIDs.add(id);
                    draftNumbers.add(number);
                    body = number + ": " + body;
                    draftMessages.add(body);
                } catch (Exception e) {
                    System.out.println("ISSUE WITH DRAFTS!");
                }
            }
            curse.close();

        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
