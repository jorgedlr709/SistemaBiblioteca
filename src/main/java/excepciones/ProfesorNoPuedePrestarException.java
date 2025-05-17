package excepciones;

public class ProfesorNoPuedePrestarException extends PrestamoException {
    public ProfesorNoPuedePrestarException(String mensaje) {
        super(mensaje);
    }
}
