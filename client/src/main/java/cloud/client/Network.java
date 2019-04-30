package cloud.client;

import cloud.client.encoders.CommandEncoder;
import cloud.common.FileMsg;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

class Network {
    private static final String HOST = "localhost";
    private static final int PORT = 8189;
    private static Socket socket;
    private static DataInputStream in;
    private static DataOutputStream out;

    static void start() {
        try {
            socket = new Socket(HOST, PORT);
            if (socket.isConnected()) {
                System.out.println("connected to server");
                in = new DataInputStream(socket.getInputStream());
                out = new DataOutputStream(socket.getOutputStream());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void sendMsg(String tag, String... body) {
        try {
            out.write(new CommandEncoder(tag).wrapToBytes(body));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void sendFile(String tag, FileMsg file) {
        try {
            out.write(new CommandEncoder(tag).wrapToBytes(file.getFileName()));
            out.flush();
            out.write(file.getData());
            out.flush();
            out.write("\n".getBytes());
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static String readStringMsg() throws IOException {
        StringBuilder builder = new StringBuilder();
        int countBytes;
        while ((countBytes = in.read()) != -1) {
            builder.append((char) countBytes);
        }
        return builder.toString();
    }

    static void stop() {
        if (in != null) {
            try {
                in.close();
            } catch (IOException ignored) {
            }
        }
        if (out != null) {
            try {
                out.flush();
                out.close();
            } catch (IOException ignored) {
            }
        }
        if (!socket.isClosed()) {
            try {
                socket.close();
            } catch (IOException ignored) {
            }
        }
    }
}
