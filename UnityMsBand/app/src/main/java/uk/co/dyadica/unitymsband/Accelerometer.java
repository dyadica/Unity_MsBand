package uk.co.dyadica.unitymsband;

import com.microsoft.band.BandClient;
import com.microsoft.band.sensors.BandAccelerometerEvent;
import com.microsoft.band.sensors.BandAccelerometerEventListener;
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

public class Accelerometer
{
    // region Properties

    // The band client

    private BandClient client = null;
    private MsBand band;

    // Output event flags

    public boolean throwRawEvent = true;
    public boolean throwFltEvent = true;
    public boolean throwValEvent = true;

    /**
     * Class used to store and access the raw accelerometer values
     */
    public class Raw
    {
        public float X;
        public float Y;
        public float Z;

        /**
         * Method to return a comma separated list of all the raw accelerometer values
         * @return String list of accelerometer values
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
            UnityPlayer.UnitySendMessage("MsBandManager", "ThrowRawAccUpdateEvent", band.bandId + "," + this.toString());
        }
    }

    /**
     * Holder for the bands orientation values.
     */
    public class Value
    {
        public float Pitch;
        public float Roll;

        /**
         * Method to return a comma separated list of all the orientation accelerometer values
         * @return String list of orientation accelerometer values
         */
        public String toString()
        {
            return String.valueOf(Pitch) + "," + String.valueOf(Roll);
        }

        /**
         * Method that sends the orientation sensor data to Unity
         */
        public void sendMessage()
        {
            UnityPlayer.UnitySendMessage("MsBandManager", "ThrowValueAccUpdateEvent", band.bandId + "," + this.toString());
        }
    }

    /**
     * Class used to store and access the filtered accelerometer values
     */
    public class Filtered
    {
        public float X;
        public float Y;
        public float Z;

        /**
         * Method to return a comma separated list of all the filtered accelerometer values
         * @return String list of filtered accelerometer values
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
            UnityPlayer.UnitySendMessage("MsBandManager","ThrowFilteredAccUpdateEvent", band.bandId + "," + this.toString());
        }
    }

    // Initialise the storage for accelerometer data

    public Raw raw = new Raw();
    public Value value = new Value();
    public Filtered filtered = new Filtered();

    // endregion Properties

    /**
     * Default constructor for the accelerometer class
     */
    public Accelerometer(MsBand band)
    {
        this.band = band;
        client = band.client;
    }

    /**
     * Function to enable or disable the Accelerometers event listener
     * @param state ...boolean enabled or disabled respectively.
     */
    public void enableOrDisableListener(boolean state)
    {
        try
        {
            if (state == true)
            {
                client.getSensorManager().registerAccelerometerEventListener(mAccelerometerEventListener, SampleRate.MS128);
            }
            else
            {
                client.getSensorManager().unregisterAccelerometerEventListener(mAccelerometerEventListener);
            }
        }
        catch (Exception ex)
        {
            System.err.println("Failed to enable or disable accelerometer: " + ex.getMessage());
        }
    }

    /**
     * The Accelerometers event listener
     */
    private BandAccelerometerEventListener mAccelerometerEventListener = new BandAccelerometerEventListener()
    {
        @Override
        public void onBandAccelerometerChanged(BandAccelerometerEvent event)
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

                // Update the orientation values
                float[] ori = Tools.getBasicOrientation(raw.X, raw.Y, raw.Z);
                value.Pitch = ori[0];
                value.Roll = ori[1];

                // Throw the raw unity event if enabled
                if(throwRawEvent)
                    raw.sendMessage();

                // Throw the raw unity event if enabled
                if(throwValEvent)
                    value.sendMessage();

                // Throw the filtered unity event if enabled
                if(throwFltEvent)
                    filtered.sendMessage();
            }
        }
    };
}
