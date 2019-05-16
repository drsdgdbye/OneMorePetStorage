package cloud.client;

import cloud.common.Command;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class StorageController {
    @FXML
    ListView<String> localStorageListView;
    @FXML
    ListView<String> cloudStorageListView;
    @FXML
    Button updateLocalStorage;
    private final Path USERPATH = Paths.get(".", "users", "user"); //будет браться из login.getText()
    @FXML
    Button sendFile;
    @FXML
    Button upgradeCloudStorage;
    @FXML
    Button deleteLocalFile;

    public void sendFile() throws IOException {
        String selectedLocalFile = localStorageListView.getFocusModel().getFocusedItem();
        if (selectedLocalFile != null) {
            Path filePath = Paths.get(USERPATH.toString(), selectedLocalFile);
            Network.sendFile(Command.ADD.getTag(), filePath.toFile());
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Choose File", ButtonType.OK);
            alert.showAndWait();
        }
    }

    public void updateLocalStorage() {
        try {
            localStorageListView.getItems().clear();
            Files.walkFileTree(USERPATH, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    localStorageListView.getItems().add(file.getFileName().toString());
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void upgradeCloudStorage() throws IOException {
        Network.sendMsg(Command.UPGRADE.getTag());
        cloudStorageListView.getItems().clear();
        cloudStorageListView.getItems().add(Network.readStringMsg());
    }

    public void downloadFile() throws IOException {
        String selectedCloudFile = cloudStorageListView.getFocusModel().getFocusedItem();
        if (selectedCloudFile != null) {
            Network.sendMsg(Command.DOWNLOAD.getTag(), selectedCloudFile);
            //add file reception
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Choose File", ButtonType.OK);
            alert.showAndWait();
        }
    }

    public void deleteLocalFile() {
        String selectedLocalFile = localStorageListView.getFocusModel().getFocusedItem();
        Path deletePath = Paths.get(USERPATH.toString(), selectedLocalFile);
        try {
            Files.delete(deletePath);
            updateLocalStorage();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteCloudFile() throws IOException {
        String selectedCloudFile = cloudStorageListView.getFocusModel().getFocusedItem();
        Network.sendMsg(Command.DELETE.getTag(), selectedCloudFile);
        upgradeCloudStorage();
    }
}
