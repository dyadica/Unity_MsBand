package uk.co.dyadica.unitymsband;

import com.microsoft.band.BandClient;
import com.microsoft.band.InvalidBandVersionException;
import com.microsoft.band.sensors.BandPedometerEvent;
import com.microsoft.band.sensors.BandPedometerEventListener;
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

public class Pedometer
{
    // The band client

    private BandClient client = null;
    private MsBand band;

    // The Pedometer sensor values

    public long stepCount;
    public long sessionCount;
    public long stepCountToday;

    private long startCount = -1;

    // Control flag for throwing the event

    public boolean throwEvent = true;

    /**
     * Pedometer sensor class constructor
     * @param band
     */
    public Pedometer(MsBand band)
    {
        this.band = band;
        client = band.client;
    }

    /**
     * Function to enable or disable the Pedometer event listener
     * @param state ...boolean enabled or disabled respectively.
     */
    public void enableOrDisableListener(boolean state)
    {
        try
        {
            if(state == true)
            {
                client.getSensorManager().registerPedometerEventListener(mPedometerEventListener);
            }
            else
            {
                client.getSensorManager().unregisterPedometerEventListener(mPedometerEventListener);
            }
        }
        catch (Exception ex)
        {
            System.err.println("Failed to enable or disable Pedometer: " + ex.getMessage());
        }
    }

    /**
     * Method that sends the sensor data to Unity
     */
    public void sendMessage()
    {
        UnityPlayer.UnitySendMessage("MsBandManager", "ThrowPedometerUpdateEvent", band.bandId + "," + this.toString());
    }

    /**
     * Method to return a comma separated list of the Pedometer values
     * @return String list of Pedometer values
     */
    public String toString()
    {
        return String.valueOf(stepCount) + "," + String.valueOf(sessionCount) + "," + String.valueOf(stepCountToday);
    }

    /**
     * The Pedometer sensor event listener
     */
    private BandPedometerEventListener mPedometerEventListener = new BandPedometerEventListener()
    {
        @Override
        public void onBandPedometerChanged(BandPedometerEvent event)
        {
            stepCount = event.getTotalSteps();

            try
            {
               stepCountToday = event.getStepsToday();
            }
            catch (InvalidBandVersionException ex)
            {
                System.err.println("BandVersionException: " + ex.getMessage());
            }


            if(startCount == -1)
                startCount = stepCount;

            sessionCount = stepCount - startCount;

            if(throwEvent)
                sendMessage();
        }
    };
}
