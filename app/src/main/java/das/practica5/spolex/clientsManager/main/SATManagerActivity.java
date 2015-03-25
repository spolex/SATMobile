package das.practica5.spolex.clientsManager.main;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.Fragment;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import das.practica5.spolex.clientsManager.avisos.Aviso;
import das.practica5.spolex.clientsManager.avisos.adapters.AvisoBdAdapter;
import das.practica5.spolex.clientsManager.avisos.fragments.ListAvisoFragment;
import das.practica5.spolex.clientsManager.main.adapters.Item_nav;
import das.practica5.spolex.clientsManager.main.adapters.NavigationAdapter;
import das.practica5.spolex.clientsManager.R;
import das.practica5.spolex.clientsManager.avisos.fragments.FormAvisoFragment;
import das.practica5.spolex.clientsManager.clientes.adapters.ClienteBdAdapter;
import das.practica5.spolex.clientsManager.clientes.fragments.ClientsFragmentForm;
import das.practica5.spolex.clientsManager.clientes.fragments.ClientsHomeFragment;
import das.practica5.spolex.clientsManager.clientes.fragments.ListaClientesFragment;
import das.practica5.spolex.clientsManager.main.ddbb.Contract;

import static das.practica5.spolex.clientsManager.R.id.contenido;


public class SATManagerActivity extends ActionBarActivity implements AdapterView.OnItemClickListener, ListaClientesFragment.OnFragmentInteractionListener, ClientsFragmentForm.FormClientFragmentInteractionListener, FormAvisoFragment.OnFragmentInteractionListener,
ListAvisoFragment.OnFragmentInteractionListener{


    //Etiqueta de la clase para el log
    private static final String LOG_TAG = SATManagerActivity.class.getSimpleName();

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawLayout;
    private ListView mNavList;
    private ArrayList<Item_nav> mNavsItem;
    private String[] titulos;
    private TypedArray NavIcons;
    private NavigationAdapter NavAdapter;

    public static final String C_MODO  = "modo" ;
    public static final String C_id = "_id";
    public static final int C_VISUALIZAR = 551 ;
    public static final int C_CREAR = 552;
    public static final int C_EDITAR = 553;

    private double mLat;
    private double mLong;
    private double mAcc;


    //Atributos para gestión de BD
    //private ClienteBdAdapter bdAdapterClientes;
    private AvisoBdAdapter bdAdapterAvisos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_clients_manager);
        mDrawLayout = (DrawerLayout)findViewById(R.id.drawer_layout);

        mNavList = (ListView) findViewById(R.id.el_menu);
        //ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,);
        //mNavList.setAdapter(adaptador);

        //Declaramos el header el caul sera el layout de header.xml
        View header = getLayoutInflater().inflate(R.layout.header, null);
        //Establecemos header
        mNavList.addHeaderView(header);

        //Tomamos listado  de imgs desde drawable
        NavIcons = getResources().obtainTypedArray(R.array.navigation_iconos);

        //Tomamos listado  de titulos desde el string-array de los recursos @string/nav_options
        titulos = getResources().getStringArray(R.array.nav_options);
        //Listado de titulos de barra de navegacion
        mNavsItem = new ArrayList<Item_nav>();

        //Agregamos objetos  al array
        //Home
        mNavsItem.add(new Item_nav(titulos[0], NavIcons.getResourceId(0, -1)));
        //Clientes
        mNavsItem.add(new Item_nav(titulos[1], NavIcons.getResourceId(1, -1)));
        //Añadir
        mNavsItem.add(new Item_nav(titulos[2], NavIcons.getResourceId(2, -1)));
        //Avisos
        mNavsItem.add(new Item_nav(titulos[3], NavIcons.getResourceId(3, -1)));
        //Añadir aviso
        mNavsItem.add(new Item_nav(titulos[4], NavIcons.getResourceId(2, -1)));


        // Establecemos en el menú el adaptador que contiene el array con los titulos
        NavAdapter= new NavigationAdapter(this,mNavsItem);
        mNavList.setAdapter(NavAdapter);


        mNavList.setOnItemClickListener(this);

        //New toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(Color.parseColor("#4585f2"));
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawLayout,
                toolbar, R.string.menu_abierto, R.string.menu_cerrado) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };


        // Set the drawer toggle as the DrawerListener
        mDrawLayout.setDrawerListener(mDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);

         /*
         * Declaramos el controlador de la BBDD y accedemos en modo escritura
         */
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(Contract.BD_NOMBRE, "Prueba");
        editor.commit();
        //bdAdapterClientes = new ClienteBdAdapter(getApplicationContext());
        bdAdapterAvisos = new AvisoBdAdapter(getApplicationContext());

        updateLocation();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_friends_manager, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Listview del Nav drawer
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        cargarFragment(position, C_CREAR ,0);
    }

    /**
     * Recibe por parámetro la posición en el menú que corresponde unívocamente con un fragment
     * @param position
     */
    private void cargarFragment(int position, int modo, long selected){
        Fragment elfragment = null;
        Bundle bundle = null;
        ClientsFragmentForm fragmentDetail = null;
        switch (position){
            case 1:
                elfragment = new ClientsHomeFragment();
                break;
            case 2:
                elfragment = new ListaClientesFragment();
                break;
            case 3:
                updateLocation();
                elfragment = new ClientsFragmentForm();
                bundle = new Bundle();
                bundle.putInt(C_MODO,modo);
                if(modo == C_EDITAR || modo == C_VISUALIZAR){
                    bundle.putLong(C_id, selected);
                }
                elfragment.setArguments(bundle);
                break;
            case 4:
                elfragment = new ListAvisoFragment();
                break;
            case 5:
                updateLocation();
                elfragment = new FormAvisoFragment();
                bundle = new Bundle();
                bundle.putInt(C_MODO,modo);
                bundle.putDouble(Contract.C_LATITUD,this.mLat);
                bundle.putDouble(Contract.C_LONGITUD, this.mLong);
                bundle.putDouble(Contract.C_ACC, this.mAcc);
                if(modo == C_EDITAR || modo == C_VISUALIZAR){
                    bundle.putLong(C_id, selected);
                }
                elfragment.setArguments(bundle);

                break;
            default:
                //Si la opción no está mostrará un toast
                break;
        }
            //Validamos si el fragment no es null

            if(elfragment!=null){

                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(contenido, elfragment).commit();
                //Actualizar el contenido según la opción escogida
                mNavList.setItemChecked(position, true);
                mNavList.setSelection(position);
                //Cambiar el título
                setTitle(titulos[position-1]);
                //Cerrar el menú deslizable
                mDrawLayout.closeDrawer(mNavList);
        }
        else
        {
            //Si el fragment es nulo mostrar un mensaje de error
            Log.e(LOG_TAG,getString(R.string.msg_fragment_nulo));
        }
    }


    //Listenner de la lista de clientes
    @Override
    public void onFragmentInteraction(long selected) {

           cargarFragment(3,C_VISUALIZAR,selected);
    }

    //Listenner del formulario clientes
    @Override
    public void FormClientFragmentInteraction(long selected) {
        cargarFragment(3,C_EDITAR,selected);
    }

    @Override
    public void onDeleteSelectedCliente(long id) {
        ListaClientesFragment listclientes =(ListaClientesFragment)getFragmentManager().findFragmentById(R.id.contenido);
        if(listclientes!=null)listclientes.refrescar();
    }

    //Listenner de formulario avisos
    @Override
    public void updateAviso(Long id) {
        cargarFragment(5,C_EDITAR,id);
    }

    //Listenner lista avisos

    //Para cargar selección en modo visualizar
    @Override
    public void onSelectedAvisoInteraction(long id) {
        cargarFragment(5, C_VISUALIZAR, id);
    }

    //Para eliminar selección
    @Override
    public void onDeleteSelectedAviso(long id) {
        borrar(Aviso.find(getApplicationContext(), id));

    }

    private void borrar(final Aviso aviso)
    {
        /**
         * Borramos el registro con confirmación
         */
        AlertDialog.Builder dialogEliminar = new AlertDialog.Builder(this);

        dialogEliminar.setIcon(android.R.drawable.ic_dialog_alert);
        dialogEliminar.setTitle(getResources().getString(R.string.aviso_eliminar_titulo));
        dialogEliminar.setMessage(getResources().getString(R.string.aviso_eliminar_mensaje));
        dialogEliminar.setCancelable(false);

        dialogEliminar.setPositiveButton(getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int boton) {
                if(aviso.delete()>0) {
                    ListAvisoFragment listavisos = (ListAvisoFragment) getFragmentManager().findFragmentById(R.id.contenido);
                    if (listavisos != null) listavisos.refresh();
                    Toast.makeText(getApplicationContext(), R.string.aviso_eliminar_confirmacion, Toast.LENGTH_SHORT).show();
                    setResult(Activity.RESULT_OK);
                }
                else{
                    Toast.makeText(getApplicationContext(), R.string.aviso_eliminar_error, Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialogEliminar.setNegativeButton(android.R.string.no, null);
        dialogEliminar.show();

    }

    private void updateLocation() {

        LocationManager elmanager =(LocationManager) getSystemService(Context.LOCATION_SERVICE);

        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                mLat = location.getLatitude();
                mLong = location.getLongitude();
                mAcc = location.getAccuracy();
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
        if(!elmanager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            Intent i= new Intent (Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            this.startActivity(i);
        }

        elmanager.requestLocationUpdates(mejorProveedor,1,0,locationListener);

        Location pos = elmanager.getLastKnownLocation(mejorProveedor);

        this.mLat = pos.getLatitude();
        this.mLong = pos.getLongitude();
        this.mAcc = pos.getAccuracy();
    }

}


