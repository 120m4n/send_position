import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Main {
    public static void main(String[] args) {
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

        // create a random position lat in range 7.11 and 7.13 with 4 decimals
        double lat = 7.11 + (7.13 - 7.11) * Math.random();
        lat = Math.round(lat * 10000.0) / 10000.0;

        // create a random position lon in range -73.11 and -73.13 with 4 decimals
        double lon = -73.11 + (-73.13 - -73.11) * Math.random();
        lon = Math.round(lon * 10000.0) / 10000.0;

        // create a position object
        PositionObj position = new PositionObj(lat, lon, fleet, userid);

        makeRequest(end_point + unique_id, position, 2000);



    }

    // generate random string with the format lat:lon
    public static String generateRandomPosition() {
        // create a random position lat in range 7.11 and 7.13 with 4 decimals
        double lat = 7.11 + (7.13 - 7.11) * Math.random();
        lat = Math.round(lat * 10000.0) / 10000.0;

        // create a random position lon in range -73.11 and -73.13 with 4 decimals
        double lon = -73.11 + (-73.13 + 73.11) * Math.random();
        lon = Math.round(lon * 10000.0) / 10000.0;

        return String.format("%f:%f", lat, lon);
    }

    // generate a list of N random positions
    public static List<String> generateRandomPositions(int N) {
        List<String> positions = new ArrayList<String>();
        for (int i = 0; i < N; i++) {
            positions.add(generateRandomPosition());
        }
        return positions;
    }

    // generate a circular iterator from a List
    public static Iterator<String> generateCircularIterator(List<String> list) {
        return new Iterator<String>() {
            private int index = 0;

            @Override
            public boolean hasNext() {
                return true;
            }

            @Override
            public String next() {
                if (index >= list.size()) {
                    index = 0;
                }
                return list.get(index++);
            }
        };
    }

    // make a request to the endpoint with the body and headers every 1000 ms
    public static void makeRequest(String end_point, PositionObj positionObj,int interval) {
        // create a list of random positions
        //List<String> positions = generateRandomPositions(10);
        // create a circular iterator from the list
        //Iterator<String> iterator = generateCircularIterator(positions);
        double x0 = -73.11;
        double y0 = 7.11;
        double x1 = -73.13;
        double y1 = 7.13;
        Iterator<double[]> iterator = Utils.generateLineIterator(x0, y0, x1, y1, 100);
        // create a request object
        request req = new request(end_point, "POST", "", "{'Content-Type':'application/json'}", "");
        // make the request every 1000 ms
        while (true) {
            try {
                // get the next position
                double[] d_position = iterator.next();

                // update positionObj set lat and lon
                positionObj.setLat(d_position[1]);
                positionObj.setLon(d_position[0]);
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