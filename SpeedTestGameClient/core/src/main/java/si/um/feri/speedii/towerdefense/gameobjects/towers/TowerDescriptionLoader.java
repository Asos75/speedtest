package si.um.feri.speedii.towerdefense.gameobjects.towers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

import java.util.ArrayList;
import java.util.List;

import java.util.Map;

public class TowerDescriptionLoader {
    public static List<TowerDescription> loadDescriptions() {
        List<TowerDescription> descriptions = new ArrayList<>();
        FileHandle file = Gdx.files.internal("assets/tower_descriptions.json");
        Json json = new Json();
        JsonValue root = json.fromJson(null, file);

        for (JsonValue towerJson : root.get("towers")) {
            String name = towerJson.getString("name");
            String description = towerJson.getString("description");
            descriptions.add(new TowerDescription(name, description));
            //Gdx.app.log("TowerDescriptionLoader", "Loaded description for " + name + ": " + description);
        }
        return descriptions;
    }
}
