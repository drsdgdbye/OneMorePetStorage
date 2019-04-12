package cloud.client;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class LoginController {
    static Path userPath;
    @FXML
    Button createUser;
    @FXML
    private TextField login;
    @FXML
    private PasswordField password;
    @FXML
    private Button authorization;
    private InetSocketAddress hostAddress;
    private SocketChannel clientSocket;

    public void startClient() throws IOException {
        hostAddress = new InetSocketAddress("localhost", 8189);
        clientSocket = SocketChannel.open(hostAddress);
        if (clientSocket.isConnected()) {
            System.out.println("client started");
        }
    }

    private void changeScene() throws IOException {
        Stage stage = (Stage) authorization.getScene().getWindow();
        stage.close();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/storage.fxml"));
        Parent root1 = fxmlLoader.load();
        stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(login.getText());
        stage.setScene(new Scene(root1));
        stage.show();
    }

    public void tryToAuth() throws IOException {
        if (login.getText().equals("login") && password.getText().equals("pass")) {
            changeScene();
        }
    }

    public void createUser() {
        if (login.getText().isEmpty() || password.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Fill login/password", ButtonType.OK);
            alert.showAndWait();
        } else {
            createNewUserFolder(login.getText());
        }
    }

    private void createNewUserFolder(String username) {
        try {
            startClient();
            userPath = Files.createDirectory(Paths.get(".\\users\\" + username));
            changeScene();
        } catch (FileAlreadyExistsException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "User already exists", ButtonType.OK);
            alert.showAndWait();
            login.clear();
            password.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
