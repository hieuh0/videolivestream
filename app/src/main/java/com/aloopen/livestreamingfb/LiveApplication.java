package com.aloopen.livestreamingfb;

import android.app.Application;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class LiveApplication extends Application {
    private Socket socket;
    {
        try {
            socket = IO.socket("");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
