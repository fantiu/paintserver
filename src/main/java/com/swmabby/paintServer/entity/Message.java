package com.swmabby.paintServer.entity;

import lombok.Data;

@Data
public class Message {
    private int groupId;

    private int userId;

    private String message;

    private long messageId;

    private int receiveNumber;
}
