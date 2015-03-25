package das.practica5.spolex.clientsManager.clientes;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;

import das.practica5.spolex.clientsManager.R;
import das.practica5.spolex.clientsManager.avisos.fragments.FormAvisoFragment;
import das.practica5.spolex.clientsManager.avisos.fragments.ListAvisoFragment;
import das.practica5.spolex.clientsManager.clientes.adapters.ClienteBdAdapter;
import das.practica5.spolex.clientsManager.clientes.fragments.ClientsFragmentForm;
import das.practica5.spolex.clientsManager.clientes.fragments.ClientsHomeFragment;
import das.practica5.spolex.clientsManager.clientes.fragments.ListaClientesFragment;
import das.practica5.spolex.clientsManager.main.SettingsActivity;
import das.practica5.spolex.clientsManager.main.ddbb.Contract;

import static das.practica5.spolex.clientsManager.R.id.contenido;
import static das.practica5.spolex.clientsManager.R.id.toolbar;

public class ClientsActivity extends ActionBarActivity implements ClientsFragmentForm.FormClientFragmentInteractionListener
        ,ListaClientesFragment.OnFragmentInteractionListener{

    private static final String LOG_TAG = ClientsActivity.class.getSimpleName() ;
    private static final String C_MODO = "modo";
    private static final String C_id ="_id";
    public static final int C_VISUALIZAR = 551 ;
    public static final int C_CREAR = 552;
    public static final int C_EDITAR = 553;

    ClienteBdAdapter mBdClientsAdapter;
    Cliente clientef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clients);

        //New toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar_cl);
        toolbar.setBackgroundColor(Color.parseColor("#4585f2"));
        toolbar.setTitleTextColor(Color.WHITE);

        setSupportActionBar(toolbar);

        mBdClientsAdapter = new ClienteBdAdapter(this);
        clientef = mBdClientsAdapter.getClientes(null).get(0);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE &&
                clientef.getId()!=null) {
            cargarFragment(C_VISUALIZAR,clientef.getId());
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_clients, menu);
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
            Intent i = new Intent(this,SettingsActivity.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void cargarFragment(int modo,long selected) {
        Fragment elfragment = null;
        elfragment = new ClientsFragmentForm();
        Bundle bundle = new Bundle();
        bundle.putInt(C_MODO, modo);
        bundle.putLong(C_id, selected);
        elfragment.setArguments(bundle);
        //Validamos si el fragment no es null

        if (elfragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.contenido_clientes_detalle_id, elfragment).commit();
        } else {
            //Si el fragment es nulo mostrar un mensaje de error
            Log.e(LOG_TAG, getString(R.string.msg_fragment_nulo));
        }
    }

    //Listenner de la lista de clientes
    @Override
    public void onFragmentInteraction(long selected) {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            cargarFragment(C_VISUALIZAR,selected);
        }

    }

    //Listenners del formulario clientes
    @Override
    public void FormClientFragmentInteraction(long selected) {
        cargarFragment(C_EDITAR,selected);
    }

    @Override
    public void onDeleteSelectedCliente(long id) {
        ListaClientesFragment listclientes =(ListaClientesFragment)getFragmentManager().findFragmentById(R.id.main_listaclientes_fragment_id);
        if(listclientes!=null)listclientes.refrescar();
    }


}
