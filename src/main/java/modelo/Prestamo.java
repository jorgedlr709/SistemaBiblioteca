package modelo;

import java.io.Serializable;
import java.time.LocalDate;

public class Prestamo implements Serializable {
    private String isbnLibro;
    private LocalDate fechaPrestamo;

    // Constructor que asigna la fecha actual
    public Prestamo(String isbnLibro) {
        this.isbnLibro = isbnLibro;
        this.fechaPrestamo = LocalDate.now();
    }

    // Constructor con fecha específica
    public Prestamo(String isbnLibro, LocalDate fechaPrestamo) {
        this.isbnLibro = isbnLibro;
        this.fechaPrestamo = fechaPrestamo;
    }

    public String getIsbnLibro() {
        return isbnLibro;
    }

    public LocalDate getFechaPrestamo() {
        return fechaPrestamo;
    }
}

