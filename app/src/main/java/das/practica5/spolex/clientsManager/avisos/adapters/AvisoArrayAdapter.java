package das.practica5.spolex.clientsManager.avisos.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

import das.practica5.spolex.clientsManager.R;
import das.practica5.spolex.clientsManager.avisos.Aviso;

public class AvisoArrayAdapter extends ArrayAdapter<Aviso> {
	
	public static class ViewHolder {
		TextView destino;
        TextView fecha;
        TextView latitud;
        TextView longitud;
        CheckBox urgente;
        CheckBox realizada;
	}
	
	public AvisoArrayAdapter(Context pContext, ArrayList<Aviso> avisos){
		super(pContext, android.R.layout.simple_dropdown_item_1line, avisos);
	}
	
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
 
        Aviso aviso = getItem(position);
 
        ViewHolder viewHolder;
 
        if (convertView == null)
        {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_aviso, parent, false);
            viewHolder.destino = (TextView) convertView.findViewById(R.id.item_avisos_destino_id);
            viewHolder.fecha = (TextView)convertView.findViewById(R.id.item_avisos_fecha_hora_id);
            viewHolder.urgente = (CheckBox)convertView.findViewById(R.id.item_avisos_urg_id);
            viewHolder.realizada = (CheckBox)convertView.findViewById(R.id.item_avisos_realizada_id);
            viewHolder.latitud = (TextView)convertView.findViewById(R.id.item_avisos_lat_id);
            viewHolder.longitud = (TextView)convertView.findViewById(R.id.item_avisos_long_id);
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.destino.setText(aviso.getmDestino());
        viewHolder.fecha.setText(aviso.getmFechaHora());
        viewHolder.latitud.setText(String.valueOf(aviso.getLatitud()));
        viewHolder.longitud.setText(String.valueOf(aviso.getLongitud()));

        //String path = cliente.getImagen();

        viewHolder.realizada.setChecked(aviso.getmRealizada());
        viewHolder.realizada.setFocusable(false);
        viewHolder.realizada.setEnabled(false);
        viewHolder.urgente.setChecked(aviso.getmUrgente());
        viewHolder.urgente.setFocusable(false);
 
        return convertView;
    }

	@Override
	public long getItemId(int position)
	{
		return getItem(position).getmId();
	}

}
