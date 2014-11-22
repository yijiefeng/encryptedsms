package com.yfeng.lockedsms;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;


public class SmsBroadCastReceiver extends BroadcastReceiver {

	
	@Override
	public void onReceive(Context context, Intent intent) {

		Bundle bundle = intent.getExtras();

		// Specify the bundle to get object based on SMS protocol "pdus"
		Object[] object = (Object[]) bundle.get("pdus");
		SmsMessage sms[] = new SmsMessage[object.length];
		Intent in = new Intent(context, DisplayMessageActivity.class);
		in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		in.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		
		String msgContent = "";
		String originNum = "";
		long timestamp = 0;
		
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < object.length; i++) {

			sms[i] = SmsMessage.createFromPdu((byte[]) object[i]);

			// get the received SMS content
			msgContent = sms[i].getDisplayMessageBody();

			// get the sender phone number
			originNum = sms[i].getDisplayOriginatingAddress();

			//get the timestamp
			timestamp = sms[i].getTimestampMillis();
			
			// aggregate the messages together when long message are fragmented
			sb.append(msgContent);

			// abort broadcast to cellphone inbox
			abortBroadcast();

		}

		// fill the sender's phone number into Intent
		in.putExtra("originNum", originNum);

		// fill the entire message body into Intent
		in.putExtra("msgContent", new String(sb));

		in.putExtra("msgTimestamp", timestamp);
		
		// start the DisplaySMSActivity.java
		//context.startActivity(in);
		
		// display a clickable notification
		createNotification(context, in, originNum);
		
		
	}
	
	private void createNotification(Context context, Intent in, String from) {

		// Prepare intent which is triggered if the notification is selected
		PendingIntent pIntent = PendingIntent.getActivity(context, 0, in, 0);

		// Build the notification with example Actions
		Notification noti = new Notification.Builder(context)
				.setContentTitle("New encrypted message")
				.setContentText("From: " + from).setSmallIcon(R.drawable.ic_launcher)
				.setContentIntent(pIntent).build();

		// get the notificationManager
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

		// hide the notification after it is selected; add the cancel flag to
		// the NotificationManager object
		noti.flags = noti.flags | Notification.FLAG_AUTO_CANCEL;

		notificationManager.notify(0, noti);
	}
	

}




