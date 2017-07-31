package uk.co.dyadica.unitymsband;

import com.microsoft.band.BandClient;
import com.microsoft.band.sensors.BandContactEvent;
import com.microsoft.band.sensors.BandContactEventListener;
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

public class Contact
{
    // The band client

    private BandClient client = null;
    private MsBand band;

    // The Contact sensor values

    public String state;
    public long timeStamp;

    // Output event flags

    public boolean throwEvent = true;

    /**
     * Constructor for the Contact sensor class
     * @param band
     */
    public Contact(MsBand band)
    {
        this.band = band;
        client = band.client;
    }

    /**
     * Function to enable or disable the Contact sensor event listener
     * @param state ...boolean enabled or disabled respectively.
     */
    public void enableOrDisableListener(boolean state)
    {
        try
        {
            if(state == true)
            {
                client.getSensorManager().registerContactEventListener(mContactEventListener);
            }
            else
            {
                client.getSensorManager().unregisterContactEventListener(mContactEventListener);
            }
        }
        catch (Exception ex)
        {
            System.err.println("Failed to enable or disable Contact: " + ex.getMessage());
        }
    }

    /**
     * Method that sends the sensor data to Unity
     */
    public void sendMessage()
    {
        UnityPlayer.UnitySendMessage("MsBandManager", "ThrowContactUpdateEvent", band.bandId + "," + this.toString());
    }

    /**
     * Method to return a comma separated list of the contact values
     * @return String list of contact values
     */
    public String toString()
    {
        return String.valueOf(state) + "," + String.valueOf(timeStamp);
    }

    /**
     * The Contact sensor event listener
     */
    private BandContactEventListener mContactEventListener = new BandContactEventListener()
    {
        @Override
        public void onBandContactChanged(BandContactEvent event)
        {
            state = event.getContactState().toString();
            timeStamp = event.getTimestamp();

            if(throwEvent)
                sendMessage();
        }
    };
}
