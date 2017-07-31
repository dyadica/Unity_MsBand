package uk.co.dyadica.unitymsband;

import com.microsoft.band.BandClient;
import com.microsoft.band.sensors.BandRRIntervalEvent;
import com.microsoft.band.sensors.BandRRIntervalEventListener;
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

public class RRInterval
{
    // The band client

    private BandClient client = null;
    private MsBand band;

    // The RRInterval sensor values

    public double interval = -1;

    // Control flag for throwing the event

    public boolean throwEvent = true;

    /**
     * Constructor for RRIndex
     * @param band
     */
    public RRInterval(MsBand band)
    {
        this.band = band;
        this.client = band.client;
    }

    /**
     * Method to return a comma separated list of the RRInterval values
     * @return String list of RRInterval values
     */
    public String toString()
    {
        return String.valueOf(interval);
    }

    /**
     * Method that sends the sensor data to Unity
     */
    public void sendMessage()
    {
        UnityPlayer.UnitySendMessage("MsBandManager", "ThrowRRIntervalRateUpdateEvent", band.bandId + "," + this.toString());
    }

    /**
     * Function to enable or disable the RRInterval event listener
     * @param state ...boolean enabled or disabled respectively.
     */
    public void enableOrDisableListener(boolean state)
    {
        try
        {
            if(state == true)
            {
                client.getSensorManager().registerRRIntervalEventListener(mRRIntervalEventListener);
            }
            else
            {
                client.getSensorManager().unregisterRRIntervalEventListener(mRRIntervalEventListener);
            }
        }
        catch (Exception ex)
        {
            System.err.println("Failed to enable or disable HeartRate: " + ex.getMessage());
        }
    }

    /**
     * The RRInterval sensor event listener
     */
    private BandRRIntervalEventListener mRRIntervalEventListener = new BandRRIntervalEventListener()
    {
        @Override
        public void onBandRRIntervalChanged(BandRRIntervalEvent event)
        {
            interval = event.getInterval();

            if(throwEvent)
                sendMessage();
        }
    };
}
