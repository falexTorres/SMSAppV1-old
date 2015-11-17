package alextorres.smsapp;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;

import android.content.BroadcastReceiver;

import android.content.Context;
import android.content.Intent;
import android.content.ContentResolver;
import android.telephony.SmsMessage;

import android.widget.Toast;

public class SmsBroadcastReceiver extends BroadcastReceiver {

        public void onReceive(Context context, Intent intent) {
            Bundle intentExtras = intent.getExtras();

            String smsMessageStr = "";

            try {
                if (intentExtras != null) {
                    Object[] sms = (Object[]) intentExtras.get("pdus");
                    for (int i = 0; i < sms.length; ++i) {
                        SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sms[i]);
                        String smsBody = smsMessage.getMessageBody().toString();
                        String address = smsMessage.getOriginatingAddress();

                        smsMessageStr += "SMS From: " + address + "\n";
                        smsMessageStr += smsBody + "\n";

                        ContentResolver cr = context.getContentResolver();
                        ContentValues values = new ContentValues();
                        values.put("address", address);
                        values.put("body", smsBody);
                        values.put("date", String.valueOf(System.currentTimeMillis()));
                        values.put("type", "1");
                        //values.put("thread_id", "0");
                        cr.insert(Uri.parse("content://sms/"), values);
                    }
                    Toast.makeText(context, smsMessageStr, Toast.LENGTH_SHORT).show();

                    //this will update the UI with message
                    // SmsRecieve inst = SmsRecieve.instance();
                    // inst.updateList(smsMessageStr);
                }
            }catch(Exception e){
                System.out.println("Exception in smsBroadcastReceiver" +e);
            }

    }
}