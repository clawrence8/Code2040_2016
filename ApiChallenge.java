// Clayton Lawrence Code2040 Assessment 2016


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ApiChallenge {

    protected static final String REGISTRATION_ENDPOINT = "http://challenge.code2040.org/api/register";
    private static final String TOKEN = "b0f4aa0922391b48bc55c307e8bde365";

    public static String post(String endpoint, JSONObject jsonObject) {
        String jsonString = null;
        try {
            URL url = new URL(endpoint);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");

            OutputStream os = connection.getOutputStream();
            os.write(jsonObject.toString().getBytes());
            os.flush();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException("Failed! Error code: " + connection.getResponseCode());
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            connection.disconnect();
            reader.close();
            jsonString = builder.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(jsonString);
        return jsonString;
    }



    public static void main(String[] args) throws JSONException {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("token", TOKEN);
        map.put("github", "https://github.com/clawrence8/Code2040_2016");
        JSONObject jsonObject = new JSONObject(map);

        post(REGISTRATION_ENDPOINT, jsonObject);
    }


}
