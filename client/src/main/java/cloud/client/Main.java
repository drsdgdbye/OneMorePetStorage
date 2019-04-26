package cloud.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Main extends Application {
    public static DataInputStream in;
    public static DataOutputStream out;
    public static Network network;

    public Main() {
        try {
            network = new Network();
            network.start();
            in = new DataInputStream(network.getSocket().getInputStream());
            out = new DataOutputStream(network.getSocket().getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        } /*finally {
            try {
                in.close();
                out.close();
            } catch (IOException ignored) {}
            if (network != null) {
                network.stop();
            }
        }*/
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/login.fxml"));
        primaryStage.setTitle("Authorization");
        primaryStage.setScene(new Scene(root, 400, 400));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
