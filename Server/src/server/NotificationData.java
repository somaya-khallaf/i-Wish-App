/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.PrintWriter;
import java.net.Socket;


public class NotificationData {

    private String userName;
    private PrintWriter notificationWriter;
    private Socket notificationSocket;

    public NotificationData(String userName, PrintWriter notificationWriter, Socket notificationSocket) {
        this.userName = userName;
        this.notificationWriter = notificationWriter;
        this.notificationSocket = notificationSocket;
    }

    public String getUserName() {
        return userName;
    }

    public PrintWriter getNotificationWriter() {
        return notificationWriter;
    }

    public Socket getNotificationSocket() {
        return notificationSocket;
    }
}
