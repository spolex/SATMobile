package das.practica5.spolex.clientsManager.clientes.fragments;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import das.practica5.spolex.clientsManager.R;
import das.practica5.spolex.clientsManager.clientes.Cliente;
import das.practica5.spolex.clientsManager.main.SATManagerActivity;
import das.practica5.spolex.clientsManager.clientes.adapters.ClienteBdAdapter;
import das.practica5.spolex.clientsManager.main.ddbb.Contract;


/**
 * A simple {@link Fragment} subclass.
 */
public class ClientsFragmentForm extends Fragment implements View.OnClickListener{

    //Clases para gestionar la bd
    private ClienteBdAdapter bdAdapter;

    //Modo del formulario para actulizar o mostrar
    private int modo;
    private long id;

    public static String LOG_TAG = ClientsFragmentForm.class.getSimpleName().toString();



    //gestionar el modo de apertura del formulario
    public static String C_MODO = "modo";
    private static String C_ID = "_id";
    private static int C_CREAR = 552;
    private static int C_VISUALIZAR = 551;
    private static int C_EDITAR = 553;

    //Gestionar el origen del archivo de imagen
    private static final int PICK_FROM_CAMERA = 100;
    private static final int PICK_FROM_FILE = 101;

    //Botones
    private ImageButton btn_photo;
    private ImageView img_user;
    private Button btn_aceptar;
    private Button btn_cancelar;

    //Campos a obtener del formulario
    private EditText nombre;
    private EditText apellidos;
    private EditText telefono;
    private EditText direccion;
    private EditText empresa;
    private EditText fecha_alta;
    private EditText email;
    private static Cliente clientef;
    private double mLat;
    private double mLong;
    private double mAcc;

    private FormClientFragmentInteractionListener mListener;



    private Uri mImageCaptureUri;

    public ClientsFragmentForm() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //http://developer.android.com/guide/components/fragments.html
        /*
        Es necesaria la llamada hasOptionsMenu para indicar que el fragment quiere añadir items al menú
        sin esta llamada oncreateoptionsmenu no resulta
         */
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_form_clientes, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle extra = this.getArguments();

        if (extra == null) return;

        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

        //Botón para sacar foto de cliente nueva con la camara
        btn_photo = (ImageButton) getView().findViewById(R.id.clientsadder_img_btn_id);
        btn_photo.setOnClickListener(this);

        //Botones aceptar y cancelar
        btn_aceptar= (Button) getView().findViewById(R.id.clientsadder_btn_aceptar_id);
        btn_aceptar.setOnClickListener(this);
        btn_cancelar= (Button) getView().findViewById(R.id.clientsadder_btn_cancelar_id);
        btn_cancelar.setOnClickListener(this);

        //Campos del formulario
        nombre= (EditText)getView().findViewById(R.id.clientsadder_nombre_id);
        apellidos= (EditText)getView().findViewById(R.id.clientsadder_apellidos_id);
        telefono= (EditText)getView().findViewById(R.id.clientssadder_telefono_id);
        email=(EditText)getView().findViewById(R.id.clientsadder_email_id);
        direccion=(EditText)getView().findViewById(R.id.clientsadder_dir_id);
        empresa=(EditText)getView().findViewById(R.id.clientsadder_empresa_id);
        fecha_alta=(EditText)getView().findViewById(R.id.clientsadder_fechaalta_id);

        //Imagen que sirve para cargar imagen sacada o seleccionar una de la galería
        img_user = (ImageView) getView().findViewById(R.id.addclients_client_img_id);
        img_user.setOnClickListener(this);



