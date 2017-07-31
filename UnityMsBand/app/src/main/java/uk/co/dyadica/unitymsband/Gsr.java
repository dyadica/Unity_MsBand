package uk.co.dyadica.unitymsband;

import com.microsoft.band.BandClient;
import com.microsoft.band.sensors.BandGsrEvent;
import com.microsoft.band.sensors.BandGsrEventListener;
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

public class Gsr
{
    // The band client

    private BandClient client = null;
    private MsBand band;

    public int resistance;

    // Control flag for throwing the event

    public boolean throwEvent = true;

    /**
    * Default constructor for the Gsr class
    */
    public Gsr(MsBand band)
    {
        this.band = band;
        client = band.client;
    }

    /**
    * Function to enable or disable the Gsr event listener
    * @param state ...boolean enabled or disabled respectively.
    */
    public void enableOrDisableListener(boolean state)
    {
        try
        {
            if(state == true)
            {
                client.getSensorManager().registerGsrEventListener(mGsrEventListener);
            }
            else
            {
                client.getSensorManager().unregisterGsrEventListener(mGsrEventListener);
            }
        }
        catch (Exception ex)
        {
            System.err.println("Failed to enable or disable Gsr: " + ex.getMessage());
        }
    }

    /**
    * Method to return a comma separated list of all the Gsr values
    * @return String list of Gsr values
    */
    public String toString()
    {
        return String.valueOf(resistance);
    }

    /**
    * Method that sends the sensor data to Unity
    */
    public void sendMessage()
    {
        UnityPlayer.UnitySendMessage("MsBandManager", "ThrowGsrUpdateEvent", band.bandId + "," + this.toString());
    }

    /**
    * The Gsr event listener
    */
    private BandGsrEventListener mGsrEventListener = new BandGsrEventListener()
    {
        @Override
        public void onBandGsrChanged(final BandGsrEvent event)
        {
            if (event != null)
            {
                resistance = event.getResistance();

                if(throwEvent)
                    sendMessage();
            }
        }
    };
}
