package uk.co.dyadica.unitymsband;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.microsoft.band.BandClient;
import com.microsoft.band.BandClientManager;
import com.microsoft.band.BandException;
import com.microsoft.band.BandInfo;
import com.microsoft.band.ConnectionState;
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

public class MsBandManager implements IBandCommunication
{
    // List of paired bands

    public BandInfo[] pairedDevices;

    // List of respective clients

    public BandClient[] clients;

    // List of bands

    public MsBand[] msBands;

    // Refs for the MsBandAndroidBridge.cs script

    private String gameObject;

    // region Multi-Plugin Methods

    ///////////////////////////////

    // Instance of this script

    private static MsBandManager bandManager;

    public Context context;
    public Activity activity;
    public UnityPlayer unityPlayer;

    /**
     * Constructor
     */
    public MsBandManager()
    {
        bandManager = this;
    }

    /**
     * Public static access for this script
     * @return this script instance
     */
    public static MsBandManager bandManager()
    {
        if(bandManager == null) {
            bandManager = new MsBandManager();
        }

        return bandManager;
    }

    ///////////////////////////////

    /**
     * Method to set the context via unity
     * @param context
     */
    public void setContext(Context context)
    {
        this.context = context;
        this.activity = (Activity) context;
    }

    /**
     * Method to set the player via unity
     * @param player
     */
    public void setUnityPlayer(UnityPlayer player)
    {
        this.unityPlayer = player;
    }

    /**
     * Method to set the activity via unity
     * @param activity
     */
    public void setActivity(Activity activity)
    {
        this.activity = activity;
    }

    ///////////////////////////////

    // endregion Multi-Plugin Methods

    /**
     * Method that acts to initialise the plugin
     */
    public void initialisePlugin()
    {
        getPairedDevices();

        if(pairedDevices == null || pairedDevices.length == 0)
        {
            System.err.println("There are no MsBands paired to this device!");
            // showToast("There are no MsBands paired to this device!");
            return;
        }
    }

    /**
     * Method used to get a list of band devices paired with
     * the mobile device.
     */
    public void getPairedDevices()
    {
        try
        {
            pairedDevices = BandClientManager.getInstance().getPairedBands();
            clients = new BandClient[pairedDevices.length];

            UnityPlayer.UnitySendMessage(
                    "MsBandManager",
                    "ThrowDeviceListUpdateEvent",
                    getStringDeviceList()
            );
        }
        catch (Exception ex)
        {
            System.err.println("Failed to get paired devices!" + ex.getMessage());
        }
    }

    // region Detail Methods

    // endregion Detail Methods

    // region Connect Bands

    /**
     * Method to allow the connection of a named band via unity and or android
     * @param name
     */
    public void connectToNamedBand(String name)
    {
        int id = getDeviceIdFromName(name);

        if(id == -1)
        {
            String error = "1,"+ "There are no bands paired with the given name!";

            System.err.println(error);

            UnityPlayer.UnitySendMessage(
                    "MsBandManager",
                    "ThrowBandErrorEvent",
                    error
            );
        }
        else
        {
            connectToPairedBand(id);
        }
    }

    /**
     * Method to get the id of a band within pairedDevices via its name.
     * This value will directly correspond to that of the clients list.
     * @param name ...String of the name of the band to find
     * @return int value of the bands position within pairedDevices.
     */
    public int getDeviceIdFromName(String name)
    {
        // Check to see that there are some bands paired
        if(pairedDevices.length == 0)
        {
            System.err.println("There are no bands paired with the device!");
            // showToast("There are no bands paired with the device!");
            return -1;
        }

        // Loop through the paired devices and try to match them
        // against the desired name.

        for (int i = 0; i < pairedDevices.length; i++)
        {
            String bandName = pairedDevices[i].getName();

            if(bandName.equals(name))
            {
                // we have found the band so return
                return i;
            }
        }

        // We have not found the band so return a null value
        System.err.println("Failed to find band: " + name);
        return -1;
    }

    /**
     * Method to get the id of a band within pairedDevices via its mac.
     * This value will directly correspond to that of the clients list.
     * @param address ...String of the band mac to find
     * @return int value of the bands position within pairedDevices.
     */
    public int getDeviceIdFromMac(String address)
    {
        // Check to see that there are some bands paired
        if(pairedDevices.length == 0)
        {
            System.err.println("There are no bands paired with the device!");
            // showToast("There are no bands paired with the device!");
            return Integer.parseInt(null);
        }

        // Loop through the paired devices and try to match them
        // against the desired name.

        for (int i = 0; i < pairedDevices.length; i++)
        {
            String bandMac = pairedDevices[i].getMacAddress();

            if(bandMac.equals(address))
            {
                // we have found the band so return
                return i;
            }
        }

        // We have not found the band so return a null value
        System.err.println("Failed to find band: " + address);
        return Integer.parseInt(null);
    }

