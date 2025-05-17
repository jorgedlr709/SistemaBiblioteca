package persistencia;

import modelo.usuarios.Usuario;
import modelo.usuarios.Estudiante;
import modelo.usuarios.Profesor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    public void guardarUsuario(Usuario usuario) throws SQLException {
        Connection conn = ConexionBD.obtenerConexion();

        String sqlCheck = "SELECT COUNT(*) FROM usuarios WHERE id = ?";
        PreparedStatement psCheck = conn.prepareStatement(sqlCheck);
        psCheck.setInt(1, usuario.getId());
        ResultSet rs = psCheck.executeQuery();
        rs.next();
        boolean existe = rs.getInt(1) > 0;
        rs.close();
        psCheck.close();

        if (existe) {
            // Actualizar usuario
            String sqlUpdate = "UPDATE usuarios SET nombre = ?, tipo = ? WHERE id = ?";
            PreparedStatement psUpdate = conn.prepareStatement(sqlUpdate);
            psUpdate.setString(1, usuario.getNombre());
            psUpdate.setString(2, usuario instanceof Estudiante ? "estudiante" : "profesor");
            psUpdate.setInt(3, usuario.getId());
            psUpdate.executeUpdate();
            psUpdate.close();

        } else {
            // Insertar usuario
            String sqlInsert = "INSERT INTO usuarios (id, nombre, tipo) VALUES (?, ?, ?)";
            PreparedStatement psInsert = conn.prepareStatement(sqlInsert);
            psInsert.setInt(1, usuario.getId());
            psInsert.setString(2, usuario.getNombre());
            psInsert.setString(3, usuario instanceof Estudiante ? "estudiante" : "profesor");
            psInsert.executeUpdate();
            psInsert.close();
        }
    }

    public Usuario buscarPorId(int id) throws SQLException {
        Connection conn = ConexionBD.obtenerConexion();

        String sql = "SELECT * FROM usuarios WHERE id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();

        Usuario usuario = null;
        if (rs.next()) {
            String nombre = rs.getString("nombre");
            String tipo = rs.getString("tipo");
            if ("estudiante".equalsIgnoreCase(tipo)) {
                usuario = new Estudiante(id, nombre);
            } else if ("profesor".equalsIgnoreCase(tipo)) {
                usuario = new Profesor(id, nombre);
            }
        }
        rs.close();
        ps.close();

        return usuario;
    }

    public List<Usuario> listarUsuarios() throws SQLException {
        Connection conn = ConexionBD.obtenerConexion();

        String sql = "SELECT * FROM usuarios";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        List<Usuario> lista = new ArrayList<>();

        while (rs.next()) {
            int id = rs.getInt("id");
            String nombre = rs.getString("nombre");
            String tipo = rs.getString("tipo");

            if ("estudiante".equalsIgnoreCase(tipo)) {
                lista.add(new Estudiante(id, nombre));
            } else if ("profesor".equalsIgnoreCase(tipo)) {
                lista.add(new Profesor(id, nombre));
            }
        }
        rs.close();
        stmt.close();

        return lista;
    }

    public void eliminarUsuario(int id) throws SQLException {
        Connection conn = ConexionBD.obtenerConexion();

        String sql = "DELETE FROM usuarios WHERE id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();
        ps.close();
    }
}
