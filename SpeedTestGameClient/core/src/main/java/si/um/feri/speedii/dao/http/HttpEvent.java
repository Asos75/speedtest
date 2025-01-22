package si.um.feri.speedii.dao.http;

import okhttp3.*;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONObject;

import io.github.cdimascio.dotenv.Dotenv;
import si.um.feri.speedii.classes.Event;
import si.um.feri.speedii.classes.Location;
import si.um.feri.speedii.classes.SessionManager;
import si.um.feri.speedii.dao.EventCRUD;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


//TODO test this in case it is used
public class HttpEvent implements EventCRUD {

    private final String ip;
    private final OkHttpClient client;
    private final SessionManager sessionManager;

    public HttpEvent(SessionManager sessionManager) {
        this.sessionManager = sessionManager;

        // Load environment variables from .env file
        Dotenv dotenv = Dotenv.load();
        this.ip = dotenv.get("IP"); // Get the IP from the .env file

        this.client = new OkHttpClient();
    }

    @Override
    public Event getById(ObjectId id) {
        String url = ip + "/event/" + id.toString();
        Request request = new Request.Builder().url(url).get().build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                JSONObject jsonObject = new JSONObject(responseBody);
                return parseEvent(jsonObject);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Event> getAll() {
        String url = ip + "/event";
        Request request = new Request.Builder().url(url).build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                System.out.println("Failed to execute request: " + response.code());
                return new ArrayList<>();
            }

            String responseBody = response.body().string();
            JSONArray jsonArray = new JSONArray(responseBody);
            List<Event> events = new ArrayList<>();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                events.add(parseEvent(jsonObject));
            }

            return events;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    @Override
    public boolean insert(Event obj) {
        String url = ip + "/event";
        RequestBody requestBody = createRequestBody(obj);
        Request request = new Request.Builder()
            .url(url)
            .addHeader("authorization", "Bearer " + sessionManager.getToken())
            .post(requestBody)
            .build();

        try (Response response = client.newCall(request).execute()) {
            return response.isSuccessful();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean update(Event obj) {
        String url = ip + "/event/" + obj.getId().toString();
        RequestBody requestBody = createRequestBody(obj);
        Request request = new Request.Builder()
            .url(url)
            .addHeader("authorization", "Bearer " + sessionManager.getToken())
            .put(requestBody)
            .build();

        try (Response response = client.newCall(request).execute()) {
            return response.isSuccessful();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delete(Event obj) {
        String url = ip + "/event/" + obj.getId().toString();
        Request request = new Request.Builder()
            .url(url)
            .addHeader("authorization", "Bearer " + sessionManager.getToken())
            .delete()
            .build();

        try (Response response = client.newCall(request).execute()) {
            return response.isSuccessful();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private RequestBody createRequestBody(Event obj) {
        JSONObject json = new JSONObject();
        json.put("name", obj.getName());
        json.put("type", obj.getType());
        json.put("time", obj.getTime().format(DateTimeFormatter.ISO_DATE_TIME));
        json.put("online", obj.isOnline());

        if (obj.getLocation() != null) {
            JSONObject locationObject = new JSONObject();
            locationObject.put("type", "Point");

            JSONArray coordinatesArray = new JSONArray();
            for (Double coordinate : obj.getLocation().coordinates) {
                coordinatesArray.put(coordinate);
            }

            locationObject.put("coordinates", coordinatesArray);
            json.put("location", locationObject);
        }

        return RequestBody.create(json.toString(), MediaType.parse("application/json"));
    }

    private Event parseEvent(JSONObject jsonObject) {
        String name = jsonObject.getString("name");
        String type = jsonObject.getString("type");

        String timeString = jsonObject.getString("time");
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        LocalDateTime time = LocalDateTime.parse(timeString, formatter);

        boolean online = jsonObject.getBoolean("online");

        Location location = null;
        if (!jsonObject.isNull("location") && jsonObject.get("location") instanceof JSONObject) {
            JSONObject locationObject = jsonObject.getJSONObject("location");

            List<Double> coordinates = new ArrayList<>();
            JSONArray coordinatesArray = locationObject.getJSONArray("coordinates");
            for (int i = 0; i < coordinatesArray.length(); i++) {
                coordinates.add(coordinatesArray.getDouble(i));
            }

            location = new Location(locationObject.getString("type"), coordinates);
        }

        ObjectId id = new ObjectId(jsonObject.getString("_id"));
        return new Event(name, type, time, online, location, id);
    }
}
