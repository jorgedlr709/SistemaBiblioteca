package modelo.usuarios;

public class Profesor extends Usuario {

    public Profesor(int id, String nombre) {
        super(id, nombre);
    }

    @Override
    public double calcularMulta(int diasRetraso) {
        // Multa menor para profesores: 0.25€ por día de retraso
        return diasRetraso * 0.25;
    }
}

