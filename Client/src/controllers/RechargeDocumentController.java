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
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;

public class RechargeDocumentController implements Initializable {

    @FXML
    private TextField creditCardField;
    @FXML
    private TextField passwordField;
    @FXML
    private TextField pointField;
    @FXML
    private Button addBtn;
    @FXML
    private Button cancelBtn;

    private ServerConnection serverConnection;
   
    public RechargeDocumentController(ServerConnection serverConnection) {
        this.serverConnection = serverConnection;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
              UnaryOperator<Change> filter = change -> {
            if (change.getControlNewText().length() > 16 || !change.getControlNewText().matches("\\d*")) {
                return null;
            }
            return change;
        };
        creditCardField.setTextFormatter(new TextFormatter<>(filter));  
    }

   
    @FXML
    private void handleAddAction(ActionEvent event) throws IOException {
       
        String creditCard = creditCardField.getText().trim();
        String inputPassword = passwordField.getText().trim();
        String pointText = pointField.getText().trim();

        if (creditCard.isEmpty() || inputPassword.isEmpty() || pointText.isEmpty()) {
            Utils.showAlert(Alert.AlertType.ERROR, "Validation Error", "Please fill in all fields.");
            return;
        }

        double pointsToAdd;
        try {
            pointsToAdd = Double.parseDouble(pointText);
        } catch (NumberFormatException ex) {
            Utils.showAlert(Alert.AlertType.ERROR, "Validation Error", "Please enter a valid number for points.");
            return;
        }


        JsonObject rechargePayload = new JsonObject();
        rechargePayload.addProperty("points", pointsToAdd);
        rechargePayload.addProperty("creditCard", creditCard);
        JsonObject rechargeResponse = serverConnection.sendRequest("recharge", rechargePayload);

        if (rechargeResponse != null && rechargeResponse.has("Result")
                && rechargeResponse.get("Result").getAsString().equals("succeed")) {
            Utils.showAlert(Alert.AlertType.INFORMATION, "Recharge Successful", "Your account has been recharged successfully!");
            creditCardField.clear();
            passwordField.clear();
            pointField.clear();
        } else {
            Utils.showAlert(Alert.AlertType.ERROR, "Recharge Failed", "Recharge failed. Please try again.");
        }
    }

    
    @FXML
    private void handleCancelAction(ActionEvent event) throws IOException {
        
        LoadScenes.loadHomeScene();

    }

    
}