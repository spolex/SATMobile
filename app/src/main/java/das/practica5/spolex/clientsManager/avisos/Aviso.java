package das.practica5.spolex.clientsManager.avisos;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.opengl.EGLExt;
import android.provider.Settings;
import android.widget.Toast;

import das.practica5.spolex.clientsManager.avisos.adapters.AvisoBdAdapter;

/**
 * Created by spolex on 21/03/15.
 */
public class Aviso {

    private Context contexto;
    private String mDestino;
    private String mFechaHora;
    private Boolean mUrgente;
    private Boolean mRealizada;
    private String mInforme;
    private Long mId;
    private Double latitud;
    private Double longitud;
    private String mDireccion;


    public Aviso(Context contexto, String mDestino, String mFechaHora, Boolean mUrgente, Boolean mRealizada, String mInforme, Double latitud, Double longitud, String direccion) {
        this.contexto = contexto;
        this.mDestino = mDestino;
        this.mFechaHora = mFechaHora;
        this.mUrgente = mUrgente;
        this.mRealizada = mRealizada;
        this.mInforme = mInforme;
        this.latitud = latitud;
        this.longitud = longitud;
        this.mDireccion = direccion;
    }
    public String getmDireccion() {
        return mDireccion;
    }

    public void setmDireccion(String mDireccion) {
        this.mDireccion = mDireccion;
    }

    public Long getmId() {
        return mId;
    }

    public void setmFechaHora(String mFechaHora) {
        this.mFechaHora = mFechaHora;
    }

    public void setmId(long mId) {
        this.mId = mId;

    }

    public Context getContexto() {
        return contexto;
    }

    public void setContexto(Context contexto) {
        this.contexto = contexto;
    }

    public String getmDestino() {
        return mDestino;
    }

    public void setmDestino(String mDestino) {
        this.mDestino = mDestino;
    }

    public String getmFechaHora() {
        return mFechaHora;
    }

    public Boolean getmUrgente() {
        return mUrgente;
    }

    public void setmUrgente(Boolean mUrgente) {
        this.mUrgente = mUrgente;
    }

    public Boolean getmRealizada() {
        return mRealizada;
    }

    public void setmRealizada(Boolean mRealizada) {
        this.mRealizada = mRealizada;
    }

    public String getmInforme() {
        return mInforme;
    }

    public void setmInforme(String mInforme) {
        this.mInforme = mInforme;
    }

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }

    public Aviso(Context pContexto){
        this.contexto=pContexto;


    }

    public static Aviso cursorToAviso(Context context, Cursor c)
    {
        Aviso aviso = null;

        if (c != null)
        {
            aviso = new Aviso(context);

            aviso.setmId(c.getLong(c.getColumnIndex(AvisoBdAdapter.C_ID)));
            aviso.setmDestino(c.getString(c.getColumnIndex(AvisoBdAdapter.C_DESTINO)));
            aviso.setmFechaHora(c.getString(c.getColumnIndex(AvisoBdAdapter.C_FECHA_HORA)));
            aviso.setmUrgente(c.getInt(c.getColumnIndex(AvisoBdAdapter.C_URGENTE)) == 1);
            aviso.setmRealizada(c.getInt(c.getColumnIndex(AvisoBdAdapter.C_REALIZADA)) == 1);
            aviso.setmInforme(c.getString(c.getColumnIndex(AvisoBdAdapter.C_PATH_INFORME)));
            aviso.setLatitud(c.getDouble(c.getColumnIndex(AvisoBdAdapter.C_LAT)));
            aviso.setLongitud(c.getDouble(c.getColumnIndex(AvisoBdAdapter.C_LONG)));
            aviso.setmDireccion(c.getString(c.getColumnIndex(AvisoBdAdapter.C_DIRECCION)));

        }

        return aviso ;
    }

    private ContentValues toContentValues()
    {
        ContentValues reg = new ContentValues();

        if(this.getmDestino()!=null)reg.put(AvisoBdAdapter.C_DESTINO, this.getmDestino());
        if(this.getmFechaHora()!=null)reg.put(AvisoBdAdapter.C_FECHA_HORA, String.valueOf(this.getmFechaHora()));
        if(this.getLatitud()!=null)reg.put(AvisoBdAdapter.C_LAT, this.getLatitud());
        if(this.getLongitud()!=null)reg.put(AvisoBdAdapter.C_LONG, this.getLongitud());
        if(this.getmInforme()!=null)reg.put(AvisoBdAdapter.C_PATH_INFORME,this.getmInforme());
        if(this.getmUrgente()!=null)reg.put(AvisoBdAdapter.C_URGENTE,this.getmUrgente());
        if(this.getmRealizada()!=null)reg.put(AvisoBdAdapter.C_REALIZADA, this.getmRealizada());
        reg.put(AvisoBdAdapter.C_ID,this.getmId());
        if(this.getLatitud()!=null)reg.put(AvisoBdAdapter.C_LAT,this.getLatitud());
        if(this.getLongitud()!=null)reg.put(AvisoBdAdapter.C_LONG,this.getLongitud());
        if(this.getmDireccion()!=null)reg.put(AvisoBdAdapter.C_DIRECCION,this.getmDireccion());
        if(this.getmInforme()!=null)reg.put(AvisoBdAdapter.C_PATH_INFORME, this.getmInforme());

        return reg;
    }

    public Long save()
    {

        AvisoBdAdapter dbAdapter = new AvisoBdAdapter(this.getContexto());

        // comprobamos si estamos insertando o actualizando según esté o no relleno el identificador
        if ((this.getmId() == null) || (!dbAdapter.exists(this.getmId())))
        {

            long nuevoId = dbAdapter.insert(this.toContentValues());

            if (nuevoId != -1)
            {
                this.setmId(nuevoId);
            }
        }
        else
        {
            return dbAdapter.update(this.toContentValues());
        }
        return this.getmId();
    }

    public long delete()
    {
        // borramos el registro
        AvisoBdAdapter dbAdapter = new AvisoBdAdapter(this.getContexto());

        long rdo = dbAdapter.delete(this.getmId());
        dbAdapter.cerrar();

        return rdo;
    }
    public static Aviso find(Context context, long id)
    {
        AvisoBdAdapter dbAdapter = new AvisoBdAdapter(context);

        Cursor c = dbAdapter.getRegistro(id);

        Aviso aviso = Aviso.cursorToAviso(context, c);

        c.close();

        return aviso;
    }
}
