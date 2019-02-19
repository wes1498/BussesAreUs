package ca.ubc.cs.cpsc210.translink.parsers;

import ca.ubc.cs.cpsc210.translink.model.*;
import ca.ubc.cs.cpsc210.translink.parsers.exception.ArrivalsDataMissingException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A parser for the data returned by the Translink arrivals at a stop query
 */
public class ArrivalsParser {

    /**
     * Parse arrivals from JSON response produced by TransLink query.  All parsed arrivals are
     * added to the given stop assuming that corresponding JSON object has a RouteNo: and an
     * array of Schedules:
     * Each schedule must have an ExpectedCountdown, ScheduleStatus, and Destination.  If
     * any of the aforementioned elements is missing, the arrival is not added to the stop.
     *
     * @param stop             stop to which parsed arrivals are to be added
     * @param jsonResponse    the JSON response produced by Translink
     * @throws JSONException  when:
     * <ul>
     *     <li>JSON response does not have expected format (JSON syntax problem)</li>
     *     <li>JSON response is not an array</li>
     * </ul>
     * @throws ArrivalsDataMissingException  when no arrivals are found in the reply
     */
    public static void parseArrivals(Stop stop, String jsonResponse)
            throws JSONException, ArrivalsDataMissingException {
        JSONArray rawArrivals = new JSONArray(
                jsonResponse);
        boolean success = true;
        for (int i = 0; i < rawArrivals.length(); i++) {
            JSONObject rawArrival = rawArrivals.getJSONObject(i);

            try {
                String routeNo = rawArrival.getString("RouteNo");
                JSONArray routeSchedule = rawArrival.getJSONArray("Schedules");

                if (routeNo == null)
                    throw new ArrivalsDataMissingException();

                Route route = RouteManager.getInstance().getRouteWithNumber(routeNo);

                for (int a = 0; a < routeSchedule.length(); a++) {
                    JSONObject schedule = routeSchedule.getJSONObject(a);
                    try {

                        Integer arrivalExpectedCountdown = (schedule.isNull("ExpectedCountdown")) ? null : schedule.getInt("ExpectedCountdown");
                        String arrivalDestination = schedule.getString("Destination");
                        String arrivalStatus = schedule.getString("ScheduleStatus");

                        if (arrivalExpectedCountdown == null || arrivalDestination == null || arrivalStatus == null)
                            throw new ArrivalsDataMissingException();

                        Arrival arrival = new Arrival(arrivalExpectedCountdown, arrivalDestination, route);
                        stop.addArrival(arrival);
                    } catch (Exception e){
                        success = false;
                    }

                }
            } catch (Exception e){
                success = false;
            }
        }
        if (!success)
            throw new ArrivalsDataMissingException();
    }
}
