// Clase LibroDigital
package modelo.libros;

public class LibroDigital extends Libro {
    private String url;

    // Constructor con disponibilidad explícita
    public LibroDigital(String isbn, String titulo, String autor, boolean disponible, String url) {
        super(isbn, titulo, autor, disponible);
        this.url = url;
    }

    // Constructor por defecto con disponible = true
    public LibroDigital(String isbn, String titulo, String autor, String url) {
        this(isbn, titulo, autor, true, url);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String getTipo() {
        return "Libro Digital";
    }

    @Override
    public String toString() {
        return super.toString() + ", URL: " + url;
    }
}




