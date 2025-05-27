package main.java.com.restaurante.app.models;

public class Usuario {
    private int id;
    private String nombre;
    private String rol;
    private String correo;
    private String contrasena;

    public Usuario() {
    }

    public Usuario(int id, String nombre, String rol, String correo, String contrasena) {
        this.id = id;
        this.nombre = nombre;
        this.rol = rol;
        this.correo = correo;
        this.contrasena = contrasena;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }
}
