package distributed.filesystem.mytinygithub.app.request;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Header {

    public Map<String, List<String>> values;

    public static final int SIZE = 10;

    public Header() {
        values = new HashMap<>();
    }

    public String getValue(String key) {
        return values.get(key.toLowerCase()).get(0);
    }

    public boolean hasValue(String key) {
        return values.containsKey(key.toLowerCase());
    }

    public void addValue(String key, String value) {
        values.computeIfAbsent(key.toLowerCase(), k -> new ArrayList<>()).add(value);
    }

    public void replaceValue(String key, String ...newValues) {
        List<String> listOfValues = new ArrayList<>();

        for (var value : newValues) {
              listOfValues.add(value.toLowerCase());
        }

        values.put(key, listOfValues);
    }

    public void print() {
        for (Map.Entry<String, List<String>> entry : values.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }

    public byte[] getInBytes() {
        StringBuilder sb = new StringBuilder();
        for (var value : values.entrySet()) {
            sb.append(value.getKey()).append(": ").append(value.getValue().get(0)).append("\n");
        }
        return sb.toString().getBytes();
    }

    public int sizeInBytes() {
        return getInBytes().length;
    }

    public static Header extractHeader(InputStream in) throws IOException {
        byte[] infoHeader = in.readNBytes(Header.SIZE);
        int headerSize = Integer.parseInt(new String(infoHeader).trim());

        var header = new Header();
        header.parse(in.readNBytes(headerSize));

        return header;
    }

    public void parse(byte[] headerBytes) {

        var headerString = new String(headerBytes);

        for (String line : headerString.split("\n")) {
            String[] keyValue = line.split(":");

            if (keyValue.length != 2) {
                continue;
            }

            String key = keyValue[0].trim().toLowerCase();
            String value = keyValue[1].trim();

            values.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
        }
    }


    public static void write(Header header, DataOutputStream out) throws IOException {
        String format = "%0" + SIZE + "d";
        out.write(format.formatted(header.sizeInBytes()).getBytes());
        out.write(header.getInBytes());
    }

}
