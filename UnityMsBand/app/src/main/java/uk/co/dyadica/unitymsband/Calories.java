package uk.co.dyadica.unitymsband;

import com.microsoft.band.BandClient;
import com.microsoft.band.InvalidBandVersionException;
import com.microsoft.band.sensors.BandCaloriesEvent;
import com.microsoft.band.sensors.BandCaloriesEventListener;
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

public class Calories
{
    // The band client

    private BandClient client = null;
    private MsBand band;

    // The Calorie sensor values

    public long total;
    public long totalToday;

    public long session;
    private long start = -1;

    // Control flag for throwing the event

    public boolean throwEvent = true;

    /**
     * Calories class constructor
     * @param band
     */
    public Calories(MsBand band)
    {
        this.band = band;
        client = band.client;
    }

    /**
     * Function to enable or disable the Calorie sensor event listener
     * @param state ...boolean enabled or disabled respectively.
     */
    public void enableOrDisableListener(boolean state)
    {
        try
        {
            if(state == true)
            {
                client.getSensorManager().registerCaloriesEventListener(mCaloriesEventListener);
            }
            else
            {
                client.getSensorManager().unregisterCaloriesEventListener(mCaloriesEventListener);
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
        UnityPlayer.UnitySendMessage("MsBandManager", "ThrowCaloriesUpdateEvent", band.bandId + "," + this.toString());
    }

    /**
     * Method to return a comma separated list of the calorie values
     * @return String list of calorie values
     */
    public String toString()
    {
        return String.valueOf(total) + "," + String.valueOf(session) + "," + String.valueOf(totalToday);
    }

    /**
     * The Calories event listener
     */
    private BandCaloriesEventListener mCaloriesEventListener = new BandCaloriesEventListener()
    {
        @Override
        public void onBandCaloriesChanged(BandCaloriesEvent event)
        {
           total = event.getCalories();

            try
            {
                totalToday = event.getCaloriesToday();
            }
            catch (InvalidBandVersionException ex)
            {
                System.err.println("BandVersionException: " + ex.getMessage());
            }

            if(start == -1)
                start = total;

            session = total - start;

            if(throwEvent)
                sendMessage();

        }
    };
}