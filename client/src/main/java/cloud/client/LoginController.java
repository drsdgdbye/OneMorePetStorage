package cloud.client;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
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
    private Button authorization;
    @FXML
    private PasswordField pass;

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

    private void setAuthorized(String response) throws IOException {
        if (response.equals("/authok")) {
            changeScene();
        }
    }

    public void signUp() {
        checkData();
        createUserDirectory(login.getText());
    }

    public void tryToAuth() throws IOException {
       /* checkData();
        Main.out.write(new CommandEncoder("/login").encode(login.getText(), pass.getText()));
        Main.out.flush();
        setAuthorized(new CommandDecoder().decode());
        userPath = Paths.get(".", "users", "login");*/
    }

    private void checkData() {
        if (login.getText().isEmpty() || pass.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Fill login/password", ButtonType.OK);
            alert.showAndWait();
            login.clear();
            pass.clear();
        }
    }

    private void createUserDirectory(String username) {
        try {
            userPath = Files.createDirectories(Paths.get(".", "users", username));
        } catch (FileAlreadyExistsException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "User already exists", ButtonType.OK);
            alert.showAndWait();
            login.clear();
            pass.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
