package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {
    @FXML
    TextField login;

    @FXML
    PasswordField password;

    @FXML
    VBox LoginScene;

    @FXML
    Button authorization;

    public void auth(ActionEvent actionEvent) throws IOException {
        System.out.println(login.getText() + " " + password.getText());
        Stage stage = (Stage) authorization.getScene().getWindow();
        stage.close();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/storage.fxml"));
        Parent root1 = fxmlLoader.load();
        stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Storage");
        stage.setScene(new Scene(root1));
        stage.show();
    }

}
