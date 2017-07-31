package uk.co.dyadica.unitymsband;

import com.microsoft.band.BandClient;
import com.microsoft.band.InvalidBandVersionException;
import com.microsoft.band.sensors.BandAltimeterEvent;
import com.microsoft.band.sensors.BandAltimeterEventListener;
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

public class Altimeter
{
    // The band client

    private BandClient client = null;
    private MsBand band;

    // Output event flags

    public boolean throwEvent = true;

    // Altimeter properties

    public long flightsDescended;
    public float rate;
    public long steppingGain;
    public long steppingLoss;
    public long stepsAscended;
    public long stepsDescended;
    public long totalGain;
    public long totalLoss;

    // Today events

    public long flightsAscendedToday;
    public long totalGainToday;

    /**
    * Default constructor for the Altimeter class
    */
    public Altimeter(MsBand band)
    {
        this.band = band;
        this.client = band.client;
    }

    /**
    * Method to return a comma separated list of all the Altimeters values
    * @return String list of Altimeter values
    */
    public String toString()
    {
        return String.valueOf(flightsDescended) + "," +
                String.valueOf(rate) + "," +
                String.valueOf(steppingGain) + "," +
                String.valueOf(steppingLoss) + "," +
                String.valueOf(stepsAscended) + "," +
                String.valueOf(stepsDescended) + "," +
                String.valueOf(totalGain) + "," +
                String.valueOf(totalLoss) + "," +
                String.valueOf(flightsAscendedToday) + "," +
                String.valueOf(totalGainToday);
    }

    /**
    * Method that sends the sensor data to Unity
    */
    public void sendMessage()
    {
        UnityPlayer.UnitySendMessage("MsBandManager", "ThrowAltUpdateEvent", band.bandId + "," + this.toString());
    }

    /**
    * Function to enable or disable the Altimeter event listener
    * @param state ...boolean enabled or disabled respectively.
    */
    public void enableOrDisableListener(boolean state)
    {
        try
        {
            if(state == true)
            {
                client.getSensorManager().registerAltimeterEventListener(mAltimeterEventListener);
            }
            else
            {
                client.getSensorManager().unregisterAltimeterEventListeners();
            }
        }
        catch (Exception ex)
        {
            System.err.println("Failed to enable or disable Altimeter: " + ex.getMessage());
        }
    }

    /**
    * The Altimeter event listener
    */
    private BandAltimeterEventListener mAltimeterEventListener = new BandAltimeterEventListener()
    {
        @Override
        public void onBandAltimeterChanged(final BandAltimeterEvent event)
        {
            if (event != null)
            {
                flightsDescended = event.getFlightsDescended();
                rate = event.getRate();
                steppingGain = event.getSteppingGain();
                steppingLoss = event.getSteppingLoss();
                stepsAscended = event.getStepsAscended();
                stepsDescended = event.getStepsDescended();
                totalGain = event.getTotalGain();
                totalLoss = event.getTotalLoss();

                // Today Events

                try
                {
                    flightsAscendedToday = event.getFlightsAscendedToday();
                    totalGainToday =  event.getTotalGainToday();

                }
                catch (InvalidBandVersionException ex)
                {
                    System.err.println("BandVersionException: " + ex.getMessage());
                }

                // Send Altimeter event message to Unity

                if(throwEvent)
                    sendMessage();
            }
        }
    };
}
