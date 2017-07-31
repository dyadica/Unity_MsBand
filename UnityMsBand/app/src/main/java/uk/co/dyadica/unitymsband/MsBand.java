package uk.co.dyadica.unitymsband;

import com.microsoft.band.BandClient;
import com.microsoft.band.BandException;
import com.microsoft.band.UserConsent;
import com.microsoft.band.notifications.VibrationType;
import com.microsoft.band.sensors.HeartRateConsentListener;
import com.unity3d.player.UnityPlayer;

import java.lang.reflect.Array;
import java.util.Arrays;

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

public class MsBand
{
    // Ref the manager
    public MsBandManager bandManager;

    // Ref this bands client
    public BandClient client;

    // Set this bands id
    public int bandId;

    // Band Details

    // The firmware version
    public String fwVersion;

    // The hardware version
    public String hwVersion;

    // Default Band Sensors

    public Accelerometer accelerometer;
    public Gyroscope gyroscope;
    public HeartRate heartRate;
    public Calories calories;
    public Distance distance;
    public Contact contact;
    public Pedometer pedometer;
    public UVIndex uvIndex;
    public SkinTemperature skinTemperature;

    // Band 2 Sensors

    public Altimeter altimeter;
    public Gsr gsr;
    public Barometer barometer;
    public AmbientLight ambientLight;
    public RRInterval rrInterval;

    // An example tile to be created on a band device

    // public ExampleTile exampleTile;

    // Flag which will be used to identify the connection
    // state of the device. TBI!

    public boolean connected = false;

    /**
     *
     * @param client
     * @param bandManager
     */
    public MsBand(BandClient client, MsBandManager bandManager)
    {
        this.client = client;
        this.bandManager = bandManager;

        try
        {
            fwVersion = client.getFirmwareVersion().await();
            hwVersion = client.getHardwareVersion().await();
        }
        catch (Exception ex)
        {
            String error = "3" + "Failed to get Band[" + bandId + "] version Information!";

            System.err.println(error);

            UnityPlayer.UnitySendMessage(
                    "MsBandManager",
                    "ThrowBandErrorEvent",
                    error
            );
        }
    }

    // region Tiles

    public void createTile()
    {
        // Sorry this has been removed for this release!

        // exampleTile = new ExampleTile(this);
        // exampleTile.createNewTile();
    }

    // endregion Tiles

    // region Enable and Disable Sensors

    /**
     * Method to enable specific sensors via defined string[]
     * @param sensors string[] array
     */
    public void enableNamedSensors(String[] sensors)
    {
        enableDisableNamedSensors(sensors, true);
    }

    /**
     * Method to disable specific sensors via defined string[]
     * @param sensors
     */
    public void disableNamedSensors(String[] sensors)
    {
        enableDisableNamedSensors(sensors, false);
    }

    /**
     * Method to both enable and or disable named sensors defined via a string[]
     * and a given boolean state
     * @param sensors string[]
     * @param state true enable or false disable
     */
    public void enableDisableNamedSensors(String[] sensors, boolean state)
    {
        for(String sensor: sensors)
        {
            switch (sensor)
            {
                case "Accelerometer":
                    accelerometer = new Accelerometer(this);
                    accelerometer.enableOrDisableListener(state);
                    break;
                
                case "Gyroscope":
                    gyroscope = new Gyroscope(this);
                    gyroscope.enableOrDisableListener(state);
                    break;
                
                case "Calories":
                    calories = new Calories(this);
                    calories.enableOrDisableListener(state);
                    break;
                
                case "Distance":
                    distance = new Distance(this);
                    distance.enableOrDisableListener(state);
                    break;    
                
                case "HeartRate":
                    checkHeartRateSensor();
                    break;

                case "Contact":
                    contact = new Contact(this);
                    contact.enableOrDisableListener(state);
                    break;

                case "Pedometer":
                    pedometer = new Pedometer(this);
                    pedometer.enableOrDisableListener(true);
                    break;

                case "UVIndex":
                    uvIndex = new UVIndex(this);
                    uvIndex.enableOrDisableListener(true);
                    break;

                case "SkinTemperature":
                    skinTemperature = new SkinTemperature(this);
                    skinTemperature.enableOrDisableListener(true);
                    break;

            }

            if (Integer.valueOf(hwVersion) >= 20)
            {
                switch (sensor)
                {
                    case "Gsr":
                        gsr = new Gsr(this);
                        gsr.enableOrDisableListener(state);
                        break;
                    
                    case "AmbientLight":
                        ambientLight = new AmbientLight(this);
                        ambientLight.enableOrDisableListener(state);
                        break;
                    
                    case "Barometer":
                        barometer = new Barometer(this);
                        barometer.enableOrDisableListener(state);
                        break;
                    
                    case "Altimeter":
                        altimeter = new Altimeter(this);
                        altimeter.enableOrDisableListener(state);
                        break;

                    /*
                    case "RRInterval":
                        rrInterval = new RRInterval(this);
                        rrInterval.enableOrDisableListener(state);
                        */
                }
            }
            else
            {
                System.err.println("Some sensors are not supported with your Band version. Microsoft Band 2 is required!");
            }
        }
    }

