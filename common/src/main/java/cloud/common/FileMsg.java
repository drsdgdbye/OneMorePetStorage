package cloud.common;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileMsg {
    private String fileName;
    private byte[] data;

    public FileMsg(Path path) {
        fileName = path.getFileName().toString();
        try {
            data = Files.readAllBytes(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getFileName() {
        return fileName;
    }

    public byte[] getData() {
        return data;
    }
}
