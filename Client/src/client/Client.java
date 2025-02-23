/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import javafx.application.Application;
import javafx.stage.Stage;
import static javafx.application.Application.launch;

public class Client extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        LoadScenes loader = LoadScenes.getLoadSceneObj(stage);
        LoadScenes.loadLoginScene();
        stage.setOnCloseRequest(event -> {
        });

    }


    public static void main(String[] args) {
        launch(args);
    }
    
}