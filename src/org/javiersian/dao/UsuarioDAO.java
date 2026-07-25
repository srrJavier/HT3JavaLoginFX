
package org.javiersian.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.javiersian.model.Usuario;
import org.javiersian.util.Conexion;


public class UsuarioDAO {    
    public Usuario iniciarSesion(String usernarme, String passwordHash){                
        Usuario usuario = null;
        String sql = "{call sp_iniciar_sesion(?,?)}";
        
        try (Connection conexion = Conexion.getInstancia().conectar();
                CallableStatement consulta = conexion.prepareCall(sql)){
            
            consulta.setString(1, usernarme);
            consulta.setString(2, passwordHash);
            
            try(ResultSet tablaResultado = consulta.executeQuery()){
                if (tablaResultado.next()) {
                    usuario = new Usuario();
                    usuario.setId(tablaResultado.getInt(1));
                    usuario.setUsername(tablaResultado.getString(2));
                    usuario.setRol(tablaResultado.getString(3));
                }
            }            
        } catch (SQLException e) {
            System.err.println("Error en Iniciar Sesion: " + e.getMessage());                   
        }
        
        return usuario;
    }
    
    public boolean registrarUsuario(String username, String passwordHash, String rol){
        
        return false;
    }
    
}
