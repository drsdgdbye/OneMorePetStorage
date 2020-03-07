package cloud.client;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

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

    static void sendRequest(String... body) {
        try {
            out.write(wrapToBytes(body));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void sendFile(String tag, File file) {
        try {
            String fileNameLength = String.format("%02d", file.getName().length());
            String fileLength = String.format("%011d", file.length());

            out.write(wrapToBytes(tag, fileNameLength, file.getName(), fileLength));
            out.flush();
            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[1024 * 1024 * 10];
            int count;
            while ((count = fis.read(buffer)) > 0) {
                out.write(buffer, 0, count);
            }
            fis.close();
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

    private static byte[] wrapToBytes(String... body) {
        StringBuilder builder = new StringBuilder();
        ArrayList<String> bodyList = new ArrayList<>(Arrays.asList(body));
        for (String s : bodyList) {
            builder.append(s);
        }
        return builder.toString().getBytes();
    }

    static void stop() {
        try {
            in.close();
        } catch (IOException ignored) {
        }

        try {
            out.flush();
            out.close();
        } catch (IOException ignored) {
        }

        try {
            socket.close();
        } catch (IOException ignored) {
        }
    }
}