    /**
     * Method to enable all sensors that are supported by the band
     */
    public void enableAllSensors()
    {
       enableAllBand1Sensors();

        System.out.println("Band[" + bandId + "] Version: " + hwVersion);

        if (Integer.valueOf(hwVersion) >= 20)
        {
            enableAllBand2Sensors();
        }
        else
        {
            System.err.println("Some sensors are not supported with your Band version. Microsoft Band 2 is required!");

            // Gsr
            // RRInterval
            // AmbientLight
            // Barometer
            // Altimeter
        }

        // The HeartRate sensor is a special case as it needs extra permissions

        checkHeartRateSensor();
    }
    
    public void  disableAllSensors()
    {
        disableAllBand1Sensors();

        if (Integer.valueOf(hwVersion) >= 20)
        {
            disableAllBand2Sensors();
        }
        else
        {
            System.err.println("Some sensors are not supported with your Band version. Microsoft Band 2 is required!");

            // Gsr
            // RRInterval
            // AmbientLight
            // Barometer
            // Altimeter
        }
    }

    /**
     * Method that checks user consent status for use of the HeartRate sensor
     */
    public void checkHeartRateSensor()
    {
        if (client.getSensorManager().getCurrentHeartRateConsent() == UserConsent.GRANTED)
        {
            // Status has already been granted to enable the sensor

            enableHeartSensors();
        }
        else
        {
            // Show the consent request

            client.getSensorManager().requestHeartRateConsent(bandManager.activity, new HeartRateConsentListener()
            {
                @Override
                public void userAccepted(boolean consentGiven)
                {
                    if(consentGiven)
                    {
                        // Consent has been granted so enable the sensor

                        enableHeartSensors();
                    }
                    else
                    {
                        // Consent declined so show error and return

                        String error = "2,"+ "You have not given this application consent to access heart rate data!";

                        System.err.println(error);

                        UnityPlayer.UnitySendMessage(
                                "MsBandManager",
                                "ThrowBandErrorEvent",
                                error
                        );

                        // HeartRate is disabled for this session!
                    }
                }
            });
        }
    }

    /**
     * Method that enables the HeartRate sensor
     */
    public void enableHeartSensors()
    {
        try
        {
            System.out.println("Enabling Heart Sensors!");

            // Initialise the class

            heartRate = new HeartRate(this);
            heartRate.enableOrDisableListener(true);

            // If this we are connected to a band 2 then also enable the
            // RRInterval sensor.

            if (Integer.valueOf(hwVersion) >= 20)
            {
                rrInterval = new RRInterval(this);
                rrInterval.enableOrDisableListener(true);
            }
        }
        catch (Exception ex)
        {
            System.err.println("HeartRate: " + ex.getMessage());
        }
    }

    /**
     * Method to disable the HeartRate sensor
     */
    public void disableHeartSensors()
    {
        try
        {
            // Disable the HeartRate sensor

            heartRate.enableOrDisableListener(false);

            // If this we are connected to a band 2 then also disable the
            // RRInterval sensor.

            if (Integer.valueOf(hwVersion) >= 20)
            {
                rrInterval.enableOrDisableListener(false);
            }
        }
        catch (Exception ex)
        {
            System.err.println("HeartRate: " + ex.getMessage());
        }
    }

    /**
     * Method that enables all the sensors of the Band 1 with the exception
     * of the HeartRate sensor.
     */
    public void enableAllBand1Sensors()
    {
        System.out.println("Enabling Band 1 Sensors for Band[" + bandId + "]");

        accelerometer = new Accelerometer(this);
        accelerometer.enableOrDisableListener(true);

        gyroscope = new Gyroscope(this);
        gyroscope.enableOrDisableListener(true);

        calories = new Calories(this);
        calories.enableOrDisableListener(true);

        distance = new Distance(this);
        distance.enableOrDisableListener(true);

        contact = new Contact(this);
        contact.enableOrDisableListener(true);

        pedometer = new Pedometer(this);
        pedometer.enableOrDisableListener(true);

        uvIndex = new UVIndex(this);
        uvIndex.enableOrDisableListener(true);

        skinTemperature = new SkinTemperature(this);
        skinTemperature.enableOrDisableListener(true);
    }

