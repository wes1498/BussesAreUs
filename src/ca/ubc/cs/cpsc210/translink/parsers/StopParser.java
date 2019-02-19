package ca.ubc.cs.cpsc210.translink.parsers;

import ca.ubc.cs.cpsc210.translink.model.Route;
import ca.ubc.cs.cpsc210.translink.model.RouteManager;
import ca.ubc.cs.cpsc210.translink.model.Stop;
import ca.ubc.cs.cpsc210.translink.model.StopManager;
import ca.ubc.cs.cpsc210.translink.parsers.exception.StopDataMissingException;
import ca.ubc.cs.cpsc210.translink.providers.DataProvider;
import ca.ubc.cs.cpsc210.translink.providers.FileDataProvider;
import ca.ubc.cs.cpsc210.translink.util.LatLon;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A parser for the data returned by Translink stops query
 */
public class StopParser {

    private String filename;

    public StopParser(String filename) {
        this.filename = filename;
    }
    /**
     * Parse stop data from the file and add all stops to stop manager.
     *
     */
    public void parse() throws IOException, StopDataMissingException, JSONException{
        DataProvider dataProvider = new FileDataProvider(filename);

        parseStops(dataProvider.dataSourceToString());
    }
    /**
     * Parse stop information from JSON response produced by Translink.
     * Stores all stops and routes found in the StopManager and RouteManager.
     *
     * @param  jsonResponse    string encoding JSON data to be parsed
     * @throws JSONException when:
     * <ul>
     *     <li>JSON data does not have expected format (JSON syntax problem)</li>
     *     <li>JSON data is not an array</li>
     * </ul>
     * If a JSONException is thrown, no stops should be added to the stop manager
     * @throws StopDataMissingException when
     * <ul>
     *  <li> JSON data is missing Name, StopNo, Routes or location (Latitude or Longitude) elements for any stop</li>
     * </ul>
     * If a StopDataMissingException is thrown, all correct stops are first added to the stop manager.
     */

    public void parseStops(String jsonResponse)
            throws JSONException, StopDataMissingException {
        JSONArray rawStops = new JSONArray(
                jsonResponse);
        boolean success = true;
        for(int i = 0; i < rawStops.length(); i++) {
            JSONObject rawStop = rawStops.getJSONObject(i);
            try {
                String stopName = rawStop.getString("Name");
                Integer stopNo = rawStop.isNull("StopNo") ? null : rawStop.getInt("StopNo");
                Double stopLatitude = rawStop.isNull("Latitude") ? null : rawStop.getDouble("Latitude");
                Double stopLongitude = rawStop.isNull("Longitude") ? null : rawStop.getDouble("Longitude");
                String stopRoutes = rawStop.getString("Routes");

                if (stopNo == null || stopLatitude == null || stopLongitude == null || stopRoutes == null)
                    throw new StopDataMissingException();

                Pattern rPattern = Pattern.compile("[^,\\s][^\\,]*[^,\\s]*");
                Matcher rMatcher = rPattern.matcher(stopRoutes);

                Stop stop = StopManager.getInstance().getStopWithNumber(stopNo, stopName, new LatLon(stopLatitude, stopLongitude));

                while(rMatcher.find()){
                    String stopRoute = rMatcher.group(0);

                    Route route = RouteManager.getInstance().getRouteWithNumber(stopRoute);
                    stop.addRoute(route);
                    route.addStop(stop);
                }
            } catch (Exception e){
                success = false;
            }
        }
        if(!success)
            throw new StopDataMissingException();
    }
}
