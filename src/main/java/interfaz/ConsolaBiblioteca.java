package interfaz;

import modelo.Biblioteca;
import modelo.libros.Libro;
import modelo.libros.LibroFisico;
import modelo.libros.LibroDigital;
import modelo.usuarios.Estudiante;
import modelo.usuarios.Profesor;
import modelo.usuarios.Usuario;
import excepciones.BibliotecaException;

import java.util.Scanner;

public class ConsolaBiblioteca {

    private Biblioteca biblioteca;
    private Scanner scanner;

    public ConsolaBiblioteca(Biblioteca biblioteca) {
        this.biblioteca = biblioteca;
        this.scanner = new Scanner(System.in);
    }

    public void iniciar() {
        int opcion = -1;

        while (opcion != 0) {
            System.out.println("\n--- Sistema Biblioteca ---");
            System.out.println("1. Registrar libro");
            System.out.println("2. Registrar usuario");
            System.out.println("3. Realizar préstamo");
            System.out.println("4. Devolver libro");
            System.out.println("0. Salir");
            System.out.print("Elige una opción: ");

            opcion = scanner.nextInt();
            scanner.nextLine(); // limpiar buffer

            try {
                switch (opcion) {
                    case 1 -> registrarLibro();
                    case 2 -> registrarUsuario();
                    case 3 -> realizarPrestamo();
                    case 4 -> devolverLibro();
                    case 0 -> System.out.println("Saliendo...");
                    default -> System.out.println("Opción inválida.");
                }
            } catch (BibliotecaException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void registrarLibro() throws BibliotecaException {
        System.out.print("ISBN (13 dígitos): ");
        String isbn = scanner.nextLine();

        System.out.print("Título: ");
        String titulo = scanner.nextLine();

        System.out.print("Autor: ");
        String autor = scanner.nextLine();

        System.out.print("Tipo (1 = Físico, 2 = Digital): ");
        int tipo = scanner.nextInt();
        scanner.nextLine();

        Libro libro;

        if (tipo == 1) {
            System.out.print("Ubicación estante: ");
            String ubicacion = scanner.nextLine();
            libro = new LibroFisico(isbn, titulo, autor, true, ubicacion);
        } else if (tipo == 2) {
            System.out.print("URL descarga: ");
            String url = scanner.nextLine();
            libro = new LibroDigital(isbn, titulo, autor, true, url);
        } else {
            System.out.println("Tipo inválido.");
            return;
        }

        biblioteca.registrarLibro(libro);
        System.out.println("Libro registrado con éxito.");
    }

    private void registrarUsuario() throws BibliotecaException {
        System.out.print("ID usuario (número): ");
        int id = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Nombre: ");
        String nombre = scanner.nextLine();

        System.out.print("Tipo usuario (1 = Estudiante, 2 = Profesor): ");
        int tipo = scanner.nextInt();
        scanner.nextLine();

        Usuario usuario;

        if (tipo == 1) {
            usuario = new Estudiante(id, nombre);
        } else if (tipo == 2) {
            usuario = new Profesor(id, nombre);
        } else {
            System.out.println("Tipo inválido.");
            return;
        }

        biblioteca.registrarUsuario(usuario);
        System.out.println("Usuario registrado con éxito.");
    }

    private void realizarPrestamo() throws BibliotecaException {
        System.out.print("ID usuario: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        System.out.print("ISBN libro: ");
        String isbn = scanner.nextLine();

        biblioteca.realizarPrestamo(id, isbn);
        System.out.println("Préstamo realizado.");
    }

    private void devolverLibro() throws BibliotecaException {
        System.out.print("ID usuario: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        System.out.print("ISBN libro: ");
        String isbn = scanner.nextLine();

        double multa = biblioteca.devolverLibro(id, isbn);
        if (multa > 0) {
            System.out.println("Libro devuelto. Multa a pagar: €" + multa);
        } else {
            System.out.println("Libro devuelto sin multa.");
        }
    }
}


