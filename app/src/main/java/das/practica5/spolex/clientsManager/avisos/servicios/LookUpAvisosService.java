package das.practica5.spolex.clientsManager.avisos.servicios;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.IBinder;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import das.practica5.spolex.clientsManager.R;
import das.practica5.spolex.clientsManager.avisos.Aviso;
import das.practica5.spolex.clientsManager.avisos.adapters.AvisoBdAdapter;

public class LookUpAvisosService extends Service {

    private static final String LOG_TAG = LookUpAvisosService.class.getSimpleName();
    private AvisoBdAdapter bdAdapterAvisos;

    public LookUpAvisosService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        bdAdapterAvisos = new AvisoBdAdapter(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Boolean urgentes = false;
        for(Aviso aviso:bdAdapterAvisos.getAvisos(null)){

            String[] fechahora = aviso.getmFechaHora().split(" ");
            String fecha = fechahora[0];
            Log.i(LOG_TAG, fecha);
            if (aviso.getmUrgente() && !aviso.getmRealizada() && isToday(fecha)){
                urgentes=true;
            }
        }

        if(urgentes){
            Notification.Builder elconstructor=
                    new Notification.Builder(this)
                            .setSmallIcon(android.R.drawable.stat_sys_warning)
                            .setLargeIcon((((BitmapDrawable)getResources()
                                    .getDrawable(android.R.drawable.ic_dialog_alert)).getBitmap()))
                            .setContentTitle(getString(R.string.avisos_pend_notificacion_titulo))
                            .setContentText(getString(R.string.msg_avisos_pend_not))
                            .setTicker(getString(R.string.ticker_not_avis_urg_pend))
                            .setDefaults(Notification.DEFAULT_ALL);
            NotificationManager elnotificador = (NotificationManager)getSystemService
                    (getApplicationContext().NOTIFICATION_SERVICE);
            elnotificador.notify(1, elconstructor.build());
        }
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public boolean isToday(String fecha){
        Calendar cal = Calendar.getInstance();
        Date sysDate = cal.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-M-yyyy");
        String hoy = formatter.format(sysDate);
        return hoy.equals(fecha);
    }
}
