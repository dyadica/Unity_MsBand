package uk.co.dyadica.unitymsband;

import com.microsoft.band.BandClient;
import com.microsoft.band.sensors.BandBarometerEvent;
import com.microsoft.band.sensors.BandBarometerEventListener;
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

public class Barometer
{
    // The band client

    private BandClient client = null;
    private MsBand band;

    // The barometer pressure and temperature values

    public double airPressure;
    public double temperature;

    // Control flag for throwing the event

    public boolean throwEvent = true;

    /**
    * Default constructor for the Barometer class
    */
    public Barometer(MsBand band)
    {
        this.band = band;
        client = band.client;
    }

    /**
    * Function to enable or disable the Barometer event listener
    * @param state ...boolean enabled or disabled respectively.
    */
    public void enableOrDisableListener(boolean state)
    {
        try
        {
            if(state == true)
            {
                client.getSensorManager().registerBarometerEventListener(mBarometerEventListener);
            }
            else
            {
                client.getSensorManager().unregisterBarometerEventListener(mBarometerEventListener);
            }
        }
        catch (Exception ex)
        {
            System.err.println("Failed to enable or disable Altimeter: " + ex.getMessage());
        }
    }

    /**
    * Method to return a comma separated list of all the Barometer values
    * @return String list of barometer values
    */
    public String toString()
    {
        return String.valueOf(airPressure) + "," + String.valueOf(temperature);
    }

    /**
    * Method that sends the sensor data to Unity
    */
    public void sendMessage()
    {
        UnityPlayer.UnitySendMessage("MsBandManager", "ThrowBarometerUpdateEvent", band.bandId + "," + this.toString());
    }

    /**
    * The Barometer event listener
    */
    private BandBarometerEventListener mBarometerEventListener = new BandBarometerEventListener()
    {
        @Override
        public void onBandBarometerChanged(final BandBarometerEvent event)
        {
            if (event != null)
            {
                temperature = event.getTemperature();
                airPressure = event.getAirPressure();

                if(throwEvent)
                    sendMessage();
            }
        }
    };
}
