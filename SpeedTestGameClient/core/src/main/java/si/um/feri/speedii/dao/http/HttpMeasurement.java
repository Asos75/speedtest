package si.um.feri.speedii.dao.http;

import io.github.cdimascio.dotenv.Dotenv;
import okhttp3.*;
import si.um.feri.speedii.classes.Location;
import si.um.feri.speedii.classes.Measurement;
import si.um.feri.speedii.classes.SessionManager;
import si.um.feri.speedii.classes.Type;
import si.um.feri.speedii.classes.User;
import si.um.feri.speedii.dao.MeasurementCRUD;

import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class HttpMeasurement implements MeasurementCRUD {

    private final OkHttpClient client;
    private final String ip;
    private final SessionManager sessionManager;

    public HttpMeasurement(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
        this.client = new OkHttpClient();

        Dotenv dotenv = Dotenv.configure().filename(".env").load();
        this.ip = dotenv.get("IP");
    }


    //TESTED
    @Override
    public List<Measurement> getByUser(User user) throws IOException {
        String url = ip + "/measurements/user/" + user.getId();
        Request request = new Request.Builder()
            .url(url)
            .get()
            .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                System.out.println("Failed to execute request: " + response.code());
                return new ArrayList<>();
            }

            String responseBody = response.body().string();
            JSONArray jsonArray = new JSONArray(responseBody);
            List<Measurement> measurements = new ArrayList<>();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                measurements.add(parseMeasurement(jsonObject));
            }

            return measurements;
        }
    }

    //TESTED
    @Override
    public List<Measurement> getByTimeFrame(LocalDateTime start, LocalDateTime end) throws IOException {
        String startTime = start.format(DateTimeFormatter.ISO_DATE_TIME);
        String endTime = end.format(DateTimeFormatter.ISO_DATE_TIME);
        String url = ip + "/measurements/timeframe/" + startTime + "/" + endTime;
        Request request = new Request.Builder()
            .url(url)
            .get()
            .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                System.out.println("Failed to execute request: " + response.code());
                return new ArrayList<>();
            }

            String responseBody = response.body().string();
            JSONArray jsonArray = new JSONArray(responseBody);
            List<Measurement> measurements = new ArrayList<>();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                measurements.add(parseMeasurement(jsonObject));
            }

            return measurements;
        }
    }

    //TESTED
    @Override
    public Measurement getById(ObjectId id) throws IOException {
        String url = ip + "/measurements/" + id;
        Request request = new Request.Builder()
            .url(url)
            .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                System.out.println("Failed to execute request: " + response.code());
                return null;
            }

            String responseBody = response.body().string();
            JSONObject jsonObject = new JSONObject(responseBody);
            return parseMeasurement(jsonObject);
        }
    }

    //TESTED
    @Override
    public List<Measurement> getAll() throws IOException {
        String url = ip + "/measurements";
        Request request = new Request.Builder()
            .url(url)
            .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                System.out.println("Failed to execute request: " + response.code());
                return new ArrayList<>();
            }

            String responseBody = response.body().string();
            JSONArray jsonArray = new JSONArray(responseBody);
            List<Measurement> measurements = new ArrayList<>();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                measurements.add(parseMeasurement(jsonObject));
            }

            return measurements;
        }
    }

    //TESTED
    @Override
    public boolean insert(Measurement obj) throws IOException {
        JSONObject requestBody = new JSONObject();
        requestBody.put("speed", obj.getSpeed());
        requestBody.put("type", obj.getType().name());
        requestBody.put("provider", obj.getProvider());
        requestBody.put("time", obj.getTime().format(DateTimeFormatter.ISO_DATE_TIME));

        JSONObject locationObject = new JSONObject();
        locationObject.put("type", "Point");
        JSONArray coordinatesArray = new JSONArray();
        for (double coordinate : obj.getLocation().coordinates) {
            coordinatesArray.put(coordinate);
        }
        locationObject.put("coordinates", coordinatesArray);

        requestBody.put("location", locationObject);
        if (obj.getUser() != null) {
            requestBody.put("measuredBy", obj.getUser().getId());
        }

        Request request = new Request.Builder()
            .url(ip + "/measurements")
            .post(RequestBody.create(requestBody.toString(), MediaType.get("application/json")))
            .build();

        try (Response response = client.newCall(request).execute()) {
            return response.isSuccessful();
        }
    }

    @Override
    public boolean insertMany(List<Measurement> list) {
        JSONArray jsonArray = new JSONArray();
        for (Measurement measurement : list) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("speed", measurement.getSpeed());
            jsonObject.put("type", measurement.getType().name());
            jsonObject.put("provider", measurement.getProvider());
            jsonObject.put("time", measurement.getTime().format(DateTimeFormatter.ISO_DATE_TIME));
            JSONObject locationObject = new JSONObject();
            locationObject.put("type", "Point");
            locationObject.put("coordinates", new JSONArray(measurement.getLocation().coordinates));
            jsonObject.put("location", locationObject);
            if (measurement.getUser() != null) {
                jsonObject.put("measuredBy", measurement.getUser().getId());
            }
            jsonArray.put(jsonObject);
        }

        JSONObject jsonMeasurements = new JSONObject();
        jsonMeasurements.put("measurements", jsonArray);

        RequestBody requestBody = RequestBody.create(
            jsonMeasurements.toString(), MediaType.get("application/json"));

        Request request = new Request.Builder()
            .url(ip + "/measurements/createMany")
            .addHeader("authorization", "Bearer " + sessionManager.getToken())
            .post(requestBody)  // Here we use `post` with `RequestBody`
            .build();

        try {
            Response response = client.newCall(request).execute();
            return response.isSuccessful();  // Returns true if the request was successful
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(Measurement obj) throws IOException {
        String url = ip + "/measurements/" + obj.getId();
        JSONObject requestBody = new JSONObject();
        requestBody.put("speed", obj.getSpeed());
        requestBody.put("type", obj.getType().name());
        requestBody.put("provider", obj.getProvider());
        requestBody.put("time", obj.getTime().format(DateTimeFormatter.ISO_DATE_TIME));

        JSONObject locationObject = new JSONObject();
        locationObject.put("type", "Point");
        locationObject.put("coordinates", obj.getLocation().coordinates);

        requestBody.put("location", locationObject);
        if (obj.getUser() != null) {
            requestBody.put("measuredBy", obj.getUser().getId());
        }

        Request request = new Request.Builder()
            .url(url)
            .addHeader("authorization", "Bearer " + sessionManager.getToken())
            .put(RequestBody.create(requestBody.toString(), MediaType.get("application/json")))
            .build();

        try (Response response = client.newCall(request).execute()) {
            return response.isSuccessful();
        }
    }


    //TESTED
    @Override
    public boolean delete(Measurement obj) throws IOException {
        String url = ip + "/measurements/" + obj.getId();
        Request request = new Request.Builder()
            .url(url)
            .addHeader("authorization", "Bearer " + sessionManager.getToken())
            .delete()
            .build();

        try (Response response = client.newCall(request).execute()) {
            return response.isSuccessful();
        }
    }

    private Measurement parseMeasurement(JSONObject jsonObject) {
        long speed = jsonObject.getLong("speed");
        Type type = Type.valueOf(jsonObject.getString("type"));
        String provider = jsonObject.getString("provider");

        String timeString = jsonObject.getString("time");
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        LocalDateTime time = LocalDateTime.parse(timeString, formatter);

        JSONObject locationObject = jsonObject.getJSONObject("location");
        List<Double> coordinates = new ArrayList<>();
        JSONArray coordinatesArray = locationObject.getJSONArray("coordinates");
        for (int i = 0; i < coordinatesArray.length(); i++) {
            coordinates.add(coordinatesArray.getDouble(i));
        }
        Location location = new Location(locationObject.getString("type"), coordinates);

        User user = null;
        if (!jsonObject.isNull("measuredBy")) {
            user = parseUser(jsonObject.getJSONObject("measuredBy"));
        }

        ObjectId id = !jsonObject.isNull("_id") && jsonObject.get("_id") instanceof String ?
            new ObjectId(jsonObject.getString("_id")) : new ObjectId();

        return new Measurement(speed, type, provider, location, time, user, id);
    }

    private User parseUser(JSONObject jsonObject) {
        String username = jsonObject.getString("username");
        String password = jsonObject.getString("password");
        String email = jsonObject.getString("email");
        boolean admin = jsonObject.getBoolean("admin");
        ObjectId id = new ObjectId(jsonObject.getString("_id"));
        return new User(username, password, email, admin, id);
    }
}
