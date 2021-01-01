package com.swmabby.paintServer.entity;

import javax.websocket.Session;
import java.io.Serializable;

public class Client implements Serializable {

    private static final long serialVersionUID = 89765465492031L;

    private String userName;

    private Session session;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public Client(String userName, Session session) {
        this.userName = userName;
        this.session = session;
    }

    public Client() {
    }
}
