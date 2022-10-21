import com.mapbox.services.commons.geojson.Feature;
import com.mapbox.services.commons.geojson.FeatureCollection;
import com.mapbox.services.commons.geojson.LineString;
import com.mapbox.services.commons.models.Position;
import org.locationtech.proj4j.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Utils {

    // convert coordinates from wgs84 to epsg:3116
    public static double[] wgs84ToEpsg3116(double lat, double lon) {
        CRSFactory crsFactory = new CRSFactory();
        CoordinateReferenceSystem WGS84 = crsFactory.createFromName("epsg:4326");
        CoordinateReferenceSystem UTM = crsFactory.createFromName("epsg:3116");

        CoordinateTransformFactory ctFactory = new CoordinateTransformFactory();
        CoordinateTransform wgsToUtm = ctFactory.createTransform(WGS84, UTM);
        // `result` is an output parameter to `transform()`
        ProjCoordinate result = new ProjCoordinate();
        wgsToUtm.transform(new ProjCoordinate(lon, lat), result);
        return new double[] { result.x, result.y };
    }

    // convert coordinates from epsg:3116 to wgs84
    public static double[] epsg3116ToWgs84(double x, double y) {
        CRSFactory crsFactory = new CRSFactory();
        CoordinateReferenceSystem WGS84 = crsFactory.createFromName("epsg:4326");
        CoordinateReferenceSystem UTM = crsFactory.createFromName("epsg:3116");

        CoordinateTransformFactory ctFactory = new CoordinateTransformFactory();
        CoordinateTransform utmToWgs = ctFactory.createTransform(UTM, WGS84);
        // `result` is an output parameter to `transform()`
        ProjCoordinate result = new ProjCoordinate();
        utmToWgs.transform(new ProjCoordinate(y, x), result);
        return new double[] { result.x, result.y };
    }

    /* given two pair of coordinates, (x1,y1) (x2, y2) return the line between them */
    public static double[] getLine(double x1, double y1, double x2, double y2) {
        double m = (y2 - y1) / (x2 - x1);
        double b = y1 - m * x1;
        return new double[] { m, b };
    }

    /* create a circular iterator yielding points in a line */
    public static Iterator<double[]> generateLineIterator(double x1, double y1, double x2, double y2, int N) {
        double[] line = getLine(x1, y1, x2, y2);
        double m = line[0];
        double b = line[1];
        double dx = (x2 - x1) / N;
        return new Iterator<>() {
            private int index = 0;

            @Override
            public boolean hasNext() {
                return true;
            }

            @Override
            public double[] next() {
                double x = x1 + index * dx;
                double y = m * x + b;
                index++;
                // return the coordinates in wgs84
                //return epsg3116ToWgs84(y, x);
                return new double[]{x, y};
            }
        };
    }

    public static List<double[]> generateLine(double x1, double y1, double x2, double y2, int N) {
        List<double[]> lineList = new ArrayList<>();
        double[] line = getLine(x1, y1, x2, y2);
        double m = line[0];
        double b = line[1];
        double dx = (x2 - x1) / N;
        for (int i = 0; i < N; i++) {
            double x = x1 + i * dx;
            double y = m * x + b;
            lineList.add(new double[] { x, y });
        }
        return lineList;
    }

    /* return List<double[]> of points in a triangle given three points not Iterator*/
    public static List<double[]> generateTriangle(double x1, double y1, double x2, double y2, double x3, double y3, int N) {
        List<double[]> triangle = new ArrayList<>();
        List<double[]> it1 = generateLine(x1, y1, x2, y2, N);
        List<double[]> it2 = generateLine(x2, y2, x3, y3, N);
        List<double[]> it3 = generateLine(x3, y3, x1, y1, N);
        triangle.addAll(it1);
        triangle.addAll(it2);
        triangle.addAll(it3);

        return triangle;
    }

    /* return List<double[]> of points given a List<Line> */
    public static List<double[]> generateRoute(List<Line> lines, int N) {
        List<double[]> route = new ArrayList<>();
        for (Line line : lines) {
            route.addAll(generateLine(line.getX1(), line.getY1(), line.getX2(), line.getY2(), N));
        }
        return route;
    }



    /* given a List<double[]> convert to a Circular Iterator<double[]> */
    public static Iterator<double[]> generateCircularIterator(List<double[]> list) {
        return new Iterator<>() {
            private int index = 0;

            @Override
            public boolean hasNext() {
                return true;
            }

            @Override
            public double[] next() {
                double[] point = list.get(index);
                index = (index + 1) % list.size();
                return point;
            }
        };
    }

    /* create a circular iterator yielding points in a line */
    public static Iterator<double[]> generateCircularLineIterator(double x1, double y1, double x2, double y2, int N) {
        double[] line = getLine(x1, y1, x2, y2);
        double m = line[0];
        double b = line[1];
        double dx = (x2 - x1) / N;
        return new Iterator<>() {
            private int index = 0;

            @Override
            public boolean hasNext() {
                return true;
            }

            @Override
            public double[] next() {
                if (index >= N) {
                    index = 0;
                }
                double x = x1 + index * dx;
                double y = m * x + b;
                index++;
                return epsg3116ToWgs84(y, x);
                //return new double[] { x, y };
            }
        };
    }

    // given a lat lon pair, return the corresponding epsg:3116 coordinates
    public static double[] getEpsg3116Coordinates(double lat, double lon) {
        double[] xy = Utils.wgs84ToEpsg3116(lat, lon);
        return new double[] { xy[0], xy[1] };
    }


    // return environment variable value
    public static String getEnv(String name) {
        return System.getenv(name);
    }


    public static boolean fileExists(String geojson) {
        File file = new File(geojson);
        return file.exists();

    }

    public static List<Line> readGeojson(String geojson) {
        try{
            //read json file data to String
            String jsonString = new String(Files.readAllBytes(Paths.get(geojson)));
            //create feature collection
            FeatureCollection featureCollectionFromJson = FeatureCollection.fromJson(jsonString);
            if (featureCollectionFromJson.getFeatures() != null) {
                for (Feature singleFeature : featureCollectionFromJson.getFeatures()) {
                    if (singleFeature.getGeometry() instanceof LineString) {
                        // print "is a LineString geometry"
                        System.out.println("is a LineString geometry");
                    } else {
                        // print "is not a LineString geometry" and exit
                        System.out.println("is not a LineString geometry");
                        System.exit(1);
                    }
                }
            }

            LineString line = (LineString) featureCollectionFromJson.getFeatures().get(0).getGeometry();
            List<Position> positionList = line.getCoordinates();
            //generate List of Lines
            List<Line> lines = getConsecutivePairsAsLine(positionList);
            return lines;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // given a List<Position> group them into pairs 0-1, 1-2, 2-3, return List<Line>
    public static List<Line> getConsecutivePairsAsLine(List<Position> list) {
        List<Line> pairs = new ArrayList<>();
        for (int i = 0; i < list.size() - 1; i++) {
            pairs.add(new Line(list.get(i).getLongitude(),list.get(i).getLatitude(), list.get(i + 1).getLongitude(), list.get(i + 1).getLatitude()));
        }
        return pairs;
    }

    // make a request to the endpoint with the body and headers every 1000 ms
    public static void makeRequest(String end_point, PositionObj positionObj, int interval, int number, boolean debug) {
        // Home point lat, lon
        double home_lat = 7.1393715212008;
        double home_lon = -73.13347639077342;
        // convert to epsg:3116
        double[] home_xy = Utils.wgs84ToEpsg3116(home_lat, home_lon);


        // Work point lat, lon
        double work_lat = 7.128905781413465;
        double work_lon = -73.13964562270147;
        // convert to epsg:3116
        double[] work_xy = Utils.wgs84ToEpsg3116(work_lat, work_lon);


        // Restaurant point lat, lon
        double restaurant_lat = 7.1299918598744085;
        double restaurant_lon = -73.1263121214377;
        // convert to epsg:3116
        double[] restaurant_xy = Utils.wgs84ToEpsg3116(restaurant_lat, restaurant_lon);


        // create a list of points in triangle home-work-restaurant
        List<double[]> points = Utils.generateTriangle(home_xy[0], home_xy[1], work_xy[0], work_xy[1], restaurant_xy[0], restaurant_xy[1], number);


        Iterator<double[]> iterator = Utils.generateCircularIterator(points);
        // create a request object
        request req = new request(end_point, "POST", "", "{'Content-Type':'application/json'}", "");
        // make the request every 1000 ms
        while (true) {
            try {
                // get the next position
                double[] d_position = iterator.next();

                // convert to wgs84
                double[] wgs84_position = Utils.epsg3116ToWgs84(d_position[1], d_position[0]);

                // update positionObj set lat and lon
                positionObj.setLat(wgs84_position[1]);
                positionObj.setLon(wgs84_position[0]);
                // create the body
                String body = positionObj.toJSONString();
                // only print if debug is true
                if (debug) {
                    System.out.println(body);
                }

                req.setBody(body);
                // make the request
                String response = req.makeRequest();
                System.out.println(response);
                // sleep for 1000 ms
                Thread.sleep(interval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void makeRequest(String end_point, String geojson, int interval, int number, boolean debug){
        //read geojson file
        List<Line> lines = Utils.readGeojson(geojson);
        //set the number of points sampled
        List<double[]> points = Utils.generateRoute(lines, number);

        Iterator<double[]> iterator = Utils.generateCircularIterator(points);

        PositionObj positionObj = new PositionObj(0, 0, "avatar", "JAVA-test");
        // create a request object
        request req = new request(end_point, "POST", "", "{'Content-Type':'application/json'}", "");
        // make the request every 1000 ms
        while (true) {
            try {
                // get the next position
                double[] d_position = iterator.next();

                // convert to wgs84
                //double[] wgs84_position = Utils.epsg3116ToWgs84(d_position[1], d_position[0]);

                // update positionObj set lat and lon
                positionObj.setLat(d_position[1]);
                positionObj.setLon(d_position[0]);
                // create the body
                String body = positionObj.toJSONString();
                // only print if debug is true
                if (debug) {
                    System.out.println(body);
                }

                req.setBody(body);
                // make the request
                String response = req.makeRequest();
                System.out.println(response);
                // sleep for 1000 ms
                Thread.sleep(interval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
