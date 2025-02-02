/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 *
 * @author Ahmed
 */
public class SignupDocumentController implements Initializable {
    
    private Label label;
    @FXML
    private TextField tfFullName;
    @FXML
    private TextField tfPassword;
    @FXML
    private TextField tfCfPassword;
    @FXML
    private TextField tfPhone;
    @FXML
    private TextField tfGender;
    @FXML
    private TextField tfDOB;
    @FXML
    private Button btSignup;
    @FXML
    private Button btCancel;
    @FXML
    private TextField tfUsername1;
    
    private void handleButtonAction(ActionEvent event) {
        System.out.println("You clicked me!");
        label.setText("Hello World!");
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void handleSignupButton(ActionEvent event) {
    }

    @FXML
    private void handleCancelButton(ActionEvent event) {
    }
    
}
