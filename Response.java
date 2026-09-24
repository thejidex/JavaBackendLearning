package Socket;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Response {
    private String version;
    private int code;
    private String status;
    private Map<String, String> headers;
    private String message;

    public void setVersion(String version) {
        this.version = version;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setHeaders(Map<String, String> mp) {
        this.headers = mp;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getVersion() {
        return this.version;
    }

    public int getCode() {
        return this.code;
    }

    public String getStatus() {
        return this.status;
    }

    public Map<String, String> getHeaders() {
        return this.headers;
    }

    public String getMessage() {
        return this.message;
    }

    public String buildResponse(Request request, String ms) {
        this.setCode(200);
        this.setStatus("OK");
        this.setVersion(request.getVersion());

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "text/plain; charset=UTF-8");
        headers.put("Content-Length", String.valueOf(ms.getBytes(StandardCharsets.UTF_8).length));
        headers.put("Connection", "close");
        this.setHeaders(headers);
        this.setMessage(ms);

        // 构建响应字符串
        StringBuilder builder = new StringBuilder();
        setResponseLine(builder);
        setResponseHeaders(builder);
        setResponseMessage(builder);
        return builder.toString();
    }

    private void setResponseLine(StringBuilder builder) {
        builder.append(this.getVersion())
                .append(" ")
                .append(this.getCode())
                .append(" ")
                .append(this.getStatus())
                .append("\r\n");
    }

    private void setResponseHeaders(StringBuilder builder) {
        for (Map.Entry<String, String> entry : this.getHeaders().entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            builder.append(key)
                    .append(":")
                    .append(value)
                    .append("\r\n");
        }
        builder.append("\r\n");
    }

    private void setResponseMessage(StringBuilder builder) {
        builder.append(this.getMessage());
    }
}
