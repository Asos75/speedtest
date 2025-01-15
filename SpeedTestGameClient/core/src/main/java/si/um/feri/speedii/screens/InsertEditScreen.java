package si.um.feri.speedii.screens;

import org.bson.types.ObjectId;

import si.um.feri.speedii.classes.MobileTower;
import si.um.feri.speedii.classes.User;
import si.um.feri.speedii.dao.http.HttpMeasurement;
import si.um.feri.speedii.classes.Measurement;
import si.um.feri.speedii.dao.http.HttpMobileTower;
import si.um.feri.speedii.dao.http.HttpUser;

import java.io.IOException;
import java.util.List;

public class InsertEditScreen {

    private HttpMeasurement httpMeasurement;
    private HttpMobileTower httpMobileTower;
    private HttpUser httpUser;

    // Konstruktor za inicializacijo HttpMeasurement in HttpMobileTower
    public InsertEditScreen(HttpMeasurement httpMeasurement, HttpMobileTower httpMobileTower, HttpUser httpUser) {
        this.httpMeasurement = httpMeasurement;
        this.httpMobileTower = httpMobileTower;
        this.httpUser = httpUser;
    }

    // Prikaz vseh meritev in mobilnih stolpov
    public void showAllMeasurements() {
        try {
            ObjectId objectId = new ObjectId("664751010ed2fc39038d7913");
            User user =  httpUser.getById(objectId);
            System.out.println(user);
            // Pridobi vse meritve
            List<Measurement> measurements = httpMeasurement.getAll();
            // Pridobi vse mobilne stolpe
          //  List<MobileTower> mobileTowers = httpMobileTower.getByLocator(user);

            // Izpis meritev

          /*  if (measurements.isEmpty()) {
                System.out.println("No measurements found.");
            } else {
                System.out.println("Measurements:");
                for (Measurement measurement : measurements) {
                    System.out.println(measurement);
                }
            }



            // Izpis mobilnih stolpov
            if (mobileTowers.isEmpty()) {
                System.out.println("No mobile towers found.");
            } else {
                System.out.println("Mobile Towers:");
                for (MobileTower tower : mobileTowers) {
                    System.out.println(tower);
                }
            }

           */
        } catch (IOException e) {
            System.err.println("Failed to fetch data: " + e.getMessage());
        }
    }
}