    /**
     * Method to allow the connection of all bands paired to the device
     */
    public void connectToPairedBands()
    {
        // Check to see that there are some bands paired
        if(pairedDevices.length == 0)
        {
            System.err.println("There are no bands paired with the device!");
            // showToast("There are no bands paired with the device!");
            return;
        }

        // Try and connect to each of the paired devices
        for (int i = 0; i < pairedDevices.length; i++)
        {
            new connectBandTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, i);
        }
    }

    /**
     * Method to allow the connection of a specific band paired to the device
     * given its id within the clients list.
     * @param id ...the id of the band to connect to.
     */
    public void connectToPairedBand(int id)
    {
        // Check to see that there are some bands paired
        if(pairedDevices.length == 0)
        {
            System.err.println("There are no bands paired with the device!");
            // showToast("There are no bands paired with the device!");
            return;
        }

        // Try and connect to each of the paired devices
        new connectBandTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, id);
    }

    /**
     * Method to allow the disconnection of all bands paired to the device
     * given its id within the clients list.
     */
    public void disconnectFromAllBands()
    {
        // Check to see that there are some bands paired
        if(pairedDevices.length == 0)
        {
            System.err.println("There are no bands paired with the device!");
            // showToast("There are no bands paired with the device!");
            return;
        }

        // Try and connect to each of the paired devices
        for (int i = 0; i < pairedDevices.length; i++)
        {
            new disconnectBandTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, i);
        }
    }

    /**
     * Method to allow the disconnection of a specific band paired to the device
     * given its id within the clients list.
     * @param id ...the id of the band to connect to.
     */
    public void disconnectFromPairedBand(int id)
    {
        // Check to see that there are some bands paired
        if(pairedDevices.length == 0)
        {
            System.err.println("There are no bands paired with the device!");
            // showToast("There are no bands paired with the device!");
            return;
        }

        new disconnectBandTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,id);
    }

    /**
     * Method that allows for the connection of a specific band given its id within
     * the clients list.
     * @param id ...the id of the band to connect to.
     * @return boolean indexLevel of connection
     * @throws InterruptedException
     * @throws BandException
     */
    private boolean getConnectedBandClient(int id) throws InterruptedException, BandException
    {
        System.out.println("Getting client for band[" + id + "]");

        if(clients[id] == null)
        {
            clients[id] = BandClientManager.getInstance().create(this.context, pairedDevices[id]);
        }
        else if (ConnectionState.CONNECTED == clients[id].getConnectionState())
        {
            return true;
        }

        return ConnectionState.CONNECTED == clients[id].connect().await();
    }

    /**
     * Overridden IBandCommunication interface function that allows access
     * to the bandConnected event.
     * @param id
     */
    @Override
    public void bandConnected(int id)
    {
        // msBands[id].connected = true;

        UnityPlayer.UnitySendMessage(
                "MsBandManager",
                "ThrowConnectedBandEvent",
                String.valueOf(id)
        );

    }

    /**
     * Overridden IBandCommunication interface function that allows access
     * to the bandNotConnected event.
     * @param id
     */
    @Override
    public void bandNotConnected(int id)
    {
        // msBands[id].connected = false;

        UnityPlayer.UnitySendMessage(
                "MsBandManager",
                "ThrowNotConnectedBandEvent",
                String.valueOf(id)
        );
    }

    private class disconnectBandTask extends AsyncTask<Integer, Void, Integer>
    {
        @Override
        protected Integer doInBackground(Integer... params)
        {
            int id = params[0];

            // Double-check that we have a clients list to work with

            if(clients == null)
                return id;

            if(msBands == null)
               return id;

            try
            {
                if (getConnectedBandClient(id))
                {
                    // Yup we are connected
                    System.out.println("Band[" + id + "] is connected!");

                    // disable all sensors
                    msBands[id].disableAllSensors();

                    // disconnect from the band
                    clients[id].disconnect();

                    // wipe the band
                    msBands[id] = null;

                    // throw a not connected event
                    bandNotConnected(id);
                }
                else
                {
                    bandNotConnected(id);
                    System.err.println("Band[" + id + "] is not connected. Please make sure bluetooth is on and the band is in range!");
                    return id;
                }
            }
            catch (BandException ex)
            {
                System.err.println("BE " + ex.getMessage());
                // showToast(ex.getMessage());
                return id;
            }
            catch (Exception ex)
            {
                System.err.println("E " + ex.getMessage());
                // showToast(ex.getMessage());
                return id;
            }

            return id;
        }
    }

    /**
     * AsyncTask that facilitates the connection of MSBands
     */
    private class connectBandTask extends AsyncTask<Integer, Void, Integer>
    {
        @Override
        protected Integer doInBackground(Integer... params)
        {
            int id = params[0];

            // Double-check that we have a clients list to populate

            if(clients == null)
                clients = new BandClient[pairedDevices.length];

            if(msBands == null)
                msBands = new MsBand[pairedDevices.length];

            try
            {
                if (getConnectedBandClient(id))
                {
                    // Yup we are connected
                    System.out.println("Band[" + id + "] is connected!");

                    // Initialise the band
                    msBands[id] = new MsBand(clients[id], bandManager);

                    // Set the bands id
                    msBands[id].bandId = id;

                    // Enable all sensors
                    // msBands[id].enableAllSensors();
                }
                else
                {
                    bandNotConnected(id);
                    System.err.println("Band[" + id + "] is not connected. Please make sure bluetooth is on and the band is in range!");
                    return -1;
                }
            }
            catch (BandException ex)
            {
                System.err.println("BE " + ex.getMessage());
                // showToast(ex.getMessage());
                return -1;
            }
            catch (Exception ex)
            {
                System.err.println("E " + ex.getMessage());
                // showToast(ex.getMessage());
                return -1;
            }

            return id;
        }

        /**
         * Method called on completion of the connection task
         * @param result the id of the connected band
         */
        @Override
        protected void onPostExecute(Integer result)
        {
            // If we have a -1 value then the connection has failed

            if(result  == -1)
                return;

            // Apply the result via the following method call

            bandConnected(result);
        }
    }

    // endregion Connect Bands

    /**
     * Method to enable all the sensors on a given band
     * @param id the band to apply sensor enable to
     */
    public void enableAllSensors(int id)
    {
        try
        {
            msBands[id].enableAllSensors();
        }
        catch (Exception ex)
        {
            System.err.println("Failed to enable sensors: Band[" + id + "]!");
        }
    }

    /**
     * Method to disable all the sensors on a given band
     * @param id the band to apply sensor enable to
     */
    public void disableAllSensors(int id)
    {
        try
        {
            msBands[id].disableAllSensors();
        }
        catch (Exception ex)
        {
            System.err.println("Failed to disable sensors: Band[" + id + "]!");
        }
    }

    /**
     *  Method to enable defined sensors on a given band
     * @param id the band to apply sensor enable to
     * @param sensors the sensors to enable
     */
    public void enableNamedSensors(int id, String[] sensors)
    {
        try
        {
            msBands[id].enableDisableNamedSensors(sensors, true);
        }
        catch (Exception ex)
        {
            System.err.println("Failed to enable sensors: Band[" + id + "]!");
        }
    }

    /**
     *  Method to disable defined sensors on a given band
     * @param id the band to apply sensor disable to
     * @param sensors the sensors to disable
     */
    public void disableNamedSensors(int id, String[] sensors)
    {
        try
        {
            msBands[id].enableDisableNamedSensors(sensors, false);
        }
        catch (Exception ex)
        {
            System.err.println("Failed to enable sensors: Band[" + id + "]!");
        }
    }

    // region Unity Function Calls

    /**
     * Method used to get a string list of devices
     * @return
     */
    public String getStringDeviceList()
    {
        String deviceList = "";

        for (BandInfo info : pairedDevices)
        {
            deviceList += info.getName();
            deviceList += ",";
        }

        return deviceList;
    }

    /**
     * Method used to get the details of a specific band identified via its id
     * @param id the id of the band to poll
     * @return comma separated string detailing both the hardware and firmware
     * versions.
     */
    public String getBandDetails(int id)
    {
        if(msBands != null && msBands[id] != null)
        {
            return msBands[id].hwVersion + "," + msBands[id].fwVersion;
        }
        else
        {
            return null;
        }
    }

    /**
     * Method for creating a tile on the band
     * !Not implemented for this release!
     * @param id id of the band on which the tile is to be created
     */
    public void createUnityTile(int id)
    {
        if(msBands != null && msBands[id] != null)
        {
            msBands[id].createTile();
        }
    }

    // region Haptic Triggers

    /**
     * Method that allows for the triggering of haptic events via unity
     * @param id id of the band to trigger
     * @param vibration the name of the vibration to be triggered
     */
    public void triggerHapticEvent(int id, String vibration)
    {
        if(msBands != null && msBands[id] != null)
        {
            msBands[id].triggerHapticEvent(vibration);
        }
    }

    /**
     * Method that allows for the triggering of haptic events via unity
     * @param id the band to trigger
     * @param value the value to be triggered
     */
    public void triggerHapticValue(int id, String value)
    {
        if(msBands != null && msBands[id] != null)
        {
            msBands[id].triggerHapticValue(value);
        }
    }

    // endregion Haptic Triggers

    // endregion Unity Function Calls

    /**
     * Method to alow for toasts just in case :)
     * @param message
     */
    public void showToast(final String message)
    {
        Toast.makeText(this.context, message, Toast.LENGTH_SHORT).show();
    }
}
