package cloud.client;

import cloud.client.encoders.CommandEncoder;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public class StorageController {
    @FXML
    ListView<Path> localStorageListView;

    @FXML
    TextField search;
    @FXML
    ListView<Path> cloudStorageListView;
    @FXML
    Button updateLocalStorage;

    @FXML
    Button sendFile;
    private Path userPath = LoginController.userPath;

    public void sendFile() {
        String selectedFile = localStorageListView.getSelectionModel().getSelectedItems().toString();
        try {
            if (selectedFile != null) { //if no file is selected from localStorageListView an exception will be thrown
                Main.out.write(new CommandEncoder("/file").encode(userPath.getFileName().toString()));
                Main.out.flush();
                FileInputStream fileInputStream = new FileInputStream(userPath.toString()
                        + File.separator
                        + selectedFile);
                byte[] buffer = new byte[1024];
                while (true) {
                    int countBytes = fileInputStream.read(buffer);
                    if (countBytes != -1) {
                        Main.out.write(buffer, 0, countBytes);
                    } else break;
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Choose File", ButtonType.OK);
                alert.showAndWait();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateLocalStorage() {
        try {
            localStorageListView.getItems().clear();
            Files.walkFileTree(userPath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    localStorageListView.getItems().add(file.getFileName());
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
