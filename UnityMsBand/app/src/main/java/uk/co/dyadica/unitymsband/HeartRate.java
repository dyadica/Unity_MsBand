package uk.co.dyadica.unitymsband;

import com.microsoft.band.BandClient;
import com.microsoft.band.sensors.BandHeartRateEvent;
import com.microsoft.band.sensors.BandHeartRateEventListener;
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

public class HeartRate
{
    // The band client

    private BandClient client = null;
    private MsBand band;

    // The HeartRate sensor values

    public int rate;
    public String quality;

    // Control flag for throwing the event

    public boolean throwEvent = true;

    /**
     * The Heartrate constructor
     * @param band
     */
    public HeartRate(MsBand band)
    {
        this.band = band;
        client = band.client;
    }

    /**
     * Function to enable or disable the heartrate event listener
     * @param state ...boolean enabled or disabled respectively.
     * @throws Exception ...you broke it... shucks!
     */
    public void enableOrDisableListener(boolean state)
    {
        try
        {
            if(state == true)
            {
                client.getSensorManager().registerHeartRateEventListener(mHeartRateEventListener);
            }
            else
            {
                client.getSensorManager().unregisterHeartRateEventListener(mHeartRateEventListener);
            }
        }
        catch (Exception ex)
        {
            System.err.println("Failed to enable or disable HeartRate: " + ex.getMessage());
        }
    }

    /**
     * Method to return a comma separated list of the HeartRate values
     * @return String list of HeartRate values
     */
    public String toString()
    {
        return String.valueOf(rate) + "," + String.valueOf(quality);
    }

    /**
     * Method that sends the sensor data to Unity
     */
    public void sendMessage()
    {
        UnityPlayer.UnitySendMessage("MsBandManager", "ThrowHeartRateUpdateEvent", band.bandId + "," + this.toString());
    }

    /**
     * The HeartRate sensor event listener
     */
    private BandHeartRateEventListener mHeartRateEventListener = new BandHeartRateEventListener()
    {
        @Override
        public void onBandHeartRateChanged(final BandHeartRateEvent event)
        {
            if (event != null)
            {
                rate = event.getHeartRate();
                quality = event.getQuality().toString();

                if(throwEvent)
                    sendMessage();
            }
        }
    };
}