package cloud.client;

import java.io.IOException;
import java.net.Socket;

class Network {
    private final String HOST = "localhost";
    private final int PORT = 8189;
    private Socket socket;

    void start() {
        try {
            socket = new Socket(HOST, PORT);
            if (socket.isConnected()) {
                System.out.println("connected");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    Socket getSocket() {
        return socket;
    }

    void stop() {
        if (!socket.isClosed()) {
            try {
                socket.close();
            } catch (IOException ignored) {
            }
        }
    }
}
