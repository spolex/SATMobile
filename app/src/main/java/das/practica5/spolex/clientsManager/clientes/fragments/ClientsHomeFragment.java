package das.practica5.spolex.clientsManager.clientes.fragments;


import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import das.practica5.spolex.clientsManager.R;

/**
 * A simple {@link Fragment} subclass.
 */


public class ClientsHomeFragment extends Fragment {


    public ClientsHomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        return inflater.inflate(R.layout.fragment_home, container, false);
    }


}
