package cloud.client;

import cloud.common.FileMsg;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class StorageController {
    private final Path USERPATH = Paths.get(".", "users", "user"); //будет браться из login.getText()
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

    public void sendFile() throws IOException {
        String selectedFile = localStorageListView.getFocusModel().getFocusedItem();
        if (selectedFile != null) {
            FileMsg file = new FileMsg(Paths.get(USERPATH + File.separator + selectedFile));
            Network.sendFile("ADD", file);
            Network.readStringMsg();
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
        Network.sendMsg("UPG");
        cloudStorageListView.getItems().clear();
        cloudStorageListView.getItems().add(Network.readStringMsg());
    }

    public void downloadFile() throws IOException {
        String selectedFile = cloudStorageListView.getFocusModel().getFocusedItem();
        if (selectedFile != null) {
            Network.sendMsg("DWN", selectedFile);
            //add file reception
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Choose File", ButtonType.OK);
            alert.showAndWait();
        }
    }
}
