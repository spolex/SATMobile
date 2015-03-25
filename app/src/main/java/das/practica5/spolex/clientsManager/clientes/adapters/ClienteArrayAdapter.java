package das.practica5.spolex.clientsManager.clientes.adapters;

import java.util.ArrayList;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import das.practica5.spolex.clientsManager.R;
import das.practica5.spolex.clientsManager.clientes.Cliente;

public class ClienteArrayAdapter extends ArrayAdapter<Cliente> {
	
	public static class ViewHolder {
		TextView nombre_cliente;
        TextView email_cliente;
        ImageView img_cliente;
	}
	
	public ClienteArrayAdapter(Context pContext,ArrayList<Cliente> clientes){
		super(pContext, android.R.layout.simple_dropdown_item_1line, clientes);
	}
	
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
 
        Cliente cliente = getItem(position);
 
        ViewHolder viewHolder;
 
        if (convertView == null)
        {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_cliente, parent, false);
            viewHolder.nombre_cliente = (TextView) convertView.findViewById(R.id.item_cliente_nombre_id);
            viewHolder.email_cliente = (TextView)convertView.findViewById(R.id.item_cliente_email_id);
            viewHolder.img_cliente = (ImageView)convertView.findViewById(R.id.item_cliente_img_id);
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.nombre_cliente.setText(cliente.getNombre());
        viewHolder.email_cliente.setText(cliente.getEmail());

        String path = cliente.getImagen();

        if(path!= null) {
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            //para ajustarlo al imageviewer
            bitmap = Bitmap.createScaledBitmap(bitmap, 400, 400, false);
            viewHolder.img_cliente.setImageBitmap(bitmap);
        }



 
        return convertView;
    }
	
	@Override
	public long getItemId(int position)
	{
		return getItem(position).getId();
	}

}
