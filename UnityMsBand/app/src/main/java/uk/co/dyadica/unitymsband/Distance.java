package uk.co.dyadica.unitymsband;

import com.microsoft.band.BandClient;
import com.microsoft.band.InvalidBandVersionException;
import com.microsoft.band.sensors.BandDistanceEvent;
import com.microsoft.band.sensors.BandDistanceEventListener;
import com.unity3d.player.UnityPlayer;

/**
 * Created by dyadica.co.uk on 02/01/2016.

 * This source is subject to the dyadica.co.uk Permissive License.
 * Please see the http://www.dyadica.co.uk/permissive-license file for more information.
 * All other rights reserved.

 * THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY
 * KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A
 * PARTICULAR PURPOSE.
 */

public class Distance
{
    // The band client

    private BandClient client = null;
    private MsBand band;

    // The Distance sensor values

    public String currentMotion;
    public float pace;
    public float speed;
    public long totalDistance;
    public long totalDistanceToday;
    public long sessionDistance;

    private long startDistance = -1;

    // Control flag for throwing the event

    public  boolean throwEvent = true;

    /**
     * Constructor for the Distance sensor class
     * @param band
     */
    public Distance(MsBand band)
    {
        this.band = band;
        client = band.client;
    }

    /**
     * Function to enable or disable the Distance sensor event listener
     * @param state ...boolean enabled or disabled respectively.
     */
    public void enableOrDisableListener(boolean state)
    {
        try
        {
            if(state == true)
            {
                client.getSensorManager().registerDistanceEventListener(mDistanceEventListener);
            }
            else
            {
                client.getSensorManager().unregisterDistanceEventListener(mDistanceEventListener);
            }
        }
        catch (Exception ex)
        {
            System.err.println("Failed to enable or disable Calories: " + ex.getMessage());
        }
    }

    /**
     * Method that sends the sensor data to Unity
     */
    public void sendMessage()
    {
       UnityPlayer.UnitySendMessage("MsBandManager", "ThrowDistanceUpdateEvent", band.bandId + "," + this.toString());
    }

    /**
     * Method to return a comma separated list of the distance values
     * @return String list of distance values
     */
    public String toString()
    {
        return String.valueOf(currentMotion) + "," +
                String.valueOf(pace) + "," +
                String.valueOf(speed) + "," +
                String.valueOf(totalDistance) + "," +
                String.valueOf(sessionDistance) + "," +
                String.valueOf(totalDistanceToday);
    }

    /**
     * The Distance sensor event listener
     */
    public BandDistanceEventListener mDistanceEventListener = new BandDistanceEventListener()
    {
        @Override
        public void onBandDistanceChanged(BandDistanceEvent event)
        {
            if(event != null) {
                currentMotion = event.getMotionType().toString();
                pace = event.getPace();
                speed = event.getSpeed();
                totalDistance = event.getTotalDistance();

                try
                {
                    totalDistanceToday = event.getDistanceToday();
                }
                catch (InvalidBandVersionException ex)
                {
                    System.err.println("BandVersionException: " + ex.getMessage());
                }

                if (startDistance == -1)
                    startDistance = totalDistance;

                sessionDistance = totalDistance - startDistance;

                if(throwEvent)
                    sendMessage();
            }
        }
    };
}
