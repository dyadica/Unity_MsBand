package uk.co.dyadica.unitymsband;

import com.microsoft.band.BandClient;
import com.microsoft.band.InvalidBandVersionException;
import com.microsoft.band.sensors.BandUVEvent;
import com.microsoft.band.sensors.BandUVEventListener;
import com.unity3d.player.UnityPlayer;

/**
 * Created by dyadica.co.uk on 09/02/2016.

 * This source is subject to the dyadica.co.uk Permissive License.
 * Please see the http://www.dyadica.co.uk/permissive-license file for more information.
 * All other rights reserved.

 * THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY
 * KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A
 * PARTICULAR PURPOSE.
 */

public class UVIndex 
{
    // The band client

    private BandClient client = null;
    private MsBand band;

    // The UVIndex sensor values

    public String indexLevel;
    public long indexLevelToday;
    public long timeStamp;

    // Flag to control event throw

    public boolean throwEvent = true;

    /**
     * Constructor for the UVIndex class
     * @param band
     */
    public UVIndex(MsBand band)
    {
        this.band = band;
        client = band.client;
    }

    /**
     * Function to enable or disable the UVIndex event listener
     * @param state ...boolean enabled or disabled respectively.
     */
    public void enableOrDisableListener(boolean state)
    {
        try
        {
            if(state == true)
            {
                client.getSensorManager().registerUVEventListener(mUVEventListener);
            }
            else
            {
                client.getSensorManager().unregisterUVEventListener(mUVEventListener);
            }
        }
        catch (Exception ex)
        {
            System.err.println("Failed to enable or disable UV: " + ex.getMessage());
        }
    }

    /**
     * Method that sends the sensor data to Unity
     */
    public void sendMessage()
    {
        UnityPlayer.UnitySendMessage("MsBandManager", "ThrowUVUpdateEvent", band.bandId + "," + this.toString());
    }

    /**
     * Method to return a comma separated list of the UVIndex values
     * @return String list of UVIndex values
     */
    public String toString()
    {
        return String.valueOf(indexLevel) + "," + String.valueOf(timeStamp) + "," + String.valueOf(indexLevelToday);
    }

    /**
     * The UVIndex sensor event listener
     */
    private BandUVEventListener mUVEventListener = new BandUVEventListener()
    {
        @Override
        public void onBandUVChanged(BandUVEvent event)
        {
            indexLevel = event.getUVIndexLevel().toString();
            timeStamp = event.getTimestamp();

            try
            {
                indexLevelToday = event.getUVExposureToday();
            }
            catch (InvalidBandVersionException ex)
            {
                System.err.println("BandVersionException: " + ex.getMessage());
            }

            if(throwEvent)
                sendMessage();
        }
    };
}
