package si.um.feri.speedii.towerdefense.gameobjects.towers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

import java.util.Map;

public class TowerDescriptionLoader {
    public static void loadDescriptions(Map<String, Tower> towerTypes) {
        FileHandle file = Gdx.files.internal("tower_descriptions.json");
        Json json = new Json();
        JsonValue root = json.fromJson(null, file.readString());

        for (JsonValue towerJson : root.get("towers")) {
            String name = towerJson.getString("name");
            String description = towerJson.getString("description");

            Tower tower = towerTypes.get(name);
            if (tower != null) {
                tower.setDescription(description);
            }
        }
    }
}
