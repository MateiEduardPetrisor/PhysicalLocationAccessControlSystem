package com.example.user.l_1_1;

public class Message {
    private String messageText;
    private String timeStamp;

    public Message(String messageText, String timeStamp) {
        this.messageText = messageText;
        this.timeStamp = timeStamp;
    }

    public String getMessageText() {
        return messageText;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    @Override
    public String toString() {
        return getMessageText() + ";" + getTimeStamp() + ";";
    }
}
