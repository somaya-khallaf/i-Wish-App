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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
    private TextArea tfresult;
    private static final Connection con = DatabaseConnection.getConnection();
    private boolean isServerRunning = false;
    private static final int THREAD_POOL_SIZE = 10;
    private ExecutorService threadPool;

    @FXML
    public void handleStartAction(ActionEvent event) {

        if (!isServerRunning) {
            LoggerUtil.info("Starting the server...");
            try {
                myServerSocket = new ServerSocket(5005);
                notificationServerSocket = new ServerSocket(5000);
            } catch (IOException ex) {
                LoggerUtil.error("The server failed to start:" + ex.getMessage());
            }

            isServerRunning = true;
            LoggerUtil.info("The server has started successfully.");
        }
        thread = new Thread(new Runnable() {
            public void run() {

                while (isServerRunning) {
                    try {
                        Socket socket = myServerSocket.accept();
                        threadPool.submit(new ClientHandler(socket, notificationServerSocket));
                    } catch (IOException ex) {
                        Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    @FXML
    public void handleStopAction(ActionEvent event) {
        thread.interrupt();
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
        threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
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