    /**
     * Method that disables all the sensors of the Band 1 with the exception
     * of the HeartRate sensor.
     */
    public void disableAllBand1Sensors()
    {
        try 
        {
            accelerometer.enableOrDisableListener(false);
            gyroscope.enableOrDisableListener(false);
            calories.enableOrDisableListener(false);
            distance.enableOrDisableListener(false);
            contact.enableOrDisableListener(false);
            pedometer.enableOrDisableListener(false);
            uvIndex.enableOrDisableListener(false);
            skinTemperature.enableOrDisableListener(false);
        }
        catch (Exception ex)
        {
            System.err.println("Failed to disable band 1 Sensors for Band[" + bandId + "]");
        }
    }

    /**
     * Method that enables all the sensors of the Band 2 with the exception
     * of the RRInterval sensor as this is catered for via the HeartRate
     * sensor check.
     */
    public void enableAllBand2Sensors()
    {
        System.out.println("Enabling Band 2 Sensors for Band[" + bandId + "]");

        altimeter = new Altimeter(this);
        altimeter.enableOrDisableListener(true);

        gsr = new Gsr(this);
        gsr.enableOrDisableListener(true);

        barometer = new Barometer(this);
        barometer.enableOrDisableListener(true);

        ambientLight = new AmbientLight(this);
        ambientLight.enableOrDisableListener(true);

        // rrInterval = new RRInterval(this);
        // rrInterval.enableOrDisableListener(true);
    }
    
    public void  disableAllBand2Sensors()
    {
        try
        {
            altimeter.enableOrDisableListener(false);
            gsr.enableOrDisableListener(false);
            barometer.enableOrDisableListener(false);
            ambientLight.enableOrDisableListener(false);
        }
        catch (Exception ex)
        {
            System.err.println("Failed to disable band 2 Sensors for Band[" + bandId + "]");
        }
    }

    // endregion Enable and Disable Sensors

    // region Haptic Triggers

    /**
     * Method used to trigger haptic events
     * @param vibration string name of the event to trigger
     */
    public void triggerHapticEvent(String vibration)
    {
        try {
            switch (vibration) {
                case "NOTIFICATION_ALARM":
                    client.getNotificationManager().vibrate(VibrationType.NOTIFICATION_ALARM).await();
                    break;
                case "NOTIFICATION_ONE_TONE":
                    client.getNotificationManager().vibrate(VibrationType.NOTIFICATION_ONE_TONE).await();
                    break;
                case "NOTIFICATION_TIMER":
                    client.getNotificationManager().vibrate(VibrationType.NOTIFICATION_TIMER).await();
                    break;
                case "NOTIFICATION_TWO_TONE":
                    client.getNotificationManager().vibrate(VibrationType.NOTIFICATION_TWO_TONE).await();
                    break;
                case "ONE_TONE_HIGH":
                    client.getNotificationManager().vibrate(VibrationType.ONE_TONE_HIGH).await();
                    break;
                case "RAMP_DOWN":
                    client.getNotificationManager().vibrate(VibrationType.RAMP_DOWN).await();
                    break;
                case "RAMP_UP":
                    client.getNotificationManager().vibrate(VibrationType.RAMP_UP).await();
                    break;
                case "THREE_TONE_HIGH":
                    client.getNotificationManager().vibrate(VibrationType.THREE_TONE_HIGH).await();
                    break;
                case "TWO_TONE_HIGH":
                    client.getNotificationManager().vibrate(VibrationType.TWO_TONE_HIGH).await();
                    break;
            }
        }
        catch (InterruptedException ex)
        {
            System.err.println(ex.getMessage());
        }
        catch (BandException ex)
        {
            System.err.println(ex.getMessage());
        }
    }

    /**
     * Method used to trigger haptic events
     * @param value string value of the event to trigger
     */
    public void triggerHapticValue(String value)
    {
        try
        {
            client.getNotificationManager().vibrate(VibrationType.valueOf(value)).await();
        }
        catch (InterruptedException ex)
        {
            System.err.println(ex.getMessage());
        }
        catch (BandException ex)
        {
            System.err.println(ex.getMessage());
        }
    }

    // endregion Haptic Triggers
}
