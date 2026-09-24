package Socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Request {
    private String method;
    private String url;
    private String version;
    private Map<String, String> headers;
    private String message;

    public Request() {
    }

    private void setMethod(String method) {
        this.method = method;
    }

    private void setUrl(String url) {
        this.url = url;
    }

    private void setVersion(String version) {
        this.version = version;
    }

    private void setHeaders(Map<String, String> mp) {
        this.headers = mp;
    }

    private void setMessage(String message) {
        this.message = message;
    }

    public String getMethod() {
        return this.method;
    }

    public String getUrl() {
        return this.url;
    }

    public String getVersion() {
        return version;
    }

    public Map<String, String> getHeader() {
        return this.headers;
    }

    public String getMessage() {
        return this.message;
    }

    public void handleRequest(BufferedReader br) throws IOException {
        decodeRequestLine(br);
        decodeHeader(br);
        decodeMessage(br);
    }

    private void decodeRequestLine(BufferedReader br) throws IOException {
        String line = br.readLine();
        String[] strs = line.split(" ");
        setMethod(strs[0]);
        setUrl(strs[1]);
        setVersion(strs[2]);
    }

    private void decodeHeader(BufferedReader br) throws IOException {
        Map<String, String> mp = new HashMap<>();
        String line;
        String[] strs;
        while (!"".equals(line = br.readLine())) {
            strs = line.split(":", 2);
            mp.put(strs[0].trim(), strs[1].trim());
        }
        setHeaders(mp);
    }

    private void decodeMessage(BufferedReader br) throws IOException {
        int len = Integer.parseInt(headers.getOrDefault("Content-Length", "0"));
        if (len == 0) return;

        char[] message = new char[len];
        br.read(message);
        setMessage(new String(message));
    }

    public void print() {
        System.out.println(getMethod() + " " + getUrl() + " " + getVersion());
        for (Map.Entry<String, String> entry : getHeader().entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            System.out.println(key + ":" + value);
        }
        System.out.println();
        System.out.println(getMessage());
    }
}
