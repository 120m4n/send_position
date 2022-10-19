import org.locationtech.proj4j.*;

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





}
