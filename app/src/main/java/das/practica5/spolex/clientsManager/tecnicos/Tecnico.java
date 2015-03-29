package das.practica5.spolex.clientsManager.tecnicos;

/**
 * Created by spolex on 29/03/15.
 */
public class Tecnico {

    private String dni;
    private String nombre;
    private String email;
    private Boolean activo;
    private int telefono;

    public Tecnico(String dni, String nombre, String email, int telefono, boolean activo) {
        this.dni = dni;
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
        this.activo = activo;
    }

    public String getDni() {
        return dni;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEmail() {
        return email;
    }

    public Boolean getActivo() {
        return activo;
    }

    public int getTelefono() {
        return telefono;
    }
}
