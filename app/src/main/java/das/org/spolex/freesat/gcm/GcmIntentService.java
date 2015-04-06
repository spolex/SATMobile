package das.org.spolex.freesat.gcm;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import das.org.spolex.freesat.R;
import das.org.spolex.freesat.avisos.Aviso;
import das.org.spolex.freesat.main.LoginActivity;
import das.org.spolex.freesat.main.MainActivity;
import das.org.spolex.freesat.main.SATManagerActivity;


public class GcmIntentService extends IntentService {

    public static String LOG_TAG = GcmIntentService.class.getName();
    public static final int NOTIFICATION_ID=1;
    private NotificationManager mNotificationManager;
    private Aviso mAviso;
    private Context context;

    public GcmIntentService() {
        super("GcmIntentService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        //TODO handle database updates from server
        context = getApplicationContext();
            GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
            String messageType = gcm.getMessageType(intent);

            Bundle extras = intent.getExtras();
            if(!extras.isEmpty()) {
                if (GoogleCloudMessaging.
                        MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                    sendNotification("Send error: " + extras.toString());
                } else if (GoogleCloudMessaging.
                        MESSAGE_TYPE_DELETED.equals(messageType)) {
                    sendNotification("Deleted messages on server: " +
                            extras.toString());
                    // If it's a regular GCM message, do some work.
                } else if (GoogleCloudMessaging.
                        MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                    // the service doing some work......
                    mAviso = new Aviso(context,extras.getString("id_cliente")
                            ,extras.getString("fecha")+extras.getString("hora"),
                            extras.getString("urgente").equals("1"),false,
                            extras.getString("informe"),null, null, null);
                    mAviso.setmId(mAviso.save());                    // Post notification of received message.

                    sendNotification(getString(R.string.recibido) + extras.toString());

                    Log.i(LOG_TAG, "Received: " + extras.toString());
                }
            }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
            GcmBroadcastReceiver.completeWakefulIntent(intent);

    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String msg) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);
        //TODO conprobar si está logueado para redirigir a otra activity

        Intent i = new Intent(this, LoginActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
               i, PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_action_clients)
                        .setContentTitle("Asignación de avisos")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg)
                        .setDefaults(Notification.DEFAULT_ALL);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

}
