package das.practica5.spolex.clientsManager.main;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import java.util.Calendar;

import das.practica5.spolex.clientsManager.R;
import das.practica5.spolex.clientsManager.avisos.servicios.LookUpAvisosService;
import das.practica5.spolex.clientsManager.clientes.ClientsActivity;

public class MainActivity extends ActionBarActivity implements View.OnClickListener {


    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private ImageButton btn_manage;
    private ImageButton btn_clients;

    // Cada día se comprueba si quedan avisos pendientes urgentes hoy;
    //private long REPEAT_TIME = 1000 * 60 * 60 * 24;//milisegundos
    private long REPEAT_TIME = 1000 * 60;
    private boolean checkurgs = true;

    public final String CHECK_AVISOS = "activar_avisos_urg";
    public final String CHECK_FREQ = "avisos_urg_freq";
    private SharedPreferences.OnSharedPreferenceChangeListener prefListener;
    //listener on changed sort order preference:
    SharedPreferences prefs;





    @Override
    public boolean onKeyShortcut(int keyCode, KeyEvent event) {
        return super.onKeyShortcut(keyCode, event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //New toolbar
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.main_toolbar);
        toolbar.setBackgroundColor(Color.parseColor("#4585f2"));
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        btn_manage = (ImageButton) findViewById(R.id.main_manage_id);
        btn_manage.setOnClickListener(this);

        btn_clients =(ImageButton)findViewById(R.id.main_clients_id);
        btn_clients.setOnClickListener(this);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        prefListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {

                Log.i(LOG_TAG,"Settings key changed: " + key);
                if(key.equals("activar_avisos_urg") || key.equals("activar_avisos_freq")){
                    checkurgs=prefs.getBoolean(key,checkurgs);
                    REPEAT_TIME=  REPEAT_TIME * Integer.valueOf(prefs.getString(CHECK_FREQ, "1"));
                    findAvisosUrgs();
                }
                    //getLoaderManager().restartLoader(LOADER_ID, null, tCallbacks);


            }
        };
        prefs.registerOnSharedPreferenceChangeListener(prefListener);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent i = new Intent(this,SettingsActivity.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){

            case R.id.main_manage_id:
                Intent i = new Intent(this,SATManagerActivity.class);
                startActivity(i);
                break;
            case R.id.main_clients_id:
                 i = new Intent(this, ClientsActivity.class);
                startActivity(i);
                break;
        }
    }

    public void findAvisosUrgs(){
        // Ejecución programada del servicio que comprueba si hay avisos urgentes pendientes
        Calendar cal = Calendar.getInstance();

        AlarmManager alarm = (AlarmManager)getSystemService(getApplicationContext().ALARM_SERVICE);
        Intent i = new Intent(this, LookUpAvisosService.class);
        //startService(i);
        PendingIntent pending = PendingIntent.getService(getApplicationContext(), 0, i, 0);
        if(checkurgs) {
            alarm.setRepeating(
                    alarm.RTC_WAKEUP,
                    System.currentTimeMillis(),
                    REPEAT_TIME,
                    pending
            );
            Log.i(LOG_TAG, "Servicio programado");
        }
        else {
            alarm.cancel(pending);
            Log.i(LOG_TAG, "Servicio cancelado");
        }
    }



    @Override
    protected void onResume() {
        super.onResume();
        getPreferences(MODE_WORLD_WRITEABLE).registerOnSharedPreferenceChangeListener(prefListener);
        //Se obtienen de las preferencias de usuario si quiere comprobar si existen avisos urgentes
        //sin realizar en la fecha actual y con que frecuencia en días haciendo uso del servicio
        // LookUpAvisosService.
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);

        checkurgs = pref.getBoolean(CHECK_AVISOS,true);
        if(checkurgs) {
            REPEAT_TIME = REPEAT_TIME * Integer.valueOf(prefs.getString(CHECK_FREQ, "1"));
            findAvisosUrgs();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        getPreferences(MODE_WORLD_WRITEABLE).unregisterOnSharedPreferenceChangeListener(prefListener);

    }

}
