/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import client.LoadScenes;
import client.ServerConnection;
import client.Utils;
import com.google.gson.JsonObject;
import dto.LoginDTO;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;

/**
 *
 * @author Ahmed
 */
public class UpdateProfileDocumentController implements Initializable {

    @FXML
    private PasswordField oldPasswordField;
    @FXML
    private PasswordField newPasswordField;
    @FXML
    private PasswordField retypePasswordField;
    @FXML
    private Button changeButton;
    @FXML
    private Button cancelButton;

    private final ServerConnection serverConnection;

    public UpdateProfileDocumentController(ServerConnection serverConnection) {
        this.serverConnection = serverConnection;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    private void handleChangeButton(ActionEvent event) {
        String oldPassword = oldPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String retypePassword = retypePasswordField.getText();

        if (oldPassword.isEmpty() || newPassword.isEmpty() || retypePassword.isEmpty()) {
            Utils.showAlert(Alert.AlertType.ERROR, "Error", "All fields must be filled.");
            return;
        }

        if (!newPassword.equals(retypePassword)) {
            Utils.showAlert(Alert.AlertType.ERROR, "Error", "New passwords do not match.");
            return;
        }

        // Send request to server
        JsonObject updatePassword = new JsonObject();
        updatePassword.addProperty("oldPassword", oldPassword);
        updatePassword.addProperty("newPassword", newPassword);
        JsonObject jsonResponse = serverConnection.sendRequest("updatePassword", updatePassword);
        String result = jsonResponse.get("Result").getAsString();

        if (result.equals("succeed")) {
            Utils.showAlert(Alert.AlertType.INFORMATION, "Success", "Password updated successfully.");
        } else if (result.equals("failed")) {
            Utils.showAlert(Alert.AlertType.ERROR, "Error", "Failed to update password. Please try again.");
        } else {
            Utils.showAlert(Alert.AlertType.ERROR, "Error", result);
        }
    }

    @FXML
    private void handleCancelAction(ActionEvent event) throws IOException {
        LoadScenes.loadHomeScene();
    }
}
