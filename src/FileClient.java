import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.Iterator;
import java.util.List;

@Command(name = "fileCli", description = "Performs file manipulation operations", mixinStandardHelpOptions = true, version = "File Client 1.0")
public class FileClient implements Runnable {

        @Option(names = { "-u", "--uniqueid" }, description = "Unique ID of the file", required = false)
        private String unique_id;

        @Option(names = { "-f", "--fleet" }, description = "Define the fleet", required = false)
        private String fleet;

        @Option(names = { "-i", "--interval" }, description = "Define the interval", required = false)
        private int interval;

        @Option(names = { "-e", "--endpoint" }, description = "Define the endpoint", required = false)
        private String endpoint;

        // option number of points to generate
        @Option(names = { "-n", "--number" }, description = "Define the number of points to generate", required = false)
        private int number;

        //option debug mode
        @Option(names = { "-d", "--debug" }, description = "Debug mode", required = false)
        private boolean debug;

        //option to read geojso file
        @Option(names = { "-g", "--geojson" }, description = "Geojson file", required = false)
        private String geojson;

        public static void main(String... args) throws Exception {
                int exitCode = new CommandLine(new FileClient()).execute(args);
                System.exit(exitCode);
        }


        public String call() throws Exception {
            //check if uniqueid is not null otherwise equal to df02bd315336e12a
            if(unique_id == null){
                unique_id = "df02bd315336e12b";
                //print the unique id
                System.out.println("Unique ID: " + unique_id);
            }
            else{
                //print the unique id
                System.out.println("Unique ID: " + unique_id);
            }
            //if type is null then type equals to avatar
            if(fleet == null){
                fleet = "avatar";
            }
            //if interval is null then interval equals to 600
            if(interval == 0){
                interval = 600;
            }

            //if endpoint is null then endpoint equals to http://localhost:8080/position/
            if(endpoint == null){
                endpoint = "https://localhost:8080/api/coordinates/";
            }

            //if number is null then number equals to 10
            if(number == 0){
                number = 10;
            }

            //if geojson is not null check if file exists otherwise exit
            if(geojson != null){
                if(!Utils.fileExists(geojson)){
                    System.out.println("File does not exist");
                    System.exit(1);
                }else{

                    Utils.makeRequest(endpoint + unique_id, geojson, interval, number, debug);

                }
            }


            String[] userids = {"G2022-roman", "G2023-roman", "G2024-roman"};
            // select a random userid
            String userid = userids[(int) (Math.random() * userids.length)];
            // create a position object
            //PositionObj position = new PositionObj(0, 0, fleet, userid);

            // make a request to the endpoint with the body and headers every 1000 ms
            //Utils.makeRequest(endpoint + unique_id, position, interval, number, debug);

            return "success";
        }

        @Override
        public void run() {
            try
            {
                call();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

        }

}
