package uk.co.dyadica.unitymsband;

import com.microsoft.band.BandClient;
import com.microsoft.band.sensors.BandAmbientLightEvent;
import com.microsoft.band.sensors.BandAmbientLightEventListener;
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

public class AmbientLight
{
    // The band client

    private BandClient client = null;
    private MsBand band;

    // Ambient light brightness value

    public int brightness;

    // Output event flags

    public boolean throwEvent = true;

    /**
    * Default constructor for the Ambient Light class
    */
    public AmbientLight(MsBand band)
    {
        this.band = band;
        client = band.client;
    }

    /**
    * Function to enable or disable the Ambient Light event listener
    * @param state ...boolean enabled or disabled respectively.
    */
    public void enableOrDisableListener(boolean state)
    {
        try
        {
            if(state == true)
            {
                client.getSensorManager().registerAmbientLightEventListener(mAmbientLightEventListener);
            }
            else
            {
                client.getSensorManager().unregisterAmbientLightEventListener(mAmbientLightEventListener);
            }
        }
        catch (Exception ex)
        {
            System.err.println("Failed to enable or disable AmbientLight sensor: " + ex.getMessage());
        }
    }

    /**
    * Method to return a comma separated list of all the Ambient Light values
    * @return String list of ambient light values
    */
    public String toString()
    {
        return String.valueOf(brightness);
    }

    /**
    * Method that sends the sensor data to Unity
    */
    public void sendMessage()
    {
        UnityPlayer.UnitySendMessage("MsBandManager", "ThrowAmbientLightUpdateEvent", band.bandId + "," + this.toString());
    }

    /**
    * The Ambient Light event listener
    */
    private BandAmbientLightEventListener mAmbientLightEventListener = new BandAmbientLightEventListener()
    {
        @Override
        public void onBandAmbientLightChanged(final BandAmbientLightEvent event) {
            if (event != null)
            {
                brightness = event.getBrightness();

                if(throwEvent)
                    sendMessage();
            }
        }
    };
}