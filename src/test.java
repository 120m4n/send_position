import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

public class test {
    public static void main(String[] args) throws Exception {
        JSONObject obj = new JSONObject();
        obj.put("name", "foo");
        obj.put("num", 100);
        obj.put("balance", 1000.21);
        obj.put("is_vip",true);
        System.out.print(obj.toString());

        geocoding("calle 80 # 11-11");


    }

    public JSONObject requestPost(String sURL, JSONObject JSONSend) throws Exception{
        //se crea el objeto de tipo URL
        URL url = new URL(sURL);
        //Convierte el JSONObject a un string y despues a un arreglo de Bytes
        byte[] postDataBytes = JSONSend.toString().getBytes("UTF-8");
        //Abre la conexión y asigna el objeto de la conexion a conn
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        //establece el metodo o verbo de la conexión    conn.setRequestMethod("POST");
        //se establece que la solicitud tendrá salida
        conn.setDoOutput(true);
        //se escribe el cuerpo de la solicitud con el JSON convertido a Bytes
        conn.getOutputStream().write(postDataBytes);
        //Se realiza la solicitud y se lee la respuesta de la misma en el Reader
        Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
        //Se convierte el Reader en un String
        StringBuilder sb = new StringBuilder();
        for (int c; (c = in.read()) >= 0;)
            sb.append((char)c);
        String response = sb.toString();
        //Se crea el objeto que parseará la respuesta
        //deprecate JSONParser parser = new JSONParser();
        //Se parsea la respuesta en un JSONObject
        JSONObject jsonResult = new JSONObject(response);
        //se Retorna la respuesta
        return jsonResult;
    }

    public static void geocoding(String addr) throws Exception
    {
        // build a URL
        String s = "http://maps.google.com/maps/api/geocode/json?" +
                "sensor=false&address=";
        s += URLEncoder.encode(addr, "UTF-8");
        URL url = new URL(s);

        // read from the URL
        Scanner scan = new Scanner(url.openStream());
        String str = "";
        while (scan.hasNext())
            str += scan.nextLine();
        scan.close();

        // build a JSON object
        JSONObject obj = new JSONObject(str);
        if (! obj.getString("status").equals("OK"))
            return;

        // get the first result
        JSONObject res = obj.getJSONArray("results").getJSONObject(0);
        System.out.println(res.getString("formatted_address"));
        JSONObject loc =
                res.getJSONObject("geometry").getJSONObject("location");
        System.out.println("lat: " + loc.getDouble("lat") +
                ", lng: " + loc.getDouble("lng"));
    }
}
