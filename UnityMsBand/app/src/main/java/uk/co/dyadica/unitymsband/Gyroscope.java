package uk.co.dyadica.unitymsband;

import com.microsoft.band.BandClient;
import com.microsoft.band.sensors.BandGyroscopeEvent;
import com.microsoft.band.sensors.BandGyroscopeEventListener;
import com.microsoft.band.sensors.SampleRate;
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

public class Gyroscope
{
    // region Properties

    // The band client

    private BandClient client = null;
    private MsBand band;

    // Output event flags

    public boolean throwRawEvent = true;
    public boolean throwFltEvent = true;

    /**
     * Class used to store and access the raw gyroscope values
     */
    public class Raw
    {
        public float X;
        public float Y;
        public float Z;

        /**
         * Method to return a comma separated list of all the raw gyroscope values
         * @return String list of gyroscope values
         */
        public String toString()
        {
            return String.valueOf(X) + "," + String.valueOf(Y) + "," + String.valueOf(Z);
        }

        /**
         * Method that sends the sensor data to Unity
         */
        public void sendMessage()
        {
            UnityPlayer.UnitySendMessage("MsBandManager", "ThrowRawGyrUpdateEvent", band.bandId + "," + this.toString());
        }
    }
    
    /**
     * Class used to store and access the filtered gyroscope values
     */
    public class Filtered
    {
        public float X;
        public float Y;
        public float Z;

        /**
         * Method to return a comma separated list of all the filtered gyroscope values
         * @return String list of filtered gyroscope values
         */
        public String toString()
        {
            return String.valueOf(X) + "," + String.valueOf(Y) + "," + String.valueOf(Z);
        }

        /**
         * Method that sends the sensor data to Unity
         */
        public void sendMessage()
        {
            UnityPlayer.UnitySendMessage("MsBandManager","ThrowFilteredGyrUpdateEvent", band.bandId + "," + this.toString());
        }
    }

    public Raw raw = new Raw();
    public Filtered filtered = new Filtered();

    /**
     * Constructor for the Gyroscope class
     * @param band
     */
    public Gyroscope(MsBand band)
    {
        this.band = band;
        client = band.client;
    }

    /**
     * Function to enable or disable the Gyroscopes event listener
     * @param state ...boolean enabled or disabled respectively.
     */
    public void enableOrDisableListener(boolean state)
    {
        try
        {
            if (state == true)
            {
                client.getSensorManager().registerGyroscopeEventListener(mGyroscopeEventListener, SampleRate.MS128);
            }
            else
            {
                client.getSensorManager().unregisterGyroscopeEventListener(mGyroscopeEventListener);
            }
        }
        catch (Exception ex)
        {
            System.err.println("Failed to enable or disable gyroscope: " + ex.getMessage());
        }
    }

    /**
     * The Gyroscopes event listener
     */
    private BandGyroscopeEventListener mGyroscopeEventListener = new BandGyroscopeEventListener()
    {
        @Override
        public void onBandGyroscopeChanged(BandGyroscopeEvent event)
        {
            if (event != null)
            {
                // Get the raw values
                raw.X = event.getAccelerationX();
                raw.Y = event.getAccelerationY();
                raw.Z = event.getAccelerationZ();

                // Apply the lowpass filter
                float[] currentAcc = new float[]{ raw.X, raw.Y, raw.Z };
                float[] lowpassAcc = Tools.lowPass(currentAcc, new float[3]);

                // Update the filtered values
                filtered.X = lowpassAcc[0];
                filtered.Y = lowpassAcc[1];
                filtered.Z = lowpassAcc[2];

                // Throw the raw unity event if enabled
                if(throwRawEvent)
                    raw.sendMessage();

                // Throw the filtered unity event if enabled
                if(throwFltEvent)
                    filtered.sendMessage();
            }
        }
    };
}
