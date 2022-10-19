import org.locationtech.proj4j.*;

import java.util.Iterator;

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

    /* given two pair of coordinates, (x1,y1) (x2, y2) return the line between them */
    public static double[] getLine(double x1, double y1, double x2, double y2) {
        double m = (y2 - y1) / (x2 - x1);
        double b = y1 - m * x1;
        return new double[] { m, b };
    }

    /* create a iterator yielding points in a line */
    public static Iterator<double[]> generateLineIterator(double x1, double y1, double x2, double y2, int N) {
        double[] line = getLine(x1, y1, x2, y2);
        double m = line[0];
        double b = line[1];
        double dx = (x2 - x1) / N;
        return new Iterator<double[]>() {
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
                return new double[] { x, y };
            }
        };
    }

    /* create a circular iterator yielding points in a line */
    public static Iterator<double[]> generateCircularLineIterator(double x1, double y1, double x2, double y2, int N) {
        double[] line = getLine(x1, y1, x2, y2);
        double m = line[0];
        double b = line[1];
        double dx = (x2 - x1) / N;
        return new Iterator<double[]>() {
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
                return new double[] { x, y };
            }
        };
    }

    // given a lat lon pair, return the corresponding epsg:3116 coordinates
    public static double[] getEpsg3116Coordinates(double lat, double lon) {
        double[] xy = Utils.wgs84ToEpsg3116(lat, lon);
        return new double[] { xy[0], xy[1] };
    }

    // given a pair of lat lon coordinates, return a iterator yielding points in a line between them
    public static Iterator<double[]> getLineIterator(double lat1, double lon1, double lat2, double lon2, int N) {
        double[] xy1 = Utils.wgs84ToEpsg3116(lat1, lon1);
        double[] xy2 = Utils.wgs84ToEpsg3116(lat2, lon2);
        return Utils.generateLineIterator(xy1[0], xy1[1], xy2[0], xy2[1], N);
    }

    // return environment variable value
    public static String getEnv(String name) {
        return System.getenv(name);
    }





}
