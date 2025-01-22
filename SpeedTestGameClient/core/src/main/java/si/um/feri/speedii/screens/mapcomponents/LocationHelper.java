package si.um.feri.speedii.screens.mapcomponents;

import io.github.cdimascio.dotenv.Dotenv;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class LocationHelper {

    private static final String URL_TEMPLATE = "https://api.geoapify.com/v1/geocode/reverse?lat=%s&lon=%s&apiKey=%s";

    public static String getLocationName(double latitude, double longitude) {
        Dotenv dotenv = Dotenv.load();
        String geoapifyApiKey = dotenv.get("GEOAPIFY_API_KEY");

        if (geoapifyApiKey == null || geoapifyApiKey.isEmpty()) {
            throw new IllegalStateException("Geoapify API key is missing! Add it to your .env file.");
        }

        String urlString = String.format(URL_TEMPLATE, latitude, longitude, geoapifyApiKey);

        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                return parseLocationDetails(response.toString());
            } else {
                throw new RuntimeException("Failed to get location. HTTP response code: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String parseLocationDetails(String jsonResponse) {
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            if (jsonObject.has("features")) {
                JSONObject firstFeature = jsonObject.getJSONArray("features").getJSONObject(0);
                JSONObject properties = firstFeature.getJSONObject("properties");

                String country = properties.optString("country", "");
                String city = properties.optString("city", "");
                String street = properties.optString("street", "");

                return String.format("%s, %s, %s", country, city, street);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "Location details not available";
    }
}
