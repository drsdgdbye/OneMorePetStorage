package cloud.client.encoders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandEncoder {
    private String command;
    private List<String> bodyList;

    public CommandEncoder(String command) {
        this.command = command;
        bodyList = new ArrayList<>();
    }

    public byte[] encode(String... body) {
        StringBuilder builder = new StringBuilder();
        bodyList.add(command);
        bodyList.addAll(Arrays.asList(body));
        for (String s : bodyList) {
            builder.append(s).append(" ");
        }
        return builder.toString().getBytes();
    }
}
