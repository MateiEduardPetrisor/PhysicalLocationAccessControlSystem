package com.example.user.l_1_1;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.telephony.SmsMessage;
import android.text.format.DateFormat;
import android.util.Log;

import static android.content.Context.NOTIFICATION_SERVICE;

public class IncomingSms extends BroadcastReceiver {
    @SuppressWarnings("deprecation")
    @Override
    public void onReceive(Context context, Intent intent) {
        final Bundle bundle = intent.getExtras();
        String senderNumber = null;
        String messageText = null;
        try {
            if (bundle != null) {
                final Object[] pduObject = (Object[]) bundle.get("pdus");
                for (int i = 0; i < pduObject.length; i++) {
                    SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pduObject[i]);
                    senderNumber = smsMessage.getDisplayOriginatingAddress();
                    if (senderNumber.equals("+40728499188")) {
                        Long dateTimeInMillis = smsMessage.getTimestampMillis();
                        String timeStamp = (String) DateFormat.format("EEEE,MMMM,dd,yyyy,h,mm,ss,aa", dateTimeInMillis);
                        messageText = smsMessage.getDisplayMessageBody();
                        Message message = new Message(messageText, timeStamp);
                        addMessageToDatabase(context, message);
                        String messageNotification;
                        String valori[] = messageText.split(",");
                        messageNotification = "Zona" + " " + Integer.parseInt(valori[0]) + " " + valori[1];
                        createNotification(context, "Alarm", messageNotification);
                    }
                }
            }
        } catch (Exception e) {
            Log.wtf("SmsReceiver ", " Exception " + e);
        }
    }

    public void createNotification(Context context, String title, String message) {
        NotificationCompat.Builder mBuilder =
                new Builder(context)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(message);

        Intent resultIntent = new Intent(context, HousePlanActivity.class);
        resultIntent.putExtra("itemData", message);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);

        int mNotificationId = 999;
        NotificationManager mNotifyMgr =
                (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        Notification notification = mBuilder.build();
        notification.defaults |= Notification.DEFAULT_SOUND;
        mNotifyMgr.notify(mNotificationId, notification);
    }

    void addMessageToDatabase(Context context, Message messageToParse) throws Exception {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        databaseHelper.insertEvent(messageToParse);
    }
}