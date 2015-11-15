package alextorres.smsapp;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.StringTokenizer;

public class ConversationDisplayActivity extends AppCompatActivity {

    ArrayAdapter<String> arrayAdapterMessages, arrayAdapterNames;
    ListView messageList, nameList;
    ArrayList<String> messages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String threadID = "99";
        setContentView(R.layout.activity_conversation_display);
        Bundle threadDetail = getIntent().getExtras();
        if (threadDetail != null) {
            threadID = threadDetail.getString("THREAD_ID");
        }
        messages = getMessages(threadID);
        Collections.reverse(messages);
        arrayAdapterMessages = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, messages);
        messageList = (ListView) findViewById(R.id.conversationMessageList);
        messageList.setAdapter(arrayAdapterMessages);
        messageList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CharSequence text = "Clicked on the item!";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(getBaseContext(), text, duration);
                toast.show();
            }
        });

        messageList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    String messageHolder = messages.get(position).toString();
                    String messageID;
                    StringTokenizer token = new StringTokenizer(messageHolder);
                    token.nextToken("::"); // skip name
                    token.nextToken("::"); // skipp sms message
                    messageID = token.nextToken("::"); //the message id we need to delete the message
                    Intent intent = new Intent(ConversationDisplayActivity.this, DeleteSmsActivity.class);
                    intent.putExtra("MESSAGE_ID", messageID);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }
        });
        ArrayList<String> names = getNames(threadID);
        Collections.reverse(names);

        combineArrays(messages, names);
      /*  arrayAdapterNames = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,names);
        nameList = (ListView) findViewById(R.id.namesOrNumbers);
        nameList.setAdapter(arrayAdapterNames);
        nameList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CharSequence text = "Clicked on the name!";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(getBaseContext(), text, duration);
                toast.show();
            }
        });
*/
    }

    private ArrayList<String> getMessages(String thread_id){
        ContentResolver cr = getContentResolver();
        ArrayList<String> messages = new ArrayList<String>();
        String query = "thread_id=" + thread_id;
        String messageHolder;
        Cursor conversationMessageCursor = cr.query(Uri.parse("content://sms/"), null, query, null, null);
        String messageID = "";

        if (conversationMessageCursor.getCount() > 0) {
            while (conversationMessageCursor.moveToNext()) {
                try{
                    messageID = conversationMessageCursor.getString(conversationMessageCursor.getColumnIndex("_ID"));
                    messageHolder = conversationMessageCursor.getString(conversationMessageCursor.getColumnIndexOrThrow("body")).toString();
                    messages.add(messageHolder + ":: " + messageID);

                }catch (Exception E){
                    System.out.println("Something went wrong: " + E);
                }
            }
        }

        conversationMessageCursor.close();

        return messages;
    }

    private ArrayList<String> getNames(String thread_id){
        ContentResolver cr = getContentResolver();
        ArrayList<String> messages = new ArrayList<String>();
        String query = "thread_id=" + thread_id;
        String name = null;
        String number = null;
        String person = null;
        String altName = null;
        String type = null;
        Cursor conversationMessageCursor = cr.query(Uri.parse("content://sms/"), null, query, null, null);

        if (conversationMessageCursor.getCount() > 0) {
            while (conversationMessageCursor.moveToNext()) {
                try{
                    number = conversationMessageCursor.getString(conversationMessageCursor.getColumnIndexOrThrow("address")).toString();
                    person = conversationMessageCursor.getString(conversationMessageCursor.getColumnIndexOrThrow("person"));
                    name = getContactName(getApplicationContext(), conversationMessageCursor.getString(conversationMessageCursor.getColumnIndexOrThrow("address")));
                    type = conversationMessageCursor.getString(conversationMessageCursor.getColumnIndexOrThrow("type"));

                if(type.equalsIgnoreCase("2")){
                    messages.add("Me");
                }else {
                    if (person != null) {
                        altName = getDisplayName(getApplicationContext(), person);
                        if (altName != null) {
                            messages.add(altName);
                        } else {
                            messages.add(name);
                        }
                    } else {

                        if (name == null) {
                            messages.add(number);
                        } else {
                            messages.add(name);
                        }
                    }
                }

                }catch (Exception E){
                    System.out.println("Something went wrong: " + E);
                }
            }
        }

            conversationMessageCursor.close();
        return messages;
    }

    public boolean isMe(String personID){

        //if(personID.equals())
        return true;
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

    public String getDisplayName(Context context, String id){
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        ContentResolver cr = context.getContentResolver();
        String query = "_ID="+id;
        Cursor name = cr.query(uri,null,query,null,null);

        //System.out.println("IS NAME NULL?: " + (name == null));

        if(cr == null)
            return null;
        String displayName = null;
        if(name.moveToFirst()){
            displayName = name.getString(name.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY));
           // System.out.println("DISPLAYNAME IS: " + displayName);
        }
        return displayName;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_conversation_display, menu);
        return true;
    }

    public void combineArrays(ArrayList<String> one, ArrayList<String> two){
        String message, names, combine;
        for(int i = 0; i < one.size(); i++){
           message = one.get(i).toString();
           names = two.get(i).toString();
            combine = names+ ":: " + message;
            one.set(i,combine);
        }

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
}
