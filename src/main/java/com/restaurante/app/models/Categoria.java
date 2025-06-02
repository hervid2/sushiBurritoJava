package main.java.com.restaurante.app.models;

public class Categoria {
    private int categoriaId;
    private String nombre;

    public Categoria() {
        // Constructor vacío
    }

    public Categoria(int categoriaId, String nombre) {
        this.categoriaId = categoriaId;
        this.nombre = nombre;
    }

    // --- Getters y Setters ---

    public int getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(int categoriaId) {
        this.categoriaId = categoriaId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Este método es crucial. Cuando añades objetos Categoria directamente a un JComboBox,
     * este método determina qué texto se mostrará para cada ítem en el desplegable.
     * Si no lo sobrescribes, el JComboBox mostraría la representación de memoria del objeto (ej. "Categoria@abcdef").
     */
    @Override
    public String toString() {
        return nombre; // Queremos que el JComboBox muestre el nombre de la categoría
    }
}