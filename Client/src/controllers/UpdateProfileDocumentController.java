/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import client.ServerConnection;
import client.Utils;
import dto.UserDTO;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author HP
 */
public class UpdateProfileDocumentController implements Initializable {

    private ServerConnection serverConnection;
    @FXML
    private Label usernameLable;
    @FXML
    private TextField phoneTextField;
    @FXML
    private Label fullnameLable;
    @FXML
    private TextField fullnameTextField;
    @FXML
    private TextField genderTextField;
    @FXML
    private TextField dobTextField;
    @FXML
    private Button balanceBtn;
    @FXML
    private Button updateBtn;
    @FXML
    private Button backBtn;
    UserDTO userdata;
    @FXML
    private Button passBtn;

    UpdateProfileDocumentController(ServerConnection serverConnection, UserDTO userdata) {
        this.serverConnection = serverConnection;
        this.userdata = userdata;
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        usernameLable.setText(userdata.getUsername());
        phoneTextField.setText(userdata.getPhone());
        fullnameTextField.setText(userdata.getFull_name());
        genderTextField.setText(userdata.getGender());
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        String dob = df.format(userdata.getDob());
        dobTextField.setText(dob);
    }

    @FXML
    private void handleRechargeButton(ActionEvent e) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/RechargeDocument.fxml"));
        RechargeDocumentController fxmlDocumentController = new RechargeDocumentController(serverConnection);
        loader.setController(fxmlDocumentController);
        Utils.moveToAntherScene(e, loader);
    }

    @FXML
    private void handleBackAction(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/HomeDocument.fxml"));
        HomeDocumentController fxmlDocumentController = new HomeDocumentController(serverConnection);
        loader.setController(fxmlDocumentController);
        Utils.moveToAntherScene(event, loader);
    }

    @FXML
    private void handleUpdateButton(ActionEvent e) throws IOException {
        System.out.println("handleUpdateButton");
    }

    @FXML
    private void handleChangePasswdButton(ActionEvent event) {
        System.out.println("handleChangePasswdButton");
    }

}
