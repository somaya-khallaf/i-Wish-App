package controllers;

import client.LoadScenes;
import client.ServerConnection;
import client.Utils;
import com.google.gson.JsonObject;
import dto.UserDTO;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.DatePicker;
import java.time.LocalDate;
import java.sql.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.ComboBox;

public class SignupDocumentController implements Initializable {

    @FXML
    private TextField tfFullName;
    @FXML
    private TextField tfUsername;
    @FXML
    private TextField tfPassword;
    @FXML
    private TextField tfCfPassword;
    @FXML
    private TextField tfPhone;
    @FXML
    private ComboBox cbGender;
    @FXML
    private DatePicker tfDOB;

    private ServerConnection serverConnection;

    public SignupDocumentController(ServerConnection serverConnection) {
        this.serverConnection = serverConnection;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
         cbGender.getItems().addAll("Male", "Female"); 
    }

    @FXML
    private void handleSignupButton(ActionEvent event) {

        
            String fullName = tfFullName.getText();
            String username = tfUsername.getText();
            String password = tfPassword.getText();
            String confirmPassword = tfCfPassword.getText();
            String phone = tfPhone.getText().trim();
            String gender = (String) cbGender.getValue();
            LocalDate dob = tfDOB.getValue();
            if (fullName.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()
                    || phone.isEmpty() || gender.isEmpty()) {
                Utils.showAlert(AlertType.ERROR, "Validation Error", "All fields must be filled.");
                return;
            }
            
            if (!password.equals(confirmPassword)) {
                Utils.showAlert(AlertType.ERROR, "Validation Error", "Passwords do not match.");
                return;
            }
            
            UserDTO user = new UserDTO(username, fullName, password, gender, phone, 0.0f, Date.valueOf(dob));
            JsonObject jsonResponse = serverConnection.sendRequest("register", user);
            if (jsonResponse.get("Result").getAsString().equals("success")) {
                Utils.showAlert(AlertType.INFORMATION, "Success", "User registered successfully.");
                try {
                    LoadScenes.loadLoginScene();
                } catch (IOException ex) {
                    Logger.getLogger(SignupDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                }

            } else {
                
                Utils.showAlert(AlertType.ERROR, "Registration Failed", jsonResponse.get("Result").getAsString());
            }
        


    }

    @FXML
    private void handleCancelButton(ActionEvent event) {
        try {
            LoadScenes.loadLoginScene();
        } catch (IOException ex) {
            Logger.getLogger(SignupDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
