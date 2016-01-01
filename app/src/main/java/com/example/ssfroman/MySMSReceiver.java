package com.example.ssfroman;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.example.analysis.BayesianClassifier;
import com.example.dataprovider.BlockedSMSDataSource;
import com.example.dataprovider.ContactsDataSource;
import com.example.dataprovider.SpamSMSDataSource;
import com.example.model.BlockedContact;
import com.example.model.SMSLocal;

public class MySMSReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle pudsBundle = intent.getExtras();
		Object[] pdus = (Object[]) pudsBundle.get("pdus");
		SmsMessage messages = SmsMessage.createFromPdu((byte[]) pdus[0]);
		SMSLocal smsLocal = new SMSLocal();

		Log.d("privateLog", messages.getOriginatingAddress() + ","
				+ messages.getDisplayMessageBody() + ","
				+ messages.getDisplayOriginatingAddress() + ","
				+ messages.getMessageBody() + ","
				+ messages.getPseudoSubject() + ","
				+ messages.getOriginatingAddress() + ","
				+ messages.getServiceCenterAddress() + ","
				+ messages.getProtocolIdentifier() + ","
				+ messages.getStatus() + ","
				+ messages.getTimestampMillis());

		smsLocal.body = messages.getMessageBody();
		smsLocal.address = messages.getOriginatingAddress();
		smsLocal.service_center = messages.getServiceCenterAddress();
		smsLocal.protocol = messages.getProtocolIdentifier();
		smsLocal.status = messages.getStatus();
		smsLocal.date = messages.getTimestampMillis();
		smsLocal.subject = messages.getPseudoSubject();
		smsLocal.read = 0;
		smsLocal.seen = 0;

		//Log.d("privateLog",)
		boolean spamFilterStatus = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("spam_filter_status", true);
		boolean blockNumberStatus = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("block_number_status", true);

		boolean isBlocked = false;

		if (blockNumberStatus) {
			BlockedContact blockedContact = ContactsDataSource.getInstance(context).getContact(smsLocal.address);

			if (blockedContact != null) {
				BlockedSMSDataSource.getInstance(context).addBlockedSMS(smsLocal);
				Toast.makeText(context, "Blocked SMS!", Toast.LENGTH_SHORT).show();
				abortBroadcast();
				isBlocked = true;
			}
		} else {
			Toast.makeText(context, "Blocking Service is not running!", Toast.LENGTH_LONG).show();
		}

		if (spamFilterStatus && !isBlocked) {
			float spamacity = BayesianClassifier.classify(context, smsLocal);
			Toast.makeText(context, "Spamacity: " + spamacity, Toast.LENGTH_LONG).show();
			if (spamacity > 0.9) {
				SpamSMSDataSource.getInstance(context).addSpamSMS(smsLocal);
				Toast.makeText(context, "Spam SMS!", Toast.LENGTH_SHORT).show();
				abortBroadcast();
			}
		} else {
			if (!spamFilterStatus) {
				Toast.makeText(context, "Spamming Service is not running!", Toast.LENGTH_LONG).show();
			}
		}
		//context.getSharedPreferences("status_on_off", )

		/*StringTokenizer st = new StringTokenizer(messages.getMessageBody());
		String tStr = "Tokens:\n";
		while (st.hasMoreTokens()) {
			tStr += "[" + st.nextToken() + "]\n";
		}*/
	}
}


