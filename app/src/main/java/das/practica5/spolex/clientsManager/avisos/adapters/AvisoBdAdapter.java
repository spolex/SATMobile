package das.practica5.spolex.clientsManager.avisos.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import das.practica5.spolex.clientsManager.avisos.Aviso;
import das.practica5.spolex.clientsManager.main.ddbb.BDHelper;
import das.practica5.spolex.clientsManager.main.ddbb.Contract;


/**
 * Gestión de las operaciones en la Base de datos
 * @author spolex
 *
 */
public class AvisoBdAdapter {
	/*
	 * Constante con el nombre de la tabla.
	 */
	public static final String TABLA = Contract.T_AVISOS;

	/*
	 * Constantes con el nombre de las columnas
	 */
	public static final String C_ID = Contract.C_ID;
	public static final String C_DESTINO = Contract.C_DESTINO;
	public static final String C_FECHA_HORA = Contract.C_FECHA_HORA;
	public static final String C_URGENTE = Contract.C_URGENTE;
	public static final String C_REALIZADA = Contract.C_REALIZADA;
    public static final String C_PATH_INFORME = Contract.C_PATH_INFORME;
    public static final String C_LONG= Contract.C_LONGITUD;
    public static final String C_LAT= Contract.C_LATITUD;
    public static final String C_DIRECCION = Contract.C_DIRECCION;

    private Context contexto;
	private BDHelper bdHelper;
	private SQLiteDatabase bd;

	/*
	 * Definimos lista de columnas de la tabla para utilizarla en las consultas a la base de datos
	 */

	private String[] columnas = new String[]{ C_ID, C_DESTINO, C_FECHA_HORA, C_URGENTE, C_REALIZADA, C_PATH_INFORME, C_LONG, C_LAT, C_DIRECCION} ;

	public AvisoBdAdapter(Context pContexto){
		this.contexto=pContexto;
	}

	public AvisoBdAdapter abrir(){
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
	public ArrayList<Aviso> getAvisos(String filtro)
    {
        ArrayList<Aviso> avisos = new ArrayList<Aviso>();
 
        if (bd == null || !bd.isOpen())
            abrir();
 
        Cursor c = bd.query(true, TABLA, columnas, filtro, null, null, null, null, null);
 
        for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext())
        {
            avisos.add(Aviso.cursorToAviso(contexto, c));
        }
 
        c.close();
        bd.close();
 
        return avisos;
    }
}
