/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import dal.DatabaseConnection;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class FXMLDocumentController implements Initializable {

    Thread thread;
    ServerSocket myServerSocket, notificationServerSocket;
    boolean firstRun = false;
    @FXML
    private Button btStart;
    @FXML
    private Button btStop;
    @FXML
    private TextField tfquery;
    @FXML
    private Button btExecute;
    @FXML
    private Button btCommit;
    @FXML
    private TextArea tfresult;
    private static final Connection con = DatabaseConnection.getConnection();
    @FXML
    private Button btRollback;
    private boolean isServerRunning = false;

    @FXML
    public void handleStartAction(ActionEvent event) {
        if (!isServerRunning) {
            LoggerUtil.info("Starting the server...");
            try {
                myServerSocket = new ServerSocket(5005);
                notificationServerSocket = new ServerSocket(5000);
                if (!firstRun) {
                    thread.start();
                } else if (!isServerRunning) {
                    thread.resume();
                }
                firstRun = true;
            } catch (IOException ex) {
                LoggerUtil.error("The server failed to start:" + ex.getMessage());
            }

            isServerRunning = true;
            LoggerUtil.info("The server has started successfully.");
        }

    }

    @FXML
    public void handleStopAction(ActionEvent event) {
        thread.stop();
        isServerRunning = false;
        try {
            if (myServerSocket != null) {
                myServerSocket.close();
            }
            if (notificationServerSocket != null) {
                notificationServerSocket.close();
            }
        } catch (IOException ex) {
            LoggerUtil.error("Error closing server sockets: " + ex.getMessage());
        }

        LoggerUtil.info("The server has stopped successfully.");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        thread = new Thread(new Runnable() {
            public void run() {
                while (isServerRunning) {
                    try {
                        System.out.println("ttt");
                        Socket socket = myServerSocket.accept();
                        System.out.println("ttt2");

                        Socket notificationSocket = notificationServerSocket.accept();
                                                System.out.println("ttt3");

                        new ClientHandler(socket, notificationSocket);
                    } catch (IOException ex) {
                        Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        thread.setDaemon(true);
    }

    @FXML
    private void handleExecuteAction(ActionEvent event) {
        String query = tfquery.getText().trim();

        if (query.isEmpty()) {
            System.out.println("Query field cannot be empty.");
            return;
        }

        try (PreparedStatement stmt = con.prepareStatement(query)) {
            boolean isResultSet = stmt.execute();
            tfresult.clear();
            if (isResultSet) {
                try (ResultSet rs = stmt.getResultSet()) {
                    tfresult.clear();
                    ResultSetMetaData metaData = rs.getMetaData();
                    int columnCount = metaData.getColumnCount();

                    StringBuilder result = new StringBuilder();
                    for (int i = 1; i <= columnCount; i++) {
                        result.append(metaData.getColumnName(i)).append("\t");
                    }
                    result.append("\n");

                    while (rs.next()) {
                        for (int i = 1; i <= columnCount; i++) {
                            result.append(rs.getString(i)).append("\t");
                        }
                        result.append("\n");
                    }
                    tfresult.setText(result.toString());
                }
            } else {
                int updateCount = stmt.getUpdateCount();
                tfresult.setText("Query executed successfully. Rows affected: " + updateCount);
            }
        } catch (SQLException ex) {
            LoggerUtil.error("SQL Error: " + ex.getMessage());
        }
    }

    @FXML
    private void handleCommitAction(ActionEvent event) {
        try {
            tfresult.clear();
            con.commit();
        } catch (SQLException ex) {
            LoggerUtil.error("SQL Error: " + ex.getMessage());
        }
    }

    @FXML
    private void handlRollbackAction(ActionEvent event) {
        try {
            tfresult.clear();
            con.rollback();
        } catch (SQLException ex) {
            LoggerUtil.error("SQL Error: " + ex.getMessage());
        }
    }

}
