package uk.co.dyadica.unitymsband;

import com.microsoft.band.BandClient;
import com.microsoft.band.sensors.BandSkinTemperatureEvent;
import com.microsoft.band.sensors.BandSkinTemperatureEventListener;
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

public class SkinTemperature 
{
    // The band client

    private BandClient client = null;
    private MsBand band;

    // The SkinTemperature sensor values

    public float temperature;

    // Control flag for throwing the event

    public boolean throwEvent = true;

    /**
     * Constructor for the SkinTemperature sensor class
     * @param band
     */
    public SkinTemperature(MsBand band)
    {
        this.band = band;
        client = band.client;
    }

    /**
     * Function to enable or disable the SkinTemperature event listener
     * @param state ...boolean enabled or disabled respectively.
     */
    public void enableOrDisableListener(boolean state)
    {
        try
        {
            if(state == true)
            {
                client.getSensorManager().registerSkinTemperatureEventListener(mSkinTemperatureEventListener);
            }
            else
            {
                client.getSensorManager().unregisterSkinTemperatureEventListener(mSkinTemperatureEventListener);
            }
        }
        catch (Exception ex)
        {
            System.err.println("Failed to enable or disable SkinTemperature: " + ex.getMessage());
        }
    }

    /**
     * Method that sends the sensor data to Unity
     */
    public void sendMessage()
    {
        UnityPlayer.UnitySendMessage("MsBandManager", "ThrowSkinTemperatureUpdateEvent", band.bandId + "," + this.toString());
    }

    /**
     * Method to return a comma separated list of the SkinTemperature values
     * @return String list of SkinTemperature values
     */
    public String toString()
    {
        return String.valueOf(temperature);
    }

    /**
     * The SkinTemperature sensor event listener
     */
    private BandSkinTemperatureEventListener mSkinTemperatureEventListener = new BandSkinTemperatureEventListener()
    {
        @Override
        public void onBandSkinTemperatureChanged(BandSkinTemperatureEvent event)
        {
            temperature = event.getTemperature();

            if(throwEvent)
                sendMessage();
        }
    };
}
