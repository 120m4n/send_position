
import com.mapbox.services.commons.geojson.Feature;
import com.mapbox.services.commons.geojson.FeatureCollection;
import com.mapbox.services.commons.geojson.LineString;
import com.mapbox.services.commons.geojson.Polygon;
import com.mapbox.services.commons.models.Position;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class test {
    public static void main(String[] args) throws Exception {
        /*JSONObject obj = new JSONObject();
        obj.put("name", "foo");
        obj.put("num", 100);
        obj.put("balance", 1000.21);
        obj.put("is_vip",true);
        System.out.print(obj.toString());*/

        try {
            //read json file data to String
            String jsonString = new String(Files.readAllBytes(Paths.get("/Users/120m4n/IdeaProjects/send_position/src/map-3.geojson")));

            //create feature collection
            FeatureCollection featureCollectionFromJson = FeatureCollection.fromJson(jsonString);
            if (featureCollectionFromJson.getFeatures() != null) {
                for (Feature singleFeature : featureCollectionFromJson.getFeatures()) {
                    if (singleFeature.getGeometry() instanceof LineString) {
                         // print "is a LineString geometry"
                        System.out.println("is a LineString geometry");
                    }
                }
            }

            LineString line = (LineString) featureCollectionFromJson.getFeatures().get(0).getGeometry();
            List<Position> positionList = line.getCoordinates();



            //generate List of Lines
            List<Line> lines = getConsecutivePairsAsLine(positionList);
            List<double[]> pointsRoute = Utils.generateRoute(lines, 5);

            //print points
            for (double[] point : pointsRoute) {
                System.out.println(point[0] + " " + point[1]);
            }




        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    // given a List<Position> group them into pairs 0-1, 1-2, 2-3, etc.
    public static List<Position> getConsecutivePairs(List<Position> list) {
        List<Position> pairs = new ArrayList<>();
        for (int i = 0; i < list.size() - 1; i++) {
            pairs.add(list.get(i));
            pairs.add(list.get(i + 1));
        }
        return pairs;
    }

    // given a List<Position> group them into pairs 0-1, 1-2, 2-3, return List<Line>
    public static List<Line> getConsecutivePairsAsLine(List<Position> list) {
        List<Line> pairs = new ArrayList<>();
        for (int i = 0; i < list.size() - 1; i++) {
            pairs.add(new Line(list.get(i).getLongitude(),list.get(i).getLatitude(), list.get(i + 1).getLongitude(), list.get(i + 1).getLatitude()));
        }
        return pairs;
    }



}
