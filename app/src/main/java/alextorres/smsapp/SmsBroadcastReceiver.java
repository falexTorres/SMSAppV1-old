package alextorres.smsapp;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
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
                        cr.insert(Uri.parse("content://sms/"), values);

                        if(SMS.auto_reply_status == true){
                            try {
                                SmsManager smsManager = SmsManager.getDefault();
                                smsManager.sendTextMessage(address, null, SMS.auto_reply_message, null, null);
                                ContentValues sentValue = new ContentValues();
                                values.put("address", address);
                                values.put("body", SMS.auto_reply_message);
                                // Date of the draft message.
                                values.put("date_sent", String.valueOf(System.currentTimeMillis()));
                                values.put("type", "2");
                                // Put the actual thread id here. 0 if there is no thread yet.
                                values.put("thread_id", "0");
                                cr.insert(Uri.parse("content://sms/"), sentValue);
                            } catch (Exception ex) {
                                System.out.println("Error sending autoreply message: " + ex);
                            }
                        }

                    }
                    Toast.makeText(context, smsMessageStr, Toast.LENGTH_SHORT).show();


                }
            }catch(Exception e){
                System.out.println("Exception in smsBroadcastReceiver" +e);
            }

    }
}