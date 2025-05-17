package util;

import interfaz.ConsolaBiblioteca;
import modelo.Biblioteca;

public class Main {
    public static void main(String[] args) {
        Biblioteca biblioteca = Biblioteca.getInstancia();


        ConsolaBiblioteca consola = new ConsolaBiblioteca(biblioteca);
        consola.iniciar();

    }
}

