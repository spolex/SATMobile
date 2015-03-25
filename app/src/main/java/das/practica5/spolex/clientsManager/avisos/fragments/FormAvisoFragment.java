package das.practica5.spolex.clientsManager.avisos.fragments;

import android.app.Activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;

import das.practica5.spolex.clientsManager.R;
import das.practica5.spolex.clientsManager.avisos.Aviso;
import das.practica5.spolex.clientsManager.clientes.Cliente;
import das.practica5.spolex.clientsManager.clientes.adapters.ClienteBdAdapter;
import das.practica5.spolex.clientsManager.main.SATManagerActivity;
import das.practica5.spolex.clientsManager.main.ddbb.Contract;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FormAvisoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FormAvisoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FormAvisoFragment extends Fragment implements View.OnClickListener{

    private int mYear;
    private int mMonth;
    private int mDay;

    private int mHour;
    private int mMinute;

    final Calendar calendar = Calendar.getInstance();

    private Button btn_aceptar;
    private Button btn_mapa;

    private Button btn_date;
    private EditText date;

    private Button btn_time;
    private EditText time;

    private CheckBox urg_check;
    private CheckBox real_check;

    private EditText informe;

    private Spinner spn_clientes;
    private ClienteBdAdapter mBdAdapter;
    private ArrayList<Cliente> mClientes;

    public static String LOG_TAG = FormAvisoFragment.class.getSimpleName().toString();

    //gestionar el modo de apertura del formulario
    public static String C_MODO = "modo";
    private static String C_ID = "_id";
    private static int C_CREAR = 552;
    private static int C_VISUALIZAR = 551;
    private static int C_EDITAR = 553;

    private static Aviso avisof;
    private int modo;

    private ArrayAdapter<String> adapter;
    private Long id;
    private double mLat;
    private double mLong;
    private double mAcc;

    private String mPath;




    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FormAvisoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FormAvisoFragment newInstance(String param1, String param2) {
        FormAvisoFragment fragment = new FormAvisoFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public FormAvisoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //mParam1 = getArguments().getString(ARG_PARAM1);
            //mParam2 = getArguments().getString(ARG_PARAM2);
        }
        /*
        Es necesaria la llamada hasOptionsMenu para indicar que el fragment quiere añadir items al menú
        sin esta llamada oncreateoptionsmenu no resulta
         */
        setHasOptionsMenu(true);
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        if(this.modo == C_VISUALIZAR)inflater.inflate(R.menu.menu_form_avisos, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_actualizar:
                mListener.updateAviso(avisof.getmId());
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        return inflater.inflate(R.layout.fragment_form_aviso, container, false);


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle extra = this.getArguments();

        if (extra == null) return;

        btn_date = (Button) getActivity().findViewById(R.id.avisos_btn_datepicker_id);
        btn_date.setOnClickListener(this);

        btn_time = (Button) getActivity().findViewById(R.id.avisos_form_timepicker_id);
        btn_time.setOnClickListener(this);

        date = (EditText) getActivity().findViewById(R.id.avisos_form_date_id);
        time = (EditText) getActivity().findViewById(R.id.avisos_form_time_id);

        informe =(EditText) getActivity().findViewById(R.id.avisos_form_informe_id);

        spn_clientes = (Spinner) getActivity().findViewById(R.id.avisos_form_clientes_spin_id);
        mBdAdapter = new ClienteBdAdapter(getActivity());
        mClientes = mBdAdapter.getClientes(null);

        ArrayList<String> items = new ArrayList<String>();

        for (Cliente cliente:mClientes){
            items.add(cliente.getNombre()+"-"+cliente.getEmail());
        }

        adapter =new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,items);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spn_clientes.setAdapter(adapter);

        urg_check = (CheckBox) getActivity().findViewById(R.id.avisos_form_urg_check_id);
        real_check = (CheckBox) getActivity().findViewById(R.id.avisos_form_check_realizadas_id);

        btn_aceptar = (Button) getActivity().findViewById(R.id.avisos_form_btn_aceptar_id);
        btn_mapa = (Button) getActivity().findViewById(R.id.avisos_form_btn_mapa_id);
        btn_mapa.setOnClickListener(this);
        btn_aceptar.setOnClickListener(this);

        /**
         * Obtenemos el indicador del registro si viene indicado
         */
        if (extra.containsKey(C_ID))
        {
            id = extra.getLong(C_ID);
            consultar(id);
        }

        if (extra.containsKey(C_MODO)){
            this.modo = extra.getInt(C_MODO);
        }
        else{
            this.modo = C_CREAR;
        }
        if(extra.containsKey(Contract.C_LATITUD)){
            this.mLat = extra.getDouble(Contract.C_LATITUD);
        }
        if(extra.containsKey(Contract.C_LONGITUD)){
            this.mLong = extra.getDouble(Contract.C_LONGITUD);
        }

        //Se establece el modo
        establecerModo(extra.getInt(C_MODO));

    }

    private void establecerModo(int m)
    {
        this.modo = m ;

        if (modo == C_VISUALIZAR)
        {
            this.setEdicion(false);
        }
        else if (modo == C_CREAR)
        {
            this.setEdicion(true);
        }
        else if (modo == C_EDITAR)
        {
            this.setEdicion(true);
        }

    }
    private void setEdicion(boolean opcion)
    {
        spn_clientes.setEnabled(opcion);
        btn_date.setEnabled(opcion);
        btn_time.setEnabled(opcion);
        urg_check.setEnabled(opcion);
        real_check.setEnabled(opcion);
        btn_aceptar.setEnabled(opcion);
        btn_mapa.setEnabled(opcion);
        date.setEnabled(opcion);
        time.setEnabled(opcion);

        if(this.modo==C_EDITAR){
            spn_clientes.setEnabled(false);
        }
        if(this.modo==C_CREAR){
            btn_mapa.setEnabled(false);
        }
        if(this.modo==C_VISUALIZAR){
            btn_mapa.setEnabled(true);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.avisos_btn_datepicker_id:
                mYear = calendar.get(Calendar.YEAR);
                mMonth = calendar.get(Calendar.MONTH);
                mDay = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dpd = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                date.setText(dayOfMonth + "-"
                                        + (monthOfYear + 1) + "-" + year);

                            }
                        }, mYear, mMonth, mDay);
                dpd.show();
                break;
            case R.id.avisos_form_timepicker_id:
                TimePickerDialog tpd = new TimePickerDialog(getActivity(),
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                time.setText(hourOfDay + ":" + minute);
                            }
                        }, mHour, mMinute, false);
                tpd.show();
                break;
            case R.id.avisos_form_btn_aceptar_id:
                if(comprobarFormulario()){
                        guardar();
                }
                else{
                        Toast.makeText(getActivity(),getResources().getString(R.string.msg_campos_vacios), Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.avisos_form_btn_mapa_id:
                if (this.modo == C_EDITAR || this.modo == C_VISUALIZAR)
                {
                    if (avisof!=null){
                        mostrarRuta();
                    }
                }
                break;
        }

    }

    private void mostrarRuta() {


            LocationManager elmanager =(LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

            double lat;
            double longitud;
            LocationListener locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {

                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            };



            Criteria loscriterios = new Criteria();

            loscriterios.setAccuracy(Criteria.ACCURACY_FINE);

            String mejorProveedor = elmanager.getBestProvider(loscriterios, true);
            if(!elmanager.isProviderEnabled(mejorProveedor)){
                Intent i= new Intent (Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                this.startActivity(i);
            }

            elmanager.requestLocationUpdates(mejorProveedor,1,0,locationListener);

            Location pos = elmanager.getLastKnownLocation(mejorProveedor);

            lat = pos.getLatitude();
            longitud = pos.getLongitude();

            Uri uri =Uri.parse("geo:0,0?q="+lat+","+longitud);

            //ruta entre dos puntos
          //  Uri uri = Uri.parse("http://maps.google.com/maps?saddr="+this.mLat+","+Double.valueOf(this.mLong)+"&daddr="+Double.valueOf(lat)+","+Double.valueOf(longitud));

            Intent intent = new Intent(Intent.ACTION_VIEW,uri);
            startActivity(intent);

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        public void updateAviso(Long id);
    }

    private boolean comprobarFormulario() {
        if(time.getText()==null || time.getText().length()<1)
        return false;

        if(date.getText()==null || date.getText().length()<1)
            return false;

        return true;
    }

    private void guardar()
    {

        final Aviso avisonuevo ;

        if (this.modo == C_EDITAR)
        {
            avisonuevo = Aviso.find(getActivity(), id);
        }
        else
        {
            avisonuevo = new Aviso(getActivity()) ;
        }


        avisonuevo.setmRealizada(real_check.isChecked());
        avisonuevo.setmUrgente(urg_check.isChecked());
        avisonuevo.setmFechaHora(date.getText().toString()+" "+time.getText().toString());
        avisonuevo.setmDestino(spn_clientes.getSelectedItem().toString());
        avisonuevo.setLatitud(this.mLat);
        avisonuevo.setLongitud(this.mLong);
        guardarInforme();
        avisonuevo.setmInforme(mPath);


        if (modo == C_CREAR)
        {
            AlertDialog.Builder dialogCrearCliente = new AlertDialog.Builder(getActivity());

            dialogCrearCliente.setIcon(android.R.drawable.ic_dialog_alert);
            dialogCrearCliente.setTitle(getResources().getString(R.string.titulo_home_avisos));
            dialogCrearCliente.setMessage(getResources().getString(R.string.msg_conf_aviso_nuevo));
            dialogCrearCliente.setCancelable(false);

            dialogCrearCliente.setPositiveButton(getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int boton) {
                    //TODO controlar guardado del informe
                    if (avisonuevo.getmInforme() != null) {
                        if (avisonuevo.save() != null) {
                            Toast.makeText(getActivity(), getResources().getString(R.string.msg_exito_guardar_aviso), Toast.LENGTH_LONG).show();
                            getActivity().setResult(Activity.RESULT_CANCELED);
                        } else {
                            getActivity().setResult(Activity.RESULT_OK);
                            Toast.makeText(getActivity(), getResources().getString(R.string.msg_no_posible_guardar_aviso), Toast.LENGTH_LONG).show();
                        }
                        getActivity().setResult(Activity.RESULT_OK);
                    }
                }
            });
            dialogCrearCliente.setNegativeButton(getResources().getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    getActivity().setResult(Activity.RESULT_CANCELED);
                }
            });
            dialogCrearCliente.show();

        }
        else if (modo == C_EDITAR)
        {
            AlertDialog.Builder dialogCrearCliente = new AlertDialog.Builder(getActivity());

            dialogCrearCliente.setIcon(android.R.drawable.ic_dialog_alert);
            dialogCrearCliente.setTitle(getResources().getString(R.string.titulo_home_avisos));
            dialogCrearCliente.setMessage(getResources().getString(R.string.msg_conf_editar_aviso));
            dialogCrearCliente.setCancelable(false);

            dialogCrearCliente.setPositiveButton(getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int boton) {

                    if(avisonuevo.save()==0){
                        Toast.makeText(getActivity(),getResources().getString(R.string.msg_no_posible_actualizar_aviso), Toast.LENGTH_LONG).show();
                        getActivity().setResult(Activity.RESULT_CANCELED);
                    }else {
                        getActivity().setResult(Activity.RESULT_OK);
                        Toast.makeText(getActivity(),getResources().getString(R.string.msg_exito_actualizar_aviso), Toast.LENGTH_LONG).show();
                    }
                }
            });
            dialogCrearCliente.setNegativeButton(getResources().getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    getActivity().setResult(Activity.RESULT_CANCELED);
                }
            });
            dialogCrearCliente.show();
        }

        //
        // Devolvemos el control
        //
        getActivity().setResult(Activity.RESULT_OK);

    }

    private void consultar(long id)
    {
        //
        // Consultamos el cliente por el identificador
        //
        avisof = Aviso.find(getActivity(), id);
        spn_clientes.setSelection(adapter.getPosition(avisof.getmDestino()));
        String[] fecha_hora = avisof.getmFechaHora().split(" ");
        date.setText(fecha_hora[0]);
        time.setText(fecha_hora[1]);
        urg_check.setChecked(avisof.getmUrgente());
        real_check.setChecked(avisof.getmRealizada());
        informe.setText(obtenerInforme(avisof.getmInforme()));
    }

    private String obtenerInforme(String path) {
        String inf=null;
        File file = new File(path);
        try
        {
            FileInputStream fIn = new FileInputStream(file);
            InputStreamReader archivo = new InputStreamReader(fIn);
            BufferedReader br = new BufferedReader(archivo);
            String linea = br.readLine();
            String todo = "";
            while (linea != null) {
                todo = todo + linea + " \n";
                linea = br.readLine();
            }
            br.close();
            archivo.close();
            inf=todo;
            return inf;
        }
        catch (IOException e)
        {
            Toast.makeText(getActivity(),getString(R.string.msg_error_recuperar_informe),Toast.LENGTH_LONG).show();
        }
        return inf;
    }

    private void guardarInforme() {
        if(informe.getText()!=null){
            String contenido = informe.getText().toString();
            String destino = spn_clientes.getSelectedItem().toString();

            File dir = new File(Environment.getExternalStorageDirectory(),"Informes");
            if(!dir.exists()) {
                dir.mkdir();
            }
            if(dir.isDirectory()) {
                File file = new File(dir,"informe"+String.valueOf(System.currentTimeMillis())+".txt");
                if(!file.exists()) try {
                    file.createNewFile();
                    FileWriter writer = new FileWriter(file);
                    writer.append(contenido + "\n" + destino);
                    writer.flush();
                    writer.close();
                    Log.i(LOG_TAG, getString(R.string.msg_archivo_guardado));
                    mPath = file.getAbsolutePath();

                } catch (IOException e) {
                    Log.i(LOG_TAG, getString(R.string.msg_archivo_error_guardado));
                    Toast.makeText(getActivity(),getString(R.string.msg_archivo_error_guardado),Toast.LENGTH_LONG).show();
                }
            }

        }
    }


}
