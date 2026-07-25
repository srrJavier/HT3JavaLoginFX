
package org.javiersian.controller;



import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.javiersian.dao.UsuarioDAO;
import org.javiersian.model.Usuario;
import org.javiersian.util.SecurityUtil;

public class InicioSesionController implements Initializable {

    @FXML
    private TextField txtUsuario;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private Button btnIniciarSesion;
    @FXML
    private Label lblMensaje;

    private UsuarioDAO usuarioDAO;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        usuarioDAO = new UsuarioDAO();
        lblMensaje.setText("");

    }

    @FXML
    public void eventoInicioSesion(ActionEvent evento) {
        String usuario = txtUsuario.getText();
        String password = txtPassword.getText();

        if (usuario.isEmpty() || password.isEmpty()) {
            lblMensaje.setText("Por favor, complete todos sus datos.");
            return;
        }
        String passwordHash = SecurityUtil.hashSHA256(password);
        Usuario usuarioIniciado = usuarioDAO.iniciarSesion(usuario, passwordHash);
        if (usuarioIniciado != null) {
            lblMensaje.setText("Inicio correcto");
            abrirDashboard(usuarioIniciado);
        } else {
            lblMensaje.setText("Usuario o contraseña incorrectos");
        }
    }

    private void abrirDashboard(Usuario usuario) {
        
    String rutaFXML;
    String tituloDashboard;

    switch (usuario.getRol().toLowerCase()) {

        case "admin":
            rutaFXML = "/org/ds/view/AdminDashboardView.fxml";
            tituloDashboard = "Panel de Administración";
            break;

        case "empleado":
            rutaFXML = "/org/ds/view/EmpleadoDashboardView.fxml";
            tituloDashboard = "Panel de Empleado";
            break;

        default:
            lblMensaje.setStyle("-fx-text-fill: red;");
            lblMensaje.setText("El rol del usuario no es válido.");
            return;
        }

        try {

            URL archivoFXML = getClass().getResource(rutaFXML);

            if (archivoFXML == null) {
                lblMensaje.setStyle("-fx-text-fill: red;");
                lblMensaje.setText(
                        "No se encontró la vista: " + rutaFXML
                );
                return;
            }

            FXMLLoader cargadorFXML
                    = new FXMLLoader(archivoFXML);

            Parent raiz = cargadorFXML.load();

            switch (usuario.getRol().toLowerCase()) {

                case "admin":
                    AdminDashboradController adminController
                            = cargadorFXML.getController();

                    adminController.iniciarUsuario(usuario);
                    break;

                case "empleado":
                    EmpleadoDashboardController empleadoController
                            = cargadorFXML.getController();

                    empleadoController.iniciarUsuario(usuario);
                    break;
            }

            Stage escenario = new Stage();

            escenario.setScene(new Scene(raiz));
            escenario.setTitle(tituloDashboard);
            escenario.show();

            Stage escenaActual
                    = (Stage) btnIniciarSesion
                            .getScene()
                            .getWindow();

            escenaActual.close();

        } catch (IOException e) {

            System.err.println(
                    "Error al cargar la vista: "
                    + rutaFXML
                    + " - "
                    + e.getMessage()
            );

            e.printStackTrace();

            lblMensaje.setStyle("-fx-text-fill: red;");
            lblMensaje.setText(
                    "Error interno al cargar el panel."
            );
        }
    }
}
