package modelo;

import modelo.libros.Libro;
import modelo.usuarios.Usuario;
import modelo.usuarios.Estudiante;
import modelo.usuarios.Profesor;
import persistencia.UsuarioDAO;
import persistencia.LibroDAO;
import persistencia.PrestamosDAO;
import excepciones.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.sql.SQLException;

public class Biblioteca implements Serializable {

    private static Biblioteca instancia;

    private final Map<String, Libro> libros;
    private final Map<Integer, Usuario> usuarios;
    private final Map<Integer, List<Prestamo>> prestamos;

    private final UsuarioDAO usuarioDAO;
    private final LibroDAO libroDAO;
    private final PrestamosDAO prestamosDAO;

    private Biblioteca() {
        libros = new HashMap<>();
        usuarios = new HashMap<>();
        prestamos = new HashMap<>();
        usuarioDAO = new UsuarioDAO();
        libroDAO = new LibroDAO();
        prestamosDAO = new PrestamosDAO();

        // Carga usuarios desde la base de datos
        try {
            List<Usuario> listaUsuarios = usuarioDAO.listarUsuarios();
            for (Usuario u : listaUsuarios) {
                usuarios.put(u.getId(), u);
            }
        } catch (Exception e) {
            System.err.println("Error cargando usuarios desde BD: " + e.getMessage());
        }

        // Carga libros desde la base de datos
        try {
            List<Libro> listaLibros = libroDAO.obtenerTodosLosLibros();
            for (Libro l : listaLibros) {
                libros.put(l.getIsbn(), l);
            }
        } catch (SQLException e) {
            System.err.println("Error cargando libros desde BD: " + e.getMessage());
        }

        // Carga préstamos desde la base de datos
        try {
            for (Integer idUsuario : usuarios.keySet()) {
                List<Prestamo> prestamosUsuario = prestamosDAO.obtenerPrestamosPorUsuario(idUsuario);
                prestamos.put(idUsuario, prestamosUsuario);

                // Marcar libros como no disponibles si están prestados
                for (Prestamo p : prestamosUsuario) {
                    Libro libroPrestado = libros.get(p.getIsbnLibro());
                    if (libroPrestado != null) {
                        libroPrestado.setDisponible(false);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error cargando préstamos desde BD: " + e.getMessage());
        }
    }

    public static Biblioteca getInstancia() {
        if (instancia == null) {
            instancia = new Biblioteca();
        }
        return instancia;
    }

    public Map<String, Libro> getLibros() {
        return libros;
    }

    public Map<Integer, Usuario> getUsuarios() {
        return usuarios;
    }

    public static class Prestamo {
        private final String isbnLibro;
        private final LocalDate fechaPrestamo;

        public Prestamo(String isbnLibro) {
            this.isbnLibro = isbnLibro;
            this.fechaPrestamo = LocalDate.now();
        }

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

    public void registrarLibro(Libro libro) throws BibliotecaException {
        if (!validarISBN(libro.getIsbn())) {
            throw new ISBNException("ISBN inválido: " + libro.getIsbn());
        }
        if (libros.containsKey(libro.getIsbn())) {
            throw new BibliotecaException("El libro con ISBN " + libro.getIsbn() + " ya está registrado.");
        }
        try {
            libroDAO.insertarLibro(libro); // Guarda en BD
            libros.put(libro.getIsbn(), libro); // Guarda en memoria
        } catch (SQLException e) {
            throw new BibliotecaException("Error al guardar libro: " + e.getMessage());
        }
    }

    public Libro buscarLibroPorIsbn(String isbn) throws BibliotecaException {
        if (!libros.containsKey(isbn)) {
            throw new BibliotecaException("Libro no encontrado con ISBN: " + isbn);
        }
        return libros.get(isbn);
    }

    public void registrarUsuario(Usuario usuario) throws BibliotecaException {
        if (usuarios.containsKey(usuario.getId())) {
            throw new BibliotecaException("El usuario con ID " + usuario.getId() + " ya está registrado.");
        }
        try {
            usuarioDAO.guardarUsuario(usuario);
            usuarios.put(usuario.getId(), usuario);
        } catch (Exception e) {
            throw new BibliotecaException("Error al registrar usuario: " + e.getMessage());
        }
    }

    public Usuario buscarUsuarioPorId(int id) throws BibliotecaException {
        if (!usuarios.containsKey(id)) {
            throw new BibliotecaException("Usuario no encontrado con ID: " + id);
        }
        return usuarios.get(id);
    }

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

        Prestamo nuevoPrestamo = new Prestamo(isbnLibro);
        prestamosUsuario.add(nuevoPrestamo);
        prestamos.put(idUsuario, prestamosUsuario);

        // Persistir préstamo en BD
        try {
            prestamosDAO.insertarPrestamo(idUsuario, isbnLibro, nuevoPrestamo.getFechaPrestamo());
        } catch (SQLException e) {
            throw new BibliotecaException("Error al guardar préstamo en BD: " + e.getMessage());
        }

        libro.setDisponible(false);
    }

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

        double multa = usuario.calcularMulta((int) diasRetraso);

        libro.setDisponible(true);
        prestamosUsuario.remove(prestamoEncontrado);

        // Eliminar préstamo de BD
        try {
            prestamosDAO.eliminarPrestamo(idUsuario, isbnLibro);
        } catch (SQLException e) {
            throw new BibliotecaException("Error al eliminar préstamo en BD: " + e.getMessage());
        }

        return multa;
    }

    private boolean validarISBN(String isbn) {
        return isbn != null && isbn.matches("\\d{13}");
    }
}








