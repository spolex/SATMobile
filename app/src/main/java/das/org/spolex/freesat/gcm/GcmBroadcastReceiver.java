package das.org.spolex.freesat.gcm;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {
    public GcmBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        //Especificación explicita del Intentservice que manejará el intent
        ComponentName comp = new ComponentName(context.getPackageName(),
                GcmIntentService.class.getName());

        //Empieza el servicio, mantiene el dispositivo "despierto" mientras está en ejecución
        startWakefulService(context,(intent.setComponent(comp)));

        setResultCode(Activity.RESULT_OK);
    }
}
