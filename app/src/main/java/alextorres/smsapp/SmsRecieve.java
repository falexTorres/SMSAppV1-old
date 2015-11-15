package alextorres.smsapp;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsMessage;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class SmsRecieve extends AppCompatActivity implements AdapterView.OnItemClickListener {

    public class SmsBroadcastReceiver extends BroadcastReceiver {

        public static final String SMS_BUNDLE = "pdus";

        public void onReceive(Context context, Intent intent) {
            Bundle intentExtras = intent.getExtras();
            if (intentExtras != null) {
                Object[] sms = (Object[]) intentExtras.get(SMS_BUNDLE);
                String smsMessageStr = "";
                for (int i = 0; i < sms.length; ++i)
                {
                    SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sms[i]);

                    String smsBody = smsMessage.getMessageBody().toString();
                    String address = smsMessage.getOriginatingAddress();

                    smsMessageStr += "SMS From: " + address + "\n";
                    smsMessageStr += smsBody + "\n";
                }
                Toast.makeText(context, smsMessageStr, Toast.LENGTH_SHORT).show();

                //this will update the UI with message
                SmsRecieve inst = SmsRecieve.instance();
                inst.updateList(smsMessageStr);
            }
        }
    }

    String[] thread_id, snippet,conversationCount, phoneNumbers;
    ArrayList<String> name;
    private static SmsRecieve inst;
    ArrayList<String> smsMessagesList = new ArrayList<String>();
    ListView smsListView;
    ArrayAdapter arrayAdapter;


    public static SmsRecieve instance() {
        return inst;
    }

    @Override
    public void onStart() {
        super.onStart();
        inst = this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_recieve);
        smsListView = (ListView) findViewById(R.id.SMSList);
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, smsMessagesList);
        smsListView.setAdapter(arrayAdapter);
        smsListView.setOnItemClickListener(this);

        refreshSmsInbox();
        refreshDraftsBox();
    }

    private void refreshDraftsBox() {
        ContentResolver contentResolver = getContentResolver();
        Cursor draftsCursor = contentResolver.query(Uri.parse("content://sms/draft"), null, null, null, null);
        int indexBody = draftsCursor.getColumnIndex("body");
        int indexAddress = draftsCursor.getColumnIndex("address");
        if (indexBody < 0 || !draftsCursor.moveToFirst()) return;
        arrayAdapter.clear();
        do {
            String str = "Draft Saved: " + draftsCursor.getString(indexAddress) +
                    "\n" + draftsCursor.getString(indexBody) + "\n";
            arrayAdapter.add(str);
        } while (draftsCursor.moveToNext());
    }

    private void refreshSmsInbox() {
        ContentResolver contentResolver = getContentResolver();
        Cursor smsInboxCursor = contentResolver.query(Uri.parse("content://sms/conversations"), null, null, null, null);
        String tempName = null;
        conversationCount = new String[smsInboxCursor.getCount()];
        snippet = new String[smsInboxCursor.getCount()];
        thread_id = new String[smsInboxCursor.getCount()];


        //get conversations from the database along with the count of corresponding messages and the thread id for
        //all of the messages
        smsInboxCursor.moveToFirst();
        for(int i = 0; i < smsInboxCursor.getCount(); i++){
            conversationCount[i] = smsInboxCursor.getString(smsInboxCursor.getColumnIndexOrThrow(("msg_count"))).toString();

            snippet[i] = smsInboxCursor.getString(smsInboxCursor.getColumnIndexOrThrow(("snippet"))).toString();

            thread_id[i] = smsInboxCursor.getString(smsInboxCursor.getColumnIndexOrThrow(("thread_id"))).toString();

            tempName = getName(getApplicationContext(),thread_id[i]);
            if(tempName != null){
                arrayAdapter.add(tempName + " : " + snippet[i]);
            }else {
                arrayAdapter.add(thread_id[i] + " : " + snippet[i]);
            }
            smsInboxCursor.moveToNext();
        }
        
        smsInboxCursor.close();

    }

    public String getName(Context context, String thread_id){
        ContentResolver contentResolver = context.getContentResolver();
        String query = "thread_id=" + thread_id;
        Cursor conversationMessageCursor = contentResolver.query(Uri.parse("content://sms/"), null, query, null, null);
        String nameToShow, number;

        if(conversationMessageCursor.moveToFirst()){
            number = conversationMessageCursor.getString(conversationMessageCursor.getColumnIndexOrThrow("address")).toString();
            nameToShow = getContactName(context.getApplicationContext(), number);
            conversationMessageCursor.close();
            if(nameToShow != null){
                return nameToShow;
            }else{
                return number;
            }
        }else{
            conversationMessageCursor.close();
            return null;
        }

    }

    public String getContactName(Context context, String phoneNumber) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(phoneNumber));
        Cursor cursor = cr.query(uri,
                new String[] { ContactsContract.PhoneLookup.DISPLAY_NAME }, null, null, null);
        if (cursor == null) {
            return null;
        }
        String contactName = null;
        if (cursor.moveToFirst()) {
            contactName = cursor.getString(cursor
                    .getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return contactName;
    }

    public void updateList(final String smsMessage) {
        arrayAdapter.insert(smsMessage, 0);
        arrayAdapter.notifyDataSetChanged();
    }

    public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
        try {
            String threadHolder = thread_id[pos];
            Intent intent = new Intent(this, ConversationDisplayActivity.class);
            intent.putExtra("THREAD_ID", threadHolder);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
