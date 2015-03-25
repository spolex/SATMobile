package das.practica5.spolex.clientsManager.clientes;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import das.practica5.spolex.clientsManager.clientes.adapters.ClienteBdAdapter;

public class Cliente 
{
	private Context contexto;
	
	private Long id;
	private String nombre;
    private String apellidos;
    private int telefono;
    private String direccion;
    private String empresa;
    private String fechaAlta;
    private String email;
    private String imagen;
    private Double latitud;
    private Double longitud;

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public String getFechaAlta() {
        return fechaAlta;
    }

    public void setFechaAlta(String fechaAlta) {
        this.fechaAlta = fechaAlta;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

	public Cliente(Context pContext)
	{
		this.contexto=pContext;
	}

	public Context getContexto() {
		return contexto;
	}

	public void setContexto(Context contexto) {
		this.contexto = contexto;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getTelefono() {
		return telefono;
	}

	public void setTelefono(int telefono) {
		this.telefono = telefono;
	}

	public String getApellidos() {
		return apellidos;
	}

	public static Cliente find(Context context, long id)
	{
		ClienteBdAdapter dbAdapter = new ClienteBdAdapter(context);

		Cursor c = dbAdapter.getRegistro(id);

		Cliente cliente = Cliente.cursorToCliente(context, c);

		c.close();

		return cliente;
	}
	 
	public static Cliente cursorToCliente(Context context, Cursor c)
    {
        Cliente cliente = null;
 
        if (c != null)
        {
            cliente = new Cliente(context);
 
            cliente.setId(c.getLong(c.getColumnIndex(ClienteBdAdapter.C_ID)));
            cliente.setNombre(c.getString(c.getColumnIndex(ClienteBdAdapter.C_NOMBRE)));
            cliente.setEmail(c.getString(c.getColumnIndex(ClienteBdAdapter.C_EMAIL)));
            cliente.setTelefono(c.getInt(c.getColumnIndex(ClienteBdAdapter.C_TELEFONO)));
            cliente.setApellidos(c.getString(c.getColumnIndex(ClienteBdAdapter.C_APELLIDOS)));
            cliente.setDireccion(c.getString(c.getColumnIndex(ClienteBdAdapter.C_DIRECCION)));
            cliente.setEmpresa(c.getString(c.getColumnIndex(ClienteBdAdapter.C_EMPRESA)));
            cliente.setImagen(c.getString(c.getColumnIndex(ClienteBdAdapter.C_PAT_IMG)));
            cliente.setFechaAlta(c.getString(c.getColumnIndex(ClienteBdAdapter.C_FECHA_ALTA)));
        }
 
        return cliente ;
    }

    private ContentValues toContentValues()
    {
        ContentValues reg = new ContentValues();

        if(this.getNombre()!=null)reg.put(ClienteBdAdapter.C_NOMBRE, this.getNombre());
        if(this.getEmail()!=null)reg.put(ClienteBdAdapter.C_EMAIL, this.getEmail());
        reg.put(ClienteBdAdapter.C_TELEFONO, this.getTelefono());
        if(this.getApellidos()!=null)reg.put(ClienteBdAdapter.C_APELLIDOS, this.getApellidos());
        if(this.getDireccion()!=null)reg.put(ClienteBdAdapter.C_DIRECCION,this.getDireccion());
        if(this.getEmpresa()!=null)reg.put(ClienteBdAdapter.C_EMPRESA,this.getEmpresa());
        if(this.getFechaAlta()!=null)reg.put(ClienteBdAdapter.C_FECHA_ALTA, String.valueOf(this.getFechaAlta()));
        if(this.getImagen()!=null)reg.put(ClienteBdAdapter.C_PAT_IMG,this.getImagen());
        reg.put(ClienteBdAdapter.C_ID,this.getId());

        return reg;
    }
	public long save()
    {
        ClienteBdAdapter dbAdapter = new ClienteBdAdapter(this.getContexto());
 
        // comprobamos si estamos insertando o actualizando según esté o no relleno el identificador
        if ((this.getId() == null) || (!dbAdapter.exists(this.getId())))
        {
            long nuevoId = dbAdapter.insert(this.toContentValues());
 
            if (nuevoId != -1)
            {
                this.setId(nuevoId);
            }
        }
        else
        {
            return dbAdapter.update(this.toContentValues());
        }
        return this.getId();
    }
	
	public long delete()
    {
        // borramos el registro
        ClienteBdAdapter dbAdapter = new ClienteBdAdapter(this.getContexto());

        long rdo = dbAdapter.delete(this.getId());
        dbAdapter.cerrar();
 
        return rdo;
    }



}
