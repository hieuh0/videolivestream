package com.aloopen.livestreamingfb;

public class Message {
    private String mMessage;
    public String getMessage() {
        return mMessage;
    };
    public static class Builder {
        private String mMessage;
        public Builder message(String message) {
            mMessage = message;
            return this;
        }
        public Message build() {
            Message message = new Message();
            message.mMessage = mMessage;
            return message;
        }
    }
}
