package das.org.spolex.freesat.widgets;

import android.app.LauncherActivity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;

import das.org.spolex.freesat.R;
import das.org.spolex.freesat.avisos.Aviso;
import das.org.spolex.freesat.avisos.adapters.AvisoBdAdapter;

/**
 * Created by spolex on 25/04/15.
 *
 * Adaptador para la clase Listview en un app widget
 */
public class ListProvider implements RemoteViewsService.RemoteViewsFactory {
    private ArrayList<Aviso> listAvisos = new ArrayList<Aviso>();
    private Context context = null;
    private int appWidgetId;
    private AvisoBdAdapter bdAdapter;

    public ListProvider(Context applicationContext, Intent intent) {
        this.context = applicationContext;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
        bdAdapter = new AvisoBdAdapter(applicationContext);
        
        cargarLista();
    }

    private void cargarLista() {
        //TODO los avisos para hoy
        listAvisos=bdAdapter.getAvisos(null);
    }
    @Override
    public int getCount() {
        return listAvisos.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        final RemoteViews remoteview = new RemoteViews(
                context.getPackageName(),
                R.layout.item_aviso_wid);
        Aviso aviso = listAvisos.get(position);
        remoteview.setTextViewText(R.id.item_avisos_wid_destino_id, aviso.getmDestino());
        remoteview.setTextViewText(R.id.item_avisos_wid_fecha_hora_id,aviso.getmFechaHora());
        return remoteview;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {

    }

    @Override
    public void onDestroy() {

    }

}
