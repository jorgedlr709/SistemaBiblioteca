package persistencia;

import modelo.libros.Libro;
import modelo.libros.LibroFisico;
import modelo.libros.LibroDigital;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LibroDAO {

    public void insertarLibro(Libro libro) throws SQLException {
        String sql = "INSERT INTO libros (isbn, titulo, autor, tipo_libro, es_digital, ubicacion_estante, url_descarga, disponible) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, libro.getIsbn());
            ps.setString(2, libro.getTitulo());
            ps.setString(3, libro.getAutor());
            ps.setString(4, libro.getTipo());  // tipo_libro

            if (libro instanceof LibroFisico) {
                LibroFisico lf = (LibroFisico) libro;
                ps.setBoolean(5, false);
                ps.setString(6, lf.getUbicacionEstante());
                ps.setNull(7, Types.VARCHAR);
            } else if (libro instanceof LibroDigital) {
                LibroDigital ld = (LibroDigital) libro;
                ps.setBoolean(5, true);
                ps.setNull(6, Types.VARCHAR);
                ps.setString(7, ld.getUrl());
            } else {
                ps.setBoolean(5, false);
                ps.setNull(6, Types.VARCHAR);
                ps.setNull(7, Types.VARCHAR);
            }

            ps.setBoolean(8, libro.isDisponible());

            ps.executeUpdate();
        }
    }



    public List<Libro> obtenerTodosLosLibros() throws SQLException {
        List<Libro> lista = new ArrayList<>();
        String sql = "SELECT * FROM libros";
        try (Connection conn = ConexionBD.obtenerConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String isbn = rs.getString("isbn");
                String titulo = rs.getString("titulo");
                String autor = rs.getString("autor");
                boolean esDigital = rs.getBoolean("es_digital");
                boolean disponible = rs.getBoolean("disponible");

                if (esDigital) {
                    String urlDescarga = rs.getString("url_descarga");
                    LibroDigital ld = new LibroDigital(isbn, titulo, autor, disponible, urlDescarga);
                    lista.add(ld);
                } else {
                    String ubicacion = rs.getString("ubicacion_estante");
                    LibroFisico lf = new LibroFisico(isbn, titulo, autor, disponible, ubicacion);
                    lista.add(lf);
                }
            }
        }
        return lista;
    }




}


