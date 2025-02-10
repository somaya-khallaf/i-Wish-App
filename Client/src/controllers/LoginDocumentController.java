package controllers;

import client.ServerConnection;
import client.Utils;
import com.google.gson.JsonObject;
import controllers.HomeDocumentController;
import controllers.SignupDocumentController;
import dto.*;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class LoginDocumentController implements Initializable {

    @FXML
    private TextField tfUsername;
    @FXML
    private TextField tfPassword;
    @FXML
    private Button btLogin;
    ServerConnection serverConnection;

    public LoginDocumentController() {
    }

    @FXML
    private void handleButtonLogin(ActionEvent e) {
        LoginDTO loginData = new LoginDTO(tfUsername.getText(), tfPassword.getText());
        try {
            JsonObject jsonResponse = serverConnection.sendRequest("login", loginData);
            String result = jsonResponse.get("Result").getAsString();
            if (result.equals("succeed")) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/HomeDocument.fxml"));
                HomeDocumentController fxmlDocumentController = new HomeDocumentController(serverConnection);
                loader.setController(fxmlDocumentController);
                Utils.moveToAntherScene(e, loader);
            } else {
                Utils.showAlert(Alert.AlertType.ERROR, "Login Failed", "Incorrect username or password.");
            }
        } catch (IOException ex) {
            Logger.getLogger(LoginDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void handleButtonSignup(ActionEvent e) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SignupDocument.fxml"));
        SignupDocumentController fxmlDocumentController = new SignupDocumentController(serverConnection);
        loader.setController(fxmlDocumentController);
        Utils.moveToAntherScene(e, loader);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        serverConnection = new ServerConnection();
    }

}
