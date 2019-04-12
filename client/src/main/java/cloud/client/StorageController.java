package cloud.client;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public class StorageController {
    @FXML
    Button sendFiles;

    @FXML
    ListView<Path> localStorageListView;

    @FXML
    ListView<Path> cloudStorageListView;

    @FXML
    Button upgradeLocalStorage;


    public void sendFiles() {

    }

    public void upgradeLocalStorage() {
        Path upgradePath = LoginController.userPath;
        try {
            localStorageListView.getItems().clear();
            Files.walkFileTree(upgradePath, new SimpleFileVisitor<Path>() {
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
