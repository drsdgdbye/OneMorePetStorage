package cloud.client.decoders;

import cloud.client.Main;

import java.io.IOException;

public class CommandDecoder {
    public String decode() throws IOException {
        StringBuilder builder = new StringBuilder();
        int countBytes;
        while ((countBytes = Main.in.read()) != -1) {
            builder.append(countBytes);
        }
        return builder.toString();
    }
}
