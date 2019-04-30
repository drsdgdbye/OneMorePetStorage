package cloud.client.encoders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandEncoder {
    private String tag;
    private List<String> bodyList;

    public CommandEncoder(String tag) {
        this.tag = tag;
        bodyList = new ArrayList<>();
    }


    public byte[] wrapToBytes(String... body) {
        StringBuilder builder = new StringBuilder();
        bodyList.add(tag);
        bodyList.addAll(Arrays.asList(body));
        for (String s : bodyList) {
            builder.append(s).append("\n");
        }
        return builder.toString().getBytes();
    }
}
