package das.practica5.spolex.clientsManager.main.adapters;

/**
 * Created by spolex on 8/03/15.
 *
 * Clase que facilita la gestión del nav menú, se gestionan las imagenes y los títulos de los items
 *
 */
public class Item_nav {
    private String mTitulo;
    private int mIcono;

    public Item_nav(String titulo, int icono){

        this.mTitulo=titulo;
        this.mIcono = icono;

    }

    public String getTitulo() {
        return mTitulo;
    }

    public void setTitulo(String mTitulo) {
        this.mTitulo = mTitulo;
    }

    public int getIcono() {
        return mIcono;
    }

    public void setIcono(int mIcono) {
        this.mIcono = mIcono;
    }
}
