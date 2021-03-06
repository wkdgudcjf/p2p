package com.ssm.util;

import com.ssm.androidcontroller.MainActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;


public class SMSReceiver extends BroadcastReceiver {
    static final String logTag = "SmsReceiver";
    static final String ACTION = "android.provider.Telephony.SMS_RECEIVED";
    @Override
    public void onReceive(Context context, Intent intent) {
           if (intent.getAction().equals(ACTION)) {
                   //Bundel 澄 眉农
                   Bundle bundle = intent.getExtras();
                   if (bundle == null) {
                          return;
                   }

                   //pdu 按眉 澄 眉农
                   Object[] pdusObj = (Object[]) bundle.get("pdus");
                   if (pdusObj == null) {
                          return;
                   }

                   //message 贸府
                   SmsMessage[] smsMessages = new SmsMessage[pdusObj.length];
                   for (int i = 0; i < pdusObj.length; i++) {
                          smsMessages[i] = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                          
                          /*     Log.e(logTag, "NEW SMS " + i + "th");
                          Log.e(logTag, "DisplayOriginatingAddress : "
                                         + smsMessages[i].getDisplayOriginatingAddress());
                          Log.e(logTag, "DisplayMessageBody : "
                                         + smsMessages[i].getDisplayMessageBody());
                          Log.e(logTag, "EmailBody : "
                                         + smsMessages[i].getEmailBody());
                          Log.e(logTag, "EmailFrom : "
                                         + smsMessages[i].getEmailFrom());
                          Log.e(logTag, "OriginatingAddress : "
                                         + smsMessages[i].getOriginatingAddress());
                          Log.e(logTag, "MessageBody : "
                                         + smsMessages[i].getMessageBody());
                          Log.e(logTag, "ServiceCenterAddress : "
                                         + smsMessages[i].getServiceCenterAddress());
                          Log.e(logTag, "TimestampMillis : "
                                         + smsMessages[i].getTimestampMillis());
                          */
                          
                          MainActivity.etConnCode.setText(smsMessages[i].getDisplayMessageBody().subSequence(16, smsMessages[i].getDisplayMessageBody().length()-2));
                          
                          Toast.makeText(context, smsMessages[i].getMessageBody(), 0).show();
                   }
           }
    }
}
