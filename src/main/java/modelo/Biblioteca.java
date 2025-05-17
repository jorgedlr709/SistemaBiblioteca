package modelo.biblioteca;

import modelo.libros.Libro;
import modelo.usuarios.Usuario;
import modelo.usuarios.Estudiante;
import modelo.usuarios.Profesor;
import excepciones.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class Biblioteca implements Serializable {

    private static Biblioteca instancia;

    private Map<String, Libro> libros;
    private Map<Integer, Usuario> usuarios;
    private Map<Integer, List<Prestamo>> prestamos; // Usuarios con lista de préstamos

    private Biblioteca() {
        libros = new HashMap<>();
        usuarios = new HashMap<>();
        prestamos = new HashMap<>();
    }

    public static Biblioteca getInstancia() {
        if (instancia == null) {
            instancia = new Biblioteca();
        }
        return instancia;
    }

    // Clase interna para manejar préstamos
    public static class Prestamo {
        private String isbnLibro;
        private LocalDate fechaPrestamo;

        public Prestamo(String isbnLibro) {
            this.isbnLibro = isbnLibro;
            this.fechaPrestamo = LocalDate.now();
        }

        public String getIsbnLibro() {
            return isbnLibro;
        }

        public LocalDate getFechaPrestamo() {
            return fechaPrestamo;
        }
    }

    // Registro libro con validación de ISBN
    public void registrarLibro(Libro libro) throws BibliotecaException {
        if (!validarISBN(libro.getIsbn())) {
            throw new ISBNException("ISBN inválido: " + libro.getIsbn());
        }
        if (libros.containsKey(libro.getIsbn())) {
            throw new BibliotecaException("El libro con ISBN " + libro.getIsbn() + " ya está registrado.");
        }
        libros.put(libro.getIsbn(), libro);
    }

    // Buscar libro por ISBN
    public Libro buscarLibroPorIsbn(String isbn) throws BibliotecaException {
        if (!libros.containsKey(isbn)) {
            throw new BibliotecaException("Libro no encontrado con ISBN: " + isbn);
        }
        return libros.get(isbn);
    }

    // Registro usuario
    public void registrarUsuario(Usuario usuario) throws BibliotecaException {
        if (usuarios.containsKey(usuario.getId())) {
            throw new BibliotecaException("El usuario con ID " + usuario.getId() + " ya está registrado.");
        }
        usuarios.put(usuario.getId(), usuario);
    }

    // Buscar usuario por ID
    public Usuario buscarUsuarioPorId(int id) throws BibliotecaException {
        if (!usuarios.containsKey(id)) {
            throw new BibliotecaException("Usuario no encontrado con ID: " + id);
        }
        return usuarios.get(id);
    }

    // Realizar préstamo
    public void realizarPrestamo(int idUsuario, String isbnLibro) throws BibliotecaException {
        Usuario usuario = buscarUsuarioPorId(idUsuario);
        Libro libro = buscarLibroPorIsbn(isbnLibro);

        if (!libro.isDisponible()) {
            throw new PrestamoException("El libro no está disponible para préstamo.");
        }

        List<Prestamo> prestamosUsuario = prestamos.getOrDefault(idUsuario, new ArrayList<>());

        int limite = (usuario instanceof Estudiante) ? 3 : 5;

        if (prestamosUsuario.size() >= limite) {
            if (usuario instanceof Profesor) {
                throw new ProfesorNoPuedePrestarException("Profesor ha superado el límite de 5 libros.");
            } else {
                throw new PrestamoException("Usuario ha superado el límite de préstamos.");
            }
        }

        prestamosUsuario.add(new Prestamo(isbnLibro));
        prestamos.put(idUsuario, prestamosUsuario);

        libro.setDisponible(false);
    }

    // Devolver libro y calcular multa
    public double devolverLibro(int idUsuario, String isbnLibro) throws BibliotecaException {
        Usuario usuario = buscarUsuarioPorId(idUsuario);
        Libro libro = buscarLibroPorIsbn(isbnLibro);

        List<Prestamo> prestamosUsuario = prestamos.get(idUsuario);
        if (prestamosUsuario == null) {
            throw new PrestamoException("El usuario no tiene préstamos registrados.");
        }

        Prestamo prestamoEncontrado = null;
        for (Prestamo p : prestamosUsuario) {
            if (p.getIsbnLibro().equals(isbnLibro)) {
                prestamoEncontrado = p;
                break;
            }
        }

        if (prestamoEncontrado == null) {
            throw new PrestamoException("El usuario no tiene este libro prestado.");
        }

        long diasPrestamo = ChronoUnit.DAYS.between(prestamoEncontrado.getFechaPrestamo(), LocalDate.now());
        long diasRetraso = diasPrestamo > 14 ? diasPrestamo - 14 : 0;

        double multa = usuario.calcularMulta((int)diasRetraso);

        libro.setDisponible(true);
        prestamosUsuario.remove(prestamoEncontrado);

        return multa;
    }

    private boolean validarISBN(String isbn) {
        return isbn != null && isbn.matches("\\d{13}");
    }
}
