package persistencia;



import modelo.Biblioteca.Prestamo;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PrestamosDAO {

    public void insertarPrestamo(int idUsuario, String isbnLibro, LocalDate fechaPrestamo) throws SQLException {
        String sql = "INSERT INTO prestamos (id_usuario, isbn_libro, fecha_prestamo) VALUES (?, ?, ?)";
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ps.setString(2, isbnLibro);
            ps.setDate(3, Date.valueOf(fechaPrestamo));
            ps.executeUpdate();
        }
    }

    public void eliminarPrestamo(int idUsuario, String isbnLibro) throws SQLException {
        String sql = "DELETE FROM prestamos WHERE id_usuario = ? AND isbn_libro = ?";
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ps.setString(2, isbnLibro);
            ps.executeUpdate();
        }
    }

    public List<Prestamo> obtenerPrestamosPorUsuario(int idUsuario) throws SQLException {
        String sql = "SELECT isbn_libro, fecha_prestamo FROM prestamos WHERE id_usuario = ?";
        List<Prestamo> prestamos = new ArrayList<>();
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String isbn = rs.getString("isbn_libro");
                    LocalDate fecha = rs.getDate("fecha_prestamo").toLocalDate();
                    prestamos.add(new Prestamo(isbn, fecha));
                }
            }
        }
        return prestamos;
    }
}

