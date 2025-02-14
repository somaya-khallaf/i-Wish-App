package controllers;

import client.LoadScenes;
import client.ServerConnection;
import client.Utils;
import com.google.gson.JsonObject;
import dto.*;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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

    public LoginDocumentController(ServerConnection serverConnection) {
        this.serverConnection = serverConnection;
    }

    @FXML
    private void handleButtonLogin(ActionEvent e) {
        LoginDTO loginData = new LoginDTO(tfUsername.getText(), tfPassword.getText());
        try {
            JsonObject jsonResponse = serverConnection.sendRequest("login", loginData);
            String result = jsonResponse.get("Result").getAsString();
            if (result.equals("succeed")) {
                LoadScenes.loadHomeScene();
            } else {
                Utils.showAlert(Alert.AlertType.ERROR, "Login Failed", "Incorrect username or password.");
            }
        } catch (IOException ex) {
            Logger.getLogger(LoginDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void handleButtonSignup(ActionEvent e) throws IOException {
        LoadScenes.loadSignupScene();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

}
