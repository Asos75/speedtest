package si.um.feri.speedii.dao.http;


import io.github.cdimascio.dotenv.Dotenv;
import si.um.feri.speedii.classes.Location;
import si.um.feri.speedii.classes.Measurement;
import si.um.feri.speedii.classes.MobileTower;
import si.um.feri.speedii.classes.SessionManager;
import si.um.feri.speedii.classes.Type;
import si.um.feri.speedii.classes.User;
import si.um.feri.speedii.dao.MeasurementCRUD;
import okhttp3.*;
import si.um.feri.speedii.dao.MobileTowerCRUD;

import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HttpMobileTower implements MobileTowerCRUD {
    private final OkHttpClient client;
    private final SessionManager sessionManager;
    private final String ip;

    public HttpMobileTower(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
        this.client = new OkHttpClient();

        Dotenv dotenv = Dotenv.configure().filename(".env").load();
        this.ip = dotenv.get("IP");
    }

    @Override
    public List<MobileTower> getByConfirmed(boolean status) {
        String url = ip + "/mobile/confirmed/" + status;
        Request request = new Request.Builder()
            .url(url)
            .get()
            .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                System.out.println("Failed to execute request: " + response.code());
                return new ArrayList<>();
            }

            String responseBody = response.body() != null ? response.body().string() : null;
            if (responseBody != null) {
                JSONArray jsonArray = new JSONArray(responseBody);
                List<MobileTower> mobileTowers = new ArrayList<>();

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    mobileTowers.add(parseMobileTower(jsonObject));
                }

                return mobileTowers;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }



    @Override
    public boolean toggleConfirm(MobileTower obj) {
        String url = ip + "/mobile/confirm/" + obj.getId();
        Request request = new Request.Builder()
            .url(url)
            .addHeader("authorization", "Bearer " + sessionManager.getToken())
            .get()
            .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                obj.setConfirmed(!obj.isConfirmed());
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public MobileTower getById(ObjectId id) {
        String url = ip + "/mobile/" + id;
        Request request = new Request.Builder()
            .url(url)
            .get()
            .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String responseBody = response.body() != null ? response.body().string() : null;
                if (responseBody != null) {
                    JSONObject jsonObject = new JSONObject(responseBody);
                    return parseMobileTower(jsonObject);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    // TESTED
    public List<MobileTower> getByLocator(User user) {
        String url = ip + "/mobile";
        Request request = new Request.Builder()
            .url(url)
            .get()
            .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                System.out.println("Failed to execute request: " + response.code());
                return new ArrayList<>();
            }

            String responseBody = response.body() != null ? response.body().string() : null;
            if (responseBody != null) {
                JSONArray jsonArray = new JSONArray(responseBody);
                List<MobileTower> mobileTowers = new ArrayList<>();

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    MobileTower mobileTower = parseMobileTower(jsonObject);

                    if (mobileTower.getLocator() != null && mobileTower.getLocator().getId().equals(user.getId())) {
                        mobileTowers.add(mobileTower);
                    }
                }

                return mobileTowers;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    // TESTED
    @Override
    public List<MobileTower> getAll() {
        String url = ip + "/mobile";
        Request request = new Request.Builder()
            .url(url)
            .get()
            .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                System.out.println("Failed to execute request: " + response.code());
                return new ArrayList<>();
            }

            String responseBody = response.body() != null ? response.body().string() : null;
            if (responseBody != null) {
                JSONArray jsonArray = new JSONArray(responseBody);
                List<MobileTower> mobileTowers = new ArrayList<>();

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    mobileTowers.add(parseMobileTower(jsonObject));
                }

                return mobileTowers;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    @Override
    public boolean insert(MobileTower obj) {
        String url = ip + "/mobile";
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
    public boolean insertMany(List<MobileTower> list) {
        JSONArray jsonArray = new JSONArray();
        for (MobileTower tower : list) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("location", new JSONObject()
                .put("type", "Point")
                .put("coordinates", tower.getLocation().coordinates));
            jsonObject.put("operator", tower.getProvider());
            jsonObject.put("type", tower.getType());
            jsonObject.put("confirmed", tower.isConfirmed());
            jsonObject.put("locator", tower.getLocator() != null ? tower.getLocator().toString() : null);
            jsonArray.put(jsonObject);
        }

        JSONObject jsonTowers = new JSONObject().put("towers", jsonArray);
        RequestBody requestBody = RequestBody.create(jsonTowers.toString(), MediaType.get("application/json"));
        Request request = new Request.Builder()
            .url(ip + "/mobileTowerRoutes/createMany")
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
    public boolean update(MobileTower obj) {
        String url = ip + "/mobile/" + obj.getId();
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
    public boolean delete(MobileTower obj) {
        String url = ip + "/mobile/" + obj.getId();
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

    private RequestBody createRequestBody(MobileTower obj) {
        JSONObject json = new JSONObject()
            .put("location", new JSONObject()
                .put("type", "Point")
                .put("coordinates", obj.getLocation().coordinates))
            .put("operator", obj.getProvider())
            .put("type", obj.getType())
            .put("confirmed", obj.isConfirmed());
        if (obj.getLocator() != null) {
            json.put("locator", obj.getLocator().getId());
        }

        return RequestBody.create(json.toString(), MediaType.get("application/json"));
    }

    private MobileTower parseMobileTower(JSONObject jsonObject) {
        JSONObject locationObject = jsonObject.getJSONObject("location");
        JSONArray coordinates = locationObject.getJSONArray("coordinates");
        Location location = new Location(List.of(coordinates.getDouble(0), coordinates.getDouble(1)));
        String operator = jsonObject.getString("operator");
        String type = jsonObject.getString("type");
        boolean confirmed = jsonObject.getBoolean("confirmed");
        User locator = jsonObject.isNull("locator") ? null : parseUser(jsonObject.getJSONObject("locator"));
        ObjectId id = jsonObject.has("_id") ? new ObjectId(jsonObject.getString("_id")) : new ObjectId();

        return new MobileTower(location, operator, type, confirmed, locator, id);
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

