package das.practica5.spolex.clientsManager.main.ddbb;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class BDHelper extends SQLiteOpenHelper{
	
	private static int bd_version = Contract.BD_VERSION;
	private static String bd_nombre = Contract.BD_NOMBRE;

	private static CursorFactory factory = null;
	//private static String t_usuario = Contract.T_USUARIO;
	private static String t_clientes = Contract.T_CLIENTE;


	public BDHelper(Context context) {
		super(context, bd_nombre, factory, bd_version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {	
		Log.i(this.getClass().toString(), "Creando base de datos");

        ContentValues values=new ContentValues();

	/*	db.execSQL( "CREATE TABLE "+t_usuario+"(" +
		          " _id INTEGER PRIMARY KEY AUTOINCREMENT," +
		          " nombre TEXT NOT NULL, " +
		          " pass TEXT NOT NULL, " +
		          " descripcion TEXT," +
		          " admin BIT," +
		          " codigo TEXT," +
		          " observaciones TEXT)" );

		db.execSQL( "CREATE UNIQUE INDEX nombre ON "+t_usuario+"(nombre ASC)" );

		Log.i(this.getClass().toString(), "Tabla USUARIO creada");*/

		/*
		 * Insertamos datos iniciales
		 *
		String nullColumnHack="codigo,admin,nombre,pass";

		values.put(Contract.C_CODIGO, 3);
		values.put(Contract.C_ADMIN, false);
		values.put(Contract.C_NOMBRE, "usuario");
		values.put(Contract.C_PASS, 123);
		values.put(Contract.C_OBSERVACIONES, 123);
		db.insert(t_usuario, nullColumnHack, values);
		db.execSQL("INSERT INTO USUARIO(codigo,admin,nombre,pass) VALUES('1','true','admin','patata')");
		db.execSQL("INSERT INTO USUARIO (codigo,admin,nombre,pass) VALUES('2','false','usuario1','pataton')");
		

		Log.i(this.getClass().toString(), "Datos iniciales USUARIO insertados");*/


		

        String table="CREATE TABLE "+t_clientes+" (_id INTEGER PRIMARY KEY AUTOINCREMENT  NOT NULL ,"+
                Contract.C_APELLIDOS+" VARCHAR, "+Contract.C_TELEFONO+" INTEGER NOT NULL ,"+
                Contract.C_DIRECCION+" VARCHAR NOT NULL , "+
                Contract.C_NOMBRE+" VARCHAR NOT NULL, "+
                Contract.C_EMPRESA+" VARCHAR, "+
                Contract.C_EMAIL+" VARCHAR, "+
                Contract.C_ALTA+" STRING NOT NULL DEFAULT CURRENT_DATE, "+
                Contract.C_LATITUD+" DOUBLE, "+
                Contract.C_LONGITUD+" DOUBLE, "+
                Contract.C_PATH_IMAGEN+" VARCHAR)";

        db.execSQL(table);
        Log.i(this.getClass().toString(), "Tabla clientes creada...");
        //Para reducir el tiempo de consulta sobre el campo email
		db.execSQL( "CREATE UNIQUE INDEX nombreCliente ON "+t_clientes+"("+Contract.C_EMAIL+" ASC)" );

        String avisos="CREATE TABLE " +Contract.T_AVISOS
                + "("+Contract.C_ID+" INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL, " +
                Contract.C_DESTINO+" VARCHAR NOT NULL, " +
                Contract.C_DIRECCION+" VARCHAR, "+
                Contract.C_FECHA_HORA+" STRING NOT NULL " +
                "DEFAULT CURRENT_TIMESTAMP, " +
                Contract.C_URGENTE+" BOOL DEFAULT 0, " +
                Contract.C_REALIZADA+" BOOL DEFAULT 0, " +
                Contract.C_LATITUD+" DOUBLE, "+
                Contract.C_LONGITUD+" DOUBLE, "+
                Contract.C_PATH_INFORME+" VARCHAR)";
        db.execSQL(avisos);
        Log.i(this.getClass().toString(), "Tabla clientes creada...");
		
		/*
		 * Insertamos datos iniciales
		 */
		/*String columns =Contract.C_NOMBRE+","+Contract.C_EMAIL+","+Contract.C_TELEFONO+","+Contract.C_DESCRIPCION;
		values.clear();
		values.put(Contract.C_NOMBRE, "Cliente1");
		values.put(Contract.C_EMAIL, "cliente1@cliente1.es");
		values.put(Contract.C_TELEFONO, "600600600");
		values.put(Contract.C_DESCRIPCION, "Cliente banca");
		db.insert(t_clientes, columns, values);
		
		values.clear();
		
		values.put(Contract.C_NOMBRE, "pedro");
		values.put(Contract.C_EMAIL, "pedro@cliente2.es");
		values.put(Contract.C_TELEFONO, "655");
		values.put(Contract.C_DESCRIPCION, "Cliente banca");
		values.put(Contract.C_PREFERENTE, false);
		
		db.insert(t_clientes, columns, values);
		
		Log.i(this.getClass().toString(),"Datos iniciales de cliente insertados");*/

		Log.i(this.getClass().toString(), "Base de datos creada");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//TODO actualización de la versión de la base de datos, migraciones.
	}

}
