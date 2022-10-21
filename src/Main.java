import java.util.Iterator;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        int invertalArg = 600;

        String end_point = Utils.getEnv("END_POINT");
        // check if end point is set otherwise exit
        if (end_point == null) {
            System.out.println("END_POINT environment variable not set");
            System.exit(1);
        }

        String unique_id = "df02bd315336e12a";
        String[] userids = {"G2022-roman", "G2023-roman", "G2024-roman"};
        String[] fleets = {"avatar", "avatar2", "avatar3"};

        // select a random user id and fleet
        String userid = userids[(int) (Math.random() * userids.length)];
        String fleet = fleets[(int) (Math.random() * fleets.length)];

        double lat = 0;
        double lon = 0;

        // create a position object
        PositionObj position = new PositionObj(lat, lon, fleet, userid);

        makeRequest(end_point + unique_id, position, 600);

    }


    // make a request to the endpoint with the body and headers every 1000 ms
    public static void makeRequest(String end_point, PositionObj positionObj,int interval) {
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
        List<double[]> points = Utils.generateTriangle(home_xy[0], home_xy[1], work_xy[0], work_xy[1], restaurant_xy[0], restaurant_xy[1], 10);


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

                // print the body and exit
                System.out.println(body);
                //System.exit(0);

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