        //Creamos el adaptador de la bd
        bdAdapter = new ClienteBdAdapter(getActivity());
        bdAdapter.abrir();

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
        //Establecemos alpha 80  [0,255] de la imagen  background, para resaltar los campos.
        Drawable backimg = (Drawable)getView().getResources().getDrawable(R.drawable.back_clientes);
        backimg.setAlpha(50);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.clientsadder_img_btn_id:
                //Saca una foto nueva
               getFromCamera();
                break;
            case R.id.addclients_client_img_id:
                //Seleccionar una foto existente de la galería
                getFromGallery();
                break;
            case R.id.clientsadder_btn_cancelar_id:
                resetear();
                break;
            case R.id.clientsadder_btn_aceptar_id:
                if(comprobarFormulario()){
                    try{
                        guardar();
                    }
                    catch (NumberFormatException e)
                    {
                        Log.e(LOG_TAG,getActivity().getString(R.string.msg_error_formato_telfno));
                        Toast.makeText(getActivity(),getActivity().getString(R.string.msg_error_formato_telfno),Toast.LENGTH_LONG).show();
                        telefono.setBackgroundColor(Color.parseColor("#B0E0E6"));
                    }
                }
                break;
            default:
                break;
        }
    }

    private boolean comprobarFormulario() {
        if(nombre.getText()==null){
            Toast.makeText(getActivity(),getActivity().getString(R.string.error_nombre_vacion),Toast.LENGTH_LONG).show();
            nombre.setBackgroundColor(Color.parseColor("#B0E0E6"));
            return false;
        }
        if(telefono.getText()==null)
        {
            Toast.makeText(getActivity(),getActivity().getString(R.string.msg_error_formato_telfno),Toast.LENGTH_LONG).show();
            telefono.setBackgroundColor(Color.parseColor("#B0E0E6"));
            return false;
        }
        if(direccion.getText()==null)
        {
            Toast.makeText(getActivity(),getActivity().getString(R.string.msg_campo_dir_no_vacio),Toast.LENGTH_LONG).show();
            direccion.setBackgroundColor(Color.parseColor("#B0E0E6"));
            return false;
        }
        if(mImageCaptureUri==null){
            Toast.makeText(getActivity(),getActivity().getString(R.string.msg_img_vacia),Toast.LENGTH_LONG).show();
            img_user.setBackgroundColor(Color.parseColor("#FFE8CD85"));
            return false;
        }
        if(email.getText()!=null){
            if (checkEmail()) return false;
        }
        else
        {
            Toast.makeText(getActivity(),getActivity().getString(R.string.msg_email_vacio),Toast.LENGTH_LONG).show();
            email.setBackgroundColor(Color.parseColor("#B0E0E6"));
            return false;
        }
        if(nombre.getText()==null){
            Toast.makeText(getActivity(),getActivity().getString(R.string.msg_nombre_vacio),Toast.LENGTH_LONG).show();
            nombre.setBackgroundColor(Color.parseColor("#B0E0E6"));
            return false;
        }
        return true;
    }

    /**
     *
     * @return
     */
    private boolean checkEmail() {
        if(email.getText().length()>0) {
            if (this.modo!=C_EDITAR && bdAdapter.exists(email.getText().toString())) {
                Toast.makeText(getActivity(), getActivity().getString(R.string.msg_email_registrado), Toast.LENGTH_LONG).show();
                return true;
            }
        }
        else
        {
            Toast.makeText(getActivity(),getActivity().getString(R.string.msg_email_vacio),Toast.LENGTH_LONG).show();
            email.setBackgroundColor(Color.parseColor("#B0E0E6"));
            return true;
        }
        return false;
    }

    //Tratar la captura/selección de la foto
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK)return;

        //Variables para capturar el resultado
        Bitmap bitmap   = null;
        String path     = "";

        switch (requestCode) {
            case PICK_FROM_FILE:
               try {
                   mImageCaptureUri = data.getData();
                   final InputStream imgstrm = getActivity().getContentResolver().openInputStream(mImageCaptureUri);
                   bitmap = BitmapFactory.decodeStream(imgstrm);
                   convertToFile(bitmap);

               }
               catch (FileNotFoundException e) {
                  Log.e(LOG_TAG,getString(R.string.msg_img_no_encontrada));
               } catch (IOException e) {
                   Log.e(LOG_TAG, getString(R.string.msg_error_escribir_img_sd));
               }
                break;
            case PICK_FROM_CAMERA:
                    //Toast.makeText(getActivity(),LOG_TAG,Toast.LENGTH_LONG).show();
                    path=mImageCaptureUri.getPath();
                    bitmap = BitmapFactory.decodeFile(path);
                break;
            default:
                break;
        }


        //para ajustarlo al imageviewer
        if(bitmap!=null){
            bitmap = Bitmap.createScaledBitmap(bitmap, 300, 300, false);
            img_user.setImageBitmap(bitmap);
        }
        else Toast.makeText(getActivity(),getActivity().getString(R.string.msg_img_no_valida),Toast.LENGTH_LONG).show();


    }

    private void  getFromGallery(){

        Intent intent = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, getString(R.string.msg_from_where)), PICK_FROM_FILE);

    }

    private void getFromCamera(){
        Intent intent    = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        String nomCliente = nombre.getText()!=null?nombre.getText().toString():"cliente";

        File dir = new File(Environment.getExternalStorageDirectory(),
                "Android/data/"+ Contract.PACKET+"/");
        dir.mkdir();
        if(dir.isDirectory()) {
            File file = new File(Environment.getExternalStorageDirectory(),
                    "Android/data/"+ Contract.PACKET+"/"+nomCliente+"_cliente_"+String.valueOf(System.currentTimeMillis())+".jpg");
            mImageCaptureUri = Uri.fromFile(file);
        }

        try
        {
            intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
            intent.putExtra("return-data", true);
            startActivityForResult(intent, PICK_FROM_CAMERA);

        }
        catch (Exception e)
        {
            Log.e(LOG_TAG, getString(R.string.msg_from_camera_fail));
        }
    }

    private void consultar(long id)
    {
        //
        // Consultamos el cliente por el identificador
        //
        clientef = Cliente.find(getActivity(), id);
        nombre.setText(clientef.getNombre());
        apellidos.setText(clientef.getApellidos());
        telefono.setText(String.valueOf(clientef.getTelefono()));
        email.setText(clientef.getEmail());
        empresa.setText(clientef.getEmpresa());
        direccion.setText(clientef.getDireccion());
        fecha_alta.setText(String.valueOf(clientef.getFechaAlta()));
        String path = clientef.getImagen();

        if(path!= null) {
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            //para ajustarlo al imageviewer
            bitmap = Bitmap.createScaledBitmap(bitmap, 400, 400, false);
            img_user.setImageBitmap(bitmap);
        }

    }

    private void setEdicion(boolean opcion)
    {
        nombre.setEnabled(opcion);
        apellidos.setEnabled(opcion);
        telefono.setEnabled(opcion);
        email.setEnabled(opcion);
        empresa.setEnabled(opcion);
        fecha_alta.setEnabled(opcion);
        direccion.setEnabled(opcion);
        img_user.setEnabled(opcion);
        btn_photo.setEnabled(opcion);
        btn_aceptar.setEnabled(opcion);
        btn_cancelar.setEnabled(opcion);

        if(this.modo==C_EDITAR){
            img_user.setEnabled(true);
            direccion.setEnabled(true);
            email.setEnabled(true);
            btn_photo.setEnabled(true);
            btn_aceptar.setEnabled(true);
            btn_cancelar.setEnabled(true);
    if(clientef.getImagen()!=null)mImageCaptureUri = Uri.parse(clientef.getImagen());
        }
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
            this.setEdicion(false);
        }

    }

    private void guardar()
    {

        final Cliente clientef ;

        if (this.modo == SATManagerActivity.C_EDITAR)
        {
            clientef = Cliente.find(getActivity(), this.id);
        }
        else
        {
            clientef = new Cliente(getActivity()) ;
        }


            clientef.setTelefono(Integer.valueOf(telefono.getText().toString()));
            clientef.setEmail(email.getText().toString());
            clientef.setFechaAlta(fecha_alta.getText().toString());
            clientef.setImagen(mImageCaptureUri.getPath());
            clientef.setEmpresa(empresa.getText().toString());            
            clientef.setApellidos(apellidos.getText().toString());
            clientef.setNombre(nombre.getText().toString());
            clientef.setDireccion(direccion.getText().toString());




    if (modo == C_CREAR)
        {
            AlertDialog.Builder dialogCrearCliente = new AlertDialog.Builder(getActivity());

            dialogCrearCliente.setIcon(android.R.drawable.ic_dialog_alert);
            dialogCrearCliente.setTitle(getResources().getString(R.string.titulo_home_clientes));
            dialogCrearCliente.setMessage(getResources().getString(R.string.msg_conf_cliente_nuevo));
            dialogCrearCliente.setCancelable(false);

            dialogCrearCliente.setPositiveButton(getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int boton) {
                    if(clientef.save()!=0){
                        Toast.makeText(getActivity(),getResources().getString(R.string.msg_cliente_nuevo_guardado), Toast.LENGTH_LONG).show();
                        getActivity().setResult(Activity.RESULT_OK);
                    }
                    else{
                        getActivity().setResult(Activity.RESULT_CANCELED);
                        Toast.makeText(getActivity(),getResources().getString(R.string.msg_error_guardar_cliente), Toast.LENGTH_LONG).show();
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
            dialogCrearCliente.setTitle(getResources().getString(R.string.titulo_home_clientes));
            dialogCrearCliente.setMessage(getResources().getString(R.string.msg_conf_editar_cliente));
            dialogCrearCliente.setCancelable(false);

            dialogCrearCliente.setPositiveButton(getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int boton) {
                    if(clientef.save()==0){
                        Toast.makeText(getActivity(),getResources().getString(R.string.msg_no_posible_actualizar_cliente), Toast.LENGTH_LONG).show();
                        getActivity().setResult(Activity.RESULT_CANCELED);
                    }else {
                        getActivity().setResult(Activity.RESULT_OK);
                        Toast.makeText(getActivity(),getResources().getString(R.string.msg_exito_actualizar_cliente), Toast.LENGTH_LONG).show();
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

    private void borrar()
    {
        /**
         * Borramos el registro con confirmación
         */
        AlertDialog.Builder dialogEliminar = new AlertDialog.Builder(getActivity());

        dialogEliminar.setIcon(android.R.drawable.ic_dialog_alert);
        dialogEliminar.setTitle(getResources().getString(R.string.cliente_eliminar_titulo));
        dialogEliminar.setMessage(getResources().getString(R.string.cliente_eliminar_mensaje));
        dialogEliminar.setCancelable(false);

        dialogEliminar.setPositiveButton(getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int boton) {
                if(clientef.delete()>0) {
                    Toast.makeText(ClientsFragmentForm.this.getActivity(), R.string.cliente_eliminar_confirmacion, Toast.LENGTH_SHORT).show();
                    getActivity().setResult(Activity.RESULT_OK);
                    if(mListener!=null){
                        mListener.onDeleteSelectedCliente(0);
                    }
                    resetear();
                    establecerModo(C_CREAR);
                }


            }
        });

        dialogEliminar.setNegativeButton(android.R.string.no, null);
        dialogEliminar.show();

    }

    private void resetear() {
        nombre.setText("");
        apellidos.setText("");
        telefono.setText("");
        email.setText("");
        empresa.setText("");
        direccion.setText("");
        fecha_alta.setText("");
        img_user.setImageBitmap(null);
        img_user.setImageResource(R.drawable.ic_action_user);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        if(this.modo == C_VISUALIZAR)inflater.inflate(R.menu.menu_form_clientes, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_eliminar:
                borrar();
                break;
            case R.id.action_actualizar:
                  mListener.FormClientFragmentInteraction(clientef.getId());
        }
        return super.onOptionsItemSelected(item);
    }

    public interface FormClientFragmentInteractionListener {
        public void FormClientFragmentInteraction(long selected);
        public void onDeleteSelectedCliente(long id);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (FormClientFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + activity+getString(R.string.callback_listener_excep));
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     *
     * @param pBitmap
     * @throws IOException
     */
    public void convertToFile(Bitmap pBitmap) throws IOException {

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        pBitmap.compress(Bitmap.CompressFormat.PNG, 40, bytes);

        String nomCliente = nombre.getText()!=null?nombre.getText().toString():"cliente";
        File dir = new File(Environment.getExternalStorageDirectory(),
                "Android/data/"+ Contract.PACKET+"/");
        dir.mkdir();
        if(dir.isDirectory()) {
            File file = new File(Environment.getExternalStorageDirectory(),
                    "Android/data/"+ Contract.PACKET+"/"+nomCliente+"_"+String.valueOf(System.currentTimeMillis())+".png");
            file.createNewFile();
            //write bytes in file

            OutputStream outStream = new FileOutputStream(file);
            outStream.write(bytes.toByteArray());

            //Close writer
            outStream.close();

            mImageCaptureUri = Uri.fromFile(file);
        }
    }
}
