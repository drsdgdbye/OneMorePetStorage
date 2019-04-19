package cloud.client;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public class StorageController {
    @FXML
    TextField search;

    @FXML
    Button sendFile;

    @FXML
    ListView<Path> localStorageListView;

    @FXML
    ListView<Path> cloudStorageListView;

    @FXML
    Button upgradeLocalStorage;


    public void sendFile() {
        try {
            /*Main.out.write("file".getBytes());
            Main.out.flush();
            Main.out.write(LoginController.userPath.getFileName().toString().getBytes());
            Main.out.flush();*/
            FileInputStream fin = new FileInputStream(LoginController.userPath.toString() + "photo1.jpg");
            byte[] buf = new byte[fin.available()];
            fin.read(buf);
            Main.out.write(buf);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
