/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package signup_page;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author Ahmed
 */
public class FXMLDocumentController implements Initializable {

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
    private TextField tfGender;
    @FXML
    private TextField tfDOB;
    @FXML
    private Button btSignup;
    @FXML
    private Button btCancel;

    /**
     * Initializes the controller class.
     */
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
