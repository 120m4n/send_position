import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class request {
    private String url_endpoint;
    private String method;
    private String body;
    private String headers;
    private String params;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getHeaders() {
        return headers;
    }

    public void setHeaders(String headers) {
        this.headers = headers;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public request(String url_endpoint, String method, String body, String headers, String params) {
        this.url_endpoint = url_endpoint;
        this.method = method;
        this.body = body;
        this.headers = headers;
        this.params = params;
    }

    public String getUrl_endpoint() {
        return url_endpoint;
    }

    public void setUrl_endpoint(String url_endpoint) {
        this.url_endpoint = url_endpoint;
    }

    // make a request post to endpoint with body and headers
    public String makeRequest() {
        String response = "";
        try {
            URL url = new URL(this.url_endpoint);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod(this.method);
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);
            OutputStream os = con.getOutputStream();
            os.write(this.body.getBytes());
            os.flush();
            os.close();
            int responseCode = con.getResponseCode();
            System.out.println("Response Code : " + responseCode);
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            response = content.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }




}
