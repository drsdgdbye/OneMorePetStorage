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
    @FXML
    Button sendFile;
    @FXML
    Button upgradeCloudStorage;
    @FXML
    Button deleteLocalFile;

    private final Path USERPATH = Paths.get(".", "users", "user"); //будет браться из login.getText()

    public void sendFile() throws IOException {
        String selectedLocalFileName = localStorageListView.getFocusModel().getFocusedItem();
        if (selectedLocalFileName != null) {
            Path filePath = Paths.get(USERPATH.toString(), selectedLocalFileName);
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
        Network.sendRequest(Command.UPGRADE.getTag());
        cloudStorageListView.getItems().clear();
        cloudStorageListView.getItems().add(Network.readStringMsg());
    }

    public void downloadFile() throws IOException {
        String selectedCloudFileName = cloudStorageListView.getFocusModel().getFocusedItem();
        String selectedCloudFileNameLength = String.format("%02d", selectedCloudFileName.length());
        if (selectedCloudFileName != null) {
            Network.sendRequest(Command.DOWNLOAD.getTag(), selectedCloudFileNameLength, selectedCloudFileName);
            //add file reception
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Choose File", ButtonType.OK);
            alert.showAndWait();
        }
    }

    public void deleteLocalFile() {
        String selectedLocalFileName = localStorageListView.getFocusModel().getFocusedItem();
        Path deletePath = Paths.get(USERPATH.toString(), selectedLocalFileName);
        try {
            Files.delete(deletePath);
            updateLocalStorage();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteCloudFile() throws IOException {
        String selectedCloudFileName = cloudStorageListView.getFocusModel().getFocusedItem();
        String selectedCloudFileNameLength = String.format("%02d", selectedCloudFileName.length());
        if (selectedCloudFileName != null) {
            Network.sendRequest(Command.DELETE.getTag(), selectedCloudFileNameLength, selectedCloudFileName);
            upgradeCloudStorage();
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Choose File", ButtonType.OK);
            alert.showAndWait();
        }
    }
}
