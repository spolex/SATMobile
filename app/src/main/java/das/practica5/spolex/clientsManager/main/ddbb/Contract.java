package das.practica5.spolex.clientsManager.main.ddbb;

import android.os.Environment;

public class Contract
{
	public static final int BD_VERSION = 2;
	public static final String BD_NOMBRE ="MobileSAT";
    public static final String REPOSITORIO = Environment.getExternalStorageDirectory()+"Android/data/"+ Contract.PACKET+"/";

    //Constantes para la palicación
    //Nombre del paquete
    public static final String PACKET = "com.spolex.satmobile";
    //Posible uso de tabla de usuario
	public static final String T_USUARIO="Usuario";
	public static final String C_CODIGO="codigo";
	public static final String C_PASS ="pass";
	public static final String C_ADMIN = "admin";
	public static final String C_DESCRIPCION="descripcion";
	public static final String C_OBSERVACIONES="observaciones";
	public static final String C_ID="_id";

    //Tabla de cliente
	public static final String T_CLIENTE="Cliente";
	public static final String C_EMAIL="email";
	public static final String C_TELEFONO="telefono";
	public static final String C_NOMBRE = "nombre";
    public static final String C_APELLIDOS="apellidos";
    public static final String C_DIRECCION="direccion";
    public static final String C_LATITUD="latitud";
    public static final String C_LONGITUD="longitud";
    public static final String C_EMPRESA="empresa";
    public static final String C_ALTA = "fecha_alta";
    public static final String C_PATH_IMAGEN ="imagen" ;

    //Tabla de avisos
    public static final String T_AVISOS ="aviso";
    public static final String C_DESTINO="cliente_id";
    public static final String C_FECHA_HORA = "fecha_hora";
    public static final String C_URGENTE = "urgente";
    public static final String C_REALIZADA = "realizada";
    public static final String C_PATH_INFORME ="informe" ;

    public static final String C_ACC = "accuracy";
}
