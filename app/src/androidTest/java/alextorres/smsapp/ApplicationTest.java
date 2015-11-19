package alextorres.smsapp;

import android.app.Application;
import android.telephony.SmsManager;
import android.test.ApplicationTestCase;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }

    public void sendSMSshouldSendtoPhoneNumberPassed() {
        SMS tester = new SMS();
        ConversationDisplayActivity tester1 = new ConversationDisplayActivity();
        SmsManager smsManager = SmsManager.getDefault();


        assertNotSame("should be of different classes", tester, smsManager);
        assertEquals("'isMe' method should return TRUE", true, tester1.isMe("5554"));


    }


}