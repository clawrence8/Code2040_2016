// Clayton Lawrence Code2040 Assessment 2016

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ApiChallenge {

    private static final String TOKEN = "b0f4aa0922391b48bc55c307e8bde365";
    private static final String REGISTRATION_ENDPOINT = "http://challenge.code2040.org/api/register";
    private static final String REVERSE_ENDPOINT = "http://challenge.code2040.org/api/reverse";
    private static final String REVERSE_ENDPOINT_VALIDATE = "http://challenge.code2040.org/api/reverse/validate";
    private static final String HAYSTACK_ENDPOINT = "http://challenge.code2040.org/api/haystack";
    private static final String HAYSTACK_ENDPOINT_VALIDATE = "http://challenge.code2040.org/api/haystack/validate";
    private static final String PREFIX_ENDPOINT = "http://challenge.code2040.org/api/prefix";
    private static final String PREFIX_VALIDATE = "http://challenge.code2040.org/api/prefix/validate";
    private static final String DATE_ENDPOINT = "http://challenge.code2040.org/api/dating";
    private static final String DATE_VALIDATE = "http://challenge.code2040.org/api/dating/validate";

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

        return jsonString;
    }

    public static String reverse(String s) {
        if (s == null) {
            return null;
        }
        char[] chars = s.toCharArray();
        int i = 0;
        int j = s.length() - 1;
        while (i < j) {
            char temp = chars[i];
            chars[i] = chars[j];
            chars[j] = temp;
            i++;
            j--;
        }
        return new String(chars);
    }

    public static int haystack(String needle, JSONArray haystack) {
        if (needle == null || haystack == null) {
            return -1;
        }

        for (int i = 0; i < haystack.length(); i++) {
            try {
                if (needle.equals(haystack.get(i))) {
                    return i;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return -1;
    }

    public static List<String> prefix(String prefix, JSONArray array) {
        ArrayList<String> list = new ArrayList<>();
        if (prefix == null || array == null) {
            return null;
        }

        for (int i = 0; i < array.length(); i++) {
            try {
                String current = (String) array.get(i);
                if (!current.startsWith(prefix)) {
                    list.add(current);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    public static String datingGame(String date, int seconds) {
        DateTime dateTime = DateTime.parse(date);
        dateTime = dateTime.plusSeconds(seconds);
        DateTimeFormatter formatter = ISODateTimeFormat.dateTimeNoMillis();
        return formatter.print(dateTime);
    }


    public static void main(String[] args) throws JSONException {
        // Stage 1
        HashMap<String, String> map = new HashMap<>();
        map.put("token", TOKEN);
        map.put("github", "https://github.com/clawrence8/Code2040_2016");
        JSONObject jsonObject = new JSONObject(map);
        post(REGISTRATION_ENDPOINT, jsonObject);

        // Stage 2
        String string = post(REVERSE_ENDPOINT, jsonObject);
        String reverseString = reverse(string);
        jsonObject.put("string", reverseString);
        post(REVERSE_ENDPOINT_VALIDATE, jsonObject);

        // Stage 3
        JSONObject dictionary = new JSONObject(post(HAYSTACK_ENDPOINT, jsonObject));
        String needle = dictionary.getString("needle");
        JSONArray haystack = dictionary.getJSONArray("haystack");
        int index = haystack(needle, haystack);
        jsonObject.put("needle", index);
        post(HAYSTACK_ENDPOINT_VALIDATE, jsonObject);

        // Stage 4
        JSONObject prefixDictionary = new JSONObject(post(PREFIX_ENDPOINT, jsonObject));
        String prefixString = prefixDictionary.getString("prefix");
        JSONArray list = prefixDictionary.getJSONArray("array");
        List<String> prefixSolution = prefix(prefixString, list);
        jsonObject.put("array", prefixSolution);
        post(PREFIX_VALIDATE, jsonObject);

        // Stage 5
        JSONObject dateDictionary = new JSONObject(post(DATE_ENDPOINT, jsonObject));
        String date = dateDictionary.getString("datestamp");
        int interval = dateDictionary.getInt("interval");
        String newDatestamp = datingGame(date, interval);
        jsonObject.put("datestamp", newDatestamp);
        post(DATE_VALIDATE, jsonObject);
    }


}
