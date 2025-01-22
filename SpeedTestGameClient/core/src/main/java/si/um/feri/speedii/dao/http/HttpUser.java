package si.um.feri.speedii.dao.http;

import io.github.cdimascio.dotenv.Dotenv;
import okhttp3.*;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONObject;

import si.um.feri.speedii.classes.Pair;
import si.um.feri.speedii.classes.SessionManager;
import si.um.feri.speedii.classes.User;
import si.um.feri.speedii.dao.UserCRUD;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HttpUser implements UserCRUD {

    private final OkHttpClient client;

    private final String ip;
    private final SessionManager sessionManager;

    public HttpUser(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
        this.client = new OkHttpClient();

        // Load the IP address from .env
        Dotenv dotenv = Dotenv.configure().filename(".env").load();
        this.ip = dotenv.get("IP");
    }

    //TESTED
    @Override
    public boolean authenticate(String username, String password) {
        RequestBody requestBody = new FormBody.Builder()
            .add("username", username)
            .add("password", password)
            .build();

        Request request = new Request.Builder()
            .url(ip + "/users/login")
            .post(requestBody)
            .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String responseBody = response.body() != null ? response.body().string() : null;
                if (responseBody != null) {
                    JSONObject jsonObject = new JSONObject(responseBody);
                    String token = jsonObject.getString("token");
                    if (token == null) {
                        return false;
                    }
                    User user = parseUser(jsonObject.getJSONObject("user"));
                    sessionManager.start(new Pair<>(token, user));
                    return true;
                }
            } else {
                System.out.println("Login failed: " + response.code());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    //TESTED
    @Override
    public User getById(ObjectId id) {
        String url = ip + "/users/" + id.toString();
        Request request = new Request.Builder()
            .url(url)
            .addHeader("authorization", "Bearer " + sessionManager.getToken())
            .get()
            .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String responseBody = response.body() != null ? response.body().string() : null;
                if (responseBody != null) {
                    JSONObject jsonObject = new JSONObject(responseBody);
                    return parseUser(jsonObject);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //TESTED
    @Override
    public List<User> getAll() {
        String url = ip + "/users";
        Request request = new Request.Builder()
            .url(url)
            .addHeader("authorization", "Bearer " + sessionManager.getToken())
            .get()
            .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String responseBody = response.body() != null ? response.body().string() : null;
                if (responseBody != null) {
                    JSONObject jsonObject = new JSONObject(responseBody);
                    JSONArray usersArray = jsonObject.getJSONArray("users");
                    List<User> users = new ArrayList<>();
                    for (int i = 0; i < usersArray.length(); i++) {
                        JSONObject userObject = usersArray.getJSONObject(i);
                        users.add(parseUser(userObject));
                    }
                    return users;
                }
            } else {
                System.out.println("Failed to execute request: " + response.code());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    //TESTED
    @Override
    public boolean insert(User obj) {
        RequestBody requestBody = new FormBody.Builder()
            .add("username", obj.getUsername())
            .add("password", obj.getPassword())
            .add("email", obj.getEmail())
            .add("admin", String.valueOf(obj.isAdmin()))
            .build();

        Request request = new Request.Builder()
            .url(ip + "/users/register")
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

    //TESTED
    @Override
    public boolean update(User obj) {
        String url = ip + "/users/" + obj.getId().toString();
        RequestBody requestBody = new FormBody.Builder()
            .add("username", obj.getUsername())
            .add("password", obj.getPassword())
            .add("email", obj.getEmail())
            .add("admin", String.valueOf(obj.isAdmin()))
            .build();

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

    //TESTED
    @Override
    public boolean delete(User obj) {
        String url = ip + "/users/" + obj.getId().toString();
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

    private User parseUser(JSONObject jsonObject) {
        String username = jsonObject.getString("username");
        String password = jsonObject.getString("password");
        String email = jsonObject.getString("email");
        boolean admin = jsonObject.getBoolean("admin");
        ObjectId id = new ObjectId(jsonObject.getString("_id"));
        return new User(username, password, email, admin, id);
    }
}
