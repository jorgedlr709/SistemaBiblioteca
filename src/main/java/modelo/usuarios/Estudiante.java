package modelo.usuarios;

public class Estudiante extends Usuario {

    public Estudiante(int id, String nombre) {
        super(id, nombre);
    }

    @Override
    public double calcularMulta(int diasRetraso) {
        // Multa: 0.50€ por día de retraso
        return diasRetraso * 0.5;
    }
}
