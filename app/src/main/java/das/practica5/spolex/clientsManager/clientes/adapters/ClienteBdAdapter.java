package das.practica5.spolex.clientsManager.clientes.adapters;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import das.practica5.spolex.clientsManager.clientes.Cliente;
import das.practica5.spolex.clientsManager.main.ddbb.BDHelper;
import das.practica5.spolex.clientsManager.main.ddbb.Contract;


/**
 * Gestión de las operaciones en la Base de datos
 * @author spolex
 *
 */
public class ClienteBdAdapter {
	/*
	 * Constante con el nombre de la tabla.
	 */
	public static final String TABLA = Contract.T_CLIENTE;

	/*
	 * Constantes con el nombre de las columnas
	 */
	public static final String C_ID = Contract.C_ID;
	public static final String C_NOMBRE = Contract.C_NOMBRE;
	public static final String C_EMAIL = Contract.C_EMAIL;
	public static final String C_TELEFONO = Contract.C_TELEFONO;
	public static final String C_APELLIDOS= Contract.C_APELLIDOS;
    public static final String C_DIRECCION= Contract.C_DIRECCION;
    public static final String C_LONG= Contract.C_LONGITUD;
    public static final String C_LAT= Contract.C_LATITUD;
    public static final String C_PAT_IMG= Contract.C_PATH_IMAGEN;
    public static final String C_EMPRESA= Contract.C_EMPRESA;
    public static final String C_FECHA_ALTA=Contract.C_ALTA;

	private Context contexto;
	private BDHelper bdHelper;
	private SQLiteDatabase bd;

	/*
	 * Definimos lista de columnas de la tabla para utilizarla en las consultas a la base de datos
	 */

	private String[] columnas = new String[]{ C_ID, C_NOMBRE, C_EMAIL, C_TELEFONO, C_APELLIDOS, C_DIRECCION, C_LONG, C_LAT, C_PAT_IMG, C_FECHA_ALTA, C_PAT_IMG, C_EMPRESA} ;

	public ClienteBdAdapter(Context pContexto){
		this.contexto=pContexto;
	}

	public ClienteBdAdapter abrir(){
		bdHelper = new BDHelper(contexto);
		bd = bdHelper.getWritableDatabase();
		return this;
	}
	public void cerrar()
	{
		bdHelper.close();
	}

	public boolean exists(Long id) {
		boolean exists ;

		if (bd == null || !bd.isOpen())
			abrir();

		Cursor c = bd.query( true, TABLA, columnas, C_ID + "=" + id, null, null, null, null, null);

		exists = (c.getCount() > 0);

		c.close();
        bd.close();

		return exists;
	}

	/**
	 * Devuelve cursor con todos las columnas del registro
	 */
	public Cursor getRegistro(long id) throws SQLException
	{
		if(bd==null || !bd.isOpen())abrir();
		Cursor c = bd.query( true, TABLA, columnas, C_ID + "=" + id, null, null, null, null, null);

		//Nos movemos al primer registro de la consulta
		if (c != null) {
			c.moveToFirst();
		}
        bd.close();
		return c;
	}
	/**
	 * Inserta los valores en un registro de la tabla
	 */
	public long insert(ContentValues reg)
	{
		if (bd == null || bd.isOpen())
			abrir();
        long rdo=bd.insert(TABLA, null, reg);

        bd.close();
		return rdo;
	}

	/**
	 * Eliminar el registro con el identificador indicado
	 */
	public long delete(long id)
	{
		if (bd == null || !bd.isOpen())
			abrir();
        long rdo=bd.delete(TABLA, "_id=" + id, null);
    bd.close();
		return rdo;
	}
	/**
	 * Modificar el registro
	 */
	public long update(ContentValues reg)
	{
		long result = 0;

		if (bd == null || !bd.isOpen()) {
            abrir();
        }

		if (reg.containsKey(C_ID))
		{
			//
			// Obtenemos el id y lo borramos de los valores
			//
			long id = reg.getAsLong(C_ID);

			reg.remove(C_ID);

			//
			// Actualizamos el registro con el identificador que hemos extraido 
			//
			result = bd.update(TABLA, reg, "_id=" + id, null); 
		}
        bd.close();
		return result;
	}

    /**
     *
     * @param filtro
     * @return arraylist con los clientes existentes en la BD
     */
	public ArrayList<Cliente> getClientes(String filtro)
    {
        ArrayList<Cliente> clientes = new ArrayList<Cliente>();
 
        if (bd == null || !bd.isOpen())
            abrir();
 
        Cursor c = bd.query(true, TABLA, columnas, filtro, null, null, null, null, null);
 
        for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext())
        {
            clientes.add(Cliente.cursorToCliente(contexto, c));
        }
 
        c.close();
        bd.close();
 
        return clientes;
    }

    /**
     *
     * @param pEmail
     * @return
     */
    public boolean exists(String pEmail) {
        boolean exists ;

        if (bd == null || !bd.isOpen())
            abrir();
        Cursor c = bd.query( true, TABLA, columnas, C_EMAIL + "='" + pEmail+"'", null, null, null, null, null);

        exists = (c.getCount() > 0);

        c.close();
        bd.close();
        return exists;
    }
}
