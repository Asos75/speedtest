package si.um.feri.speedii.utils;

import io.github.cdimascio.dotenv.Dotenv;

public class Keys {
    public static String MAPBOX = "";
    public static String GEOAPIFY = "";

    static {
        Dotenv dotenv = Dotenv.load();

        GEOAPIFY = dotenv.get("GEOAPIFY_API_KEY");
    }
}
