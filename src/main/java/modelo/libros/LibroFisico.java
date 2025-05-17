// Clase LibroFisico
package modelo.libros;

public class LibroFisico extends Libro {
    private String ubicacionEstante;

    // Constructor con disponibilidad explícita
    public LibroFisico(String isbn, String titulo, String autor, boolean disponible, String ubicacionEstante) {
        super(isbn, titulo, autor, disponible);
        this.ubicacionEstante = ubicacionEstante;
    }

    // Constructor por defecto con disponible = true
    public LibroFisico(String isbn, String titulo, String autor, String ubicacionEstante) {
        this(isbn, titulo, autor, true, ubicacionEstante);
    }

    public String getUbicacionEstante() {
        return ubicacionEstante;
    }

    public void setUbicacionEstante(String ubicacionEstante) {
        this.ubicacionEstante = ubicacionEstante;
    }

    @Override
    public String getTipo() {
        return "Libro Físico";
    }

    @Override
    public String toString() {
        return super.toString() + ", Ubicación estante: " + ubicacionEstante;
    }
}




