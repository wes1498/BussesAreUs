package ca.ubc.cs.cpsc210.translink.parsers;

import ca.ubc.cs.cpsc210.translink.model.Route;
import ca.ubc.cs.cpsc210.translink.model.RouteManager;
import ca.ubc.cs.cpsc210.translink.model.RoutePattern;
import ca.ubc.cs.cpsc210.translink.parsers.exception.RouteDataMissingException;
import ca.ubc.cs.cpsc210.translink.providers.DataProvider;
import ca.ubc.cs.cpsc210.translink.providers.FileDataProvider;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Parse route information in JSON format.
 */
public class RouteParser {
    private String filename;

    public RouteParser(String filename) {
        this.filename = filename;
    }
    /**
     * Parse route data from the file and add all route to the route manager.
     *
     */
    public void parse() throws IOException, RouteDataMissingException, JSONException{
        DataProvider dataProvider = new FileDataProvider(filename);

        parseRoutes(dataProvider.dataSourceToString());
    }
    /**
     * Parse route information from JSON response produced by Translink.
     * Stores all routes and route patterns found in the RouteManager.
     *
     * @param  jsonResponse    string encoding JSON data to be parsed
     * @throws JSONException   when:
     * <ul>
     *     <li>JSON data does not have expected format (JSON syntax problem)
     *     <li>JSON data is not an array
     * </ul>
     * If a JSONException is thrown, no stops should be added to the stop manager
     *
     * @throws RouteDataMissingException when
     * <ul>
     *  <li>JSON data is missing RouteNo, Name, or Patterns element for any route</li>
     *  <li>The value of the Patterns element is not an array for any route</li>
     *  <li>JSON data is missing PatternNo, Destination, or Direction element for any route pattern</li>
     * </ul>
     * If a RouteDataMissingException is thrown, all correct routes are first added to the route manager.
     */

    public void parseRoutes(String jsonResponse)
            throws JSONException, RouteDataMissingException {
        JSONArray rawRoutes = new JSONArray(
                jsonResponse);
        boolean success = true;
        for(int i = 0; i < rawRoutes.length(); i++){
            JSONObject rawRoute = rawRoutes.getJSONObject(i);
            try {
                String routeNo = rawRoute.getString("RouteNo");
                String routeName = rawRoute.getString("Name");
                JSONArray routePatterns = rawRoute.getJSONArray("Patterns");

                if (routeNo == null || routeName == null || routePatterns == null)
                    throw new RouteDataMissingException();

                Route route = RouteManager.getInstance().getRouteWithNumber(routeNo, routeName);
                for (int p = 0; p < routePatterns.length(); p++) {
                    JSONObject rawPattern = routePatterns.getJSONObject(p);
                    try {
                        String patternNo = rawPattern.getString("PatternNo");
                        String patternDestination = rawPattern.getString("Destination");
                        String patternDirection = rawPattern.getString("Direction");

                        if (patternNo == null || patternDestination == null || patternDirection == null)
                            throw new RouteDataMissingException();

                        RoutePattern routePattern = new RoutePattern(patternNo, patternDestination, patternDirection, route);
                        route.addPattern(routePattern);
                    } catch (Exception e){
                        success = false;
                    }
                }
            } catch (Exception e){
                success = false;
            }
        }
        if (!success)
            throw new RouteDataMissingException();
    }
}

