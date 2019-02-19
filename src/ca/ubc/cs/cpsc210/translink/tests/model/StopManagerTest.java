package ca.ubc.cs.cpsc210.translink.tests.model;

import ca.ubc.cs.cpsc210.translink.model.Stop;
import ca.ubc.cs.cpsc210.translink.model.StopManager;
import ca.ubc.cs.cpsc210.translink.model.exception.StopException;
import ca.ubc.cs.cpsc210.translink.util.LatLon;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Test the StopManager
 */
public class StopManagerTest {

    @Before
    public void setup() {
        StopManager.getInstance().clearStops();
    }

    @Test
    public void testBoring() {
        Stop s9999 = new Stop(9999, "My house", new LatLon(49.2, -123.2));
        Stop r = StopManager.getInstance().getStopWithNumber(9999);
        assertEquals(s9999, r);

        try {
            StopManager.getInstance().setSelected(r);
        } catch(StopException e){
            fail("Unexpected exception.");
        }

        assertEquals(r, StopManager.getInstance().getSelected());

        Stop r2 = StopManager.getInstance().getStopWithNumber(9999);
        assertEquals(r, r2);

        Stop r3 = StopManager.getInstance().getStopWithNumber(9999, "My house", new LatLon(49.2, -123.2));
        Stop r4 = StopManager.getInstance().getStopWithNumber(6666, "Neighbour", new LatLon(49.1, -123.2));

        LatLon l1 = new LatLon(49.2, -123.2);
        Stop r5 = StopManager.getInstance().findNearestTo(l1);
        assertEquals(r3, r5);
    }

    @Test (expected = StopException.class)
    public void testNotContains() throws StopException {
        Stop s9999 = new Stop(9999, "My house", new LatLon(49.2, -123.2));

        StopManager.getInstance().setSelected(s9999);

    }
}
