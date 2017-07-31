// <copyright file="MsBandAndroidBridge.cs" company="dyadica.co.uk">
// Copyright (c) 2010, 2016 All Right Reserved, http://www.dyadica.co.uk

// This source is subject to the dyadica.co.uk Permissive License.
// Please see the http://www.dyadica.co.uk/permissive-license file for more information.
// All other rights reserved.

// THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY
// KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A
// PARTICULAR PURPOSE.

// </copyright>

// <author>SJB</author>
// <email>contact via facebook.com/adropinthedigitalocean</email>
// <date>07.03.2016</date>

using UnityEngine;
using UnityEngine.UI;

using System.Collections;
using System.Collections.Generic;

public class MsBandAndroidBridge : MonoBehaviour
{
    #region Properties

    public static MsBandAndroidBridge Instance;
    public static AndroidJavaClass UnityPlayer;
    public static AndroidJavaObject Activity;

    private AndroidJavaClass activityClass = null;
    private AndroidJavaObject activityContext = null;

    private AndroidJavaObject bandPlugin = null;

    public Dictionary<int, string> bandNameDictionary =
        new Dictionary<int, string>();

    public Dictionary<int, MsBand> BandDictionary =
        new Dictionary<int, MsBand>();

    public bool PreventSleep = true;

    //

    public bool AutoEnableSensors = true;

    public List<string> SensorsToEnable;

    #endregion Properties

    #region Events

    public delegate void ConnectedBandEventHandler(int id, MsBand band);
    public static event ConnectedBandEventHandler ConnectedBandEvent;

    // Accelerometer events

    public delegate void RawAccUpdateEventHandler(float x, float y, float z, MsBand band);
    public static event RawAccUpdateEventHandler RawAccUpdateEvent;

    public delegate void FltAccUpdateEventHandler(float x, float y, float z, MsBand band);
    public static event FltAccUpdateEventHandler FilteredAccUpdateEvent;

    public delegate void ValueAccUpdateEventHandler(float p, float r, MsBand band);
    public static event ValueAccUpdateEventHandler ValueAccUpdateEvent;

    // Gyroscope events

    public delegate void RawGyrUpdateEventHandler(float x, float y, float z, MsBand band);
    public static event RawGyrUpdateEventHandler RawGyrUpdateEvent;

    // Altimeter events

    public delegate void AltimeterUpdateEventHandler(Altimeter altimeter, MsBand band);
    public static event AltimeterUpdateEventHandler AltimeterUpdateEvent;

    // AmbientLight event

    public delegate void AmbientLightUpdateEventHandler(AmbientLight ambientLight, MsBand band);
    public static event AmbientLightUpdateEventHandler AmbientLightUpdateEvent;

    // Barometer events

    public delegate void BarometerUpdateEventHandler(Barometer barometer, MsBand band);
    public static event BarometerUpdateEventHandler BarometerUpdateEvent;

    // Calories events

    public delegate void CaloriesUpdateEventHandler(Calories calories, MsBand band);
    public static event CaloriesUpdateEventHandler CaloriesUpdateEvent;

    // Contact events

    public delegate void ContactUpdateEventHandler(Contact contact, MsBand band);
    public static event ContactUpdateEventHandler ContactUpdateEvent;

    // Distance events

    public delegate void DistanceUpdateEventHandler(Distance distance, MsBand band);
    public static event DistanceUpdateEventHandler DistanceUpdateEvent;

    // Gsr events

    public delegate void GsrUpdateEventHandler(Gsr gsr, MsBand band);
    public static event GsrUpdateEventHandler GsrUpdateEvent;

    // Pedometer events

    public delegate void PedometerUpdateEventHandler(Pedometer pedometer, MsBand band);
    public static event PedometerUpdateEventHandler PedometerUpdateEvent;

    // SkinTemperature events

    public delegate void SkinTemperatureUpdateEventHandler(SkinTemperature SkinTemperature, MsBand band);
    public static event SkinTemperatureUpdateEventHandler SkinTemperatureUpdateEvent;

    // UVIndex events

    public delegate void UVIndexUpdateEventHandler(UVIndex uvIndex, MsBand band);
    public static event UVIndexUpdateEventHandler UVIndexUpdateEvent;

    // RRInterval events

    public delegate void RRIntervalUpdateEventHandler(RRInterval rrInterval, MsBand band);
    public static event RRIntervalUpdateEventHandler RRIntervalUpdateEvent;

    // DeviceListUpdateEvent

    public delegate void DeviceListUpdateEventHandler(Dictionary<int, string> devices);
    public static event DeviceListUpdateEventHandler DeviceListUpdateEvent;

    // HeartRateUpdateEvent

    public delegate void HeartRateUpdateEventHandler(HeartRate heartRate, MsBand band);
    public static event HeartRateUpdateEventHandler HeartRateUpdateEvent;

    // Error Event

    public delegate void BandErrorEventHandler(int id, string message);
    public static event BandErrorEventHandler BandErrorEvent;

    #endregion Events

    public Text DebugWindow;
    public Text AccBand1;
    public Text AccBand2;

    #region Unity Loop

    // If you dont know what this is then I cannot help you!
    void Awake()
    {
        Instance = this;

        // Stop the application from going to sleep

        if(PreventSleep)
            Screen.sleepTimeout = SleepTimeout.NeverSleep;
    }

    // If you dont know what this is then I cannot help you!
    void Update()
    {
    }

    // If you dont know what this is then I cannot help you!
    void Start ()
    {
        ConnectedBandEvent += MsBandAndroidBridge_ConnectedBandEvent;        

        // Get the main activity of your application

        activityClass = new AndroidJavaClass("com.unity3d.player.UnityPlayer");

        // Get the activities context

        activityContext = activityClass.GetStatic<AndroidJavaObject>("currentActivity");

        if (DebugWindow != null)
            DebugWindow.text = "Found unity plugin!";

        // Initialise the plugin

        using (AndroidJavaClass pluginClass = new AndroidJavaClass("uk.co.dyadica.unitymsband.MsBandManager"))
        {
            if (pluginClass != null)
            {
                if (DebugWindow != null)
                    DebugWindow.text = "Found band plugin!";

                // Set the plugin ref

                bandPlugin = pluginClass.CallStatic<AndroidJavaObject>("bandManager");

                // Assign the context

                bandPlugin.Call("setContext", activityContext);

                // Perfom the initialisation

                bandPlugin.Call("initialisePlugin");
            }
        }
    }

    private void MsBandAndroidBridge_ConnectedBandEvent(int id, MsBand band)
    {
        if (AutoEnableSensors)
        {
            if(SensorsToEnable != null && SensorsToEnable.Count > 0)
            {
                EnableNamedSensors(id);
            }
            else
            {
                EnableAllSensors(id);
            }
        }            
    }

    #endregion Unity Loop

    #region Connection calls

    /// <summary>
    /// Method that attempts to connect all paired bands
    /// </summary>
    public void ConnectToPairedBands()
    {
        bandPlugin.Call("connectToPairedBands");
    }

    /// <summary>
    /// Method that attempts to connect to a band with a given id
    /// </summary>
    /// <param name="id">int</param>
    public void ConnectToPairedBand(int id)
    {
        bandPlugin.Call("connectToPairedBand", id);
    }

    /// <summary>
    /// Method that attempts to connect to a band with a given name
    /// </summary>
    /// <param name="name"></param>
    public void ConnectToNamedBand(string name)
    {
        bandPlugin.Call("connectToNamedBand", name);
    }

    /// <summary>
    /// Method that attempts to disconnect from all paired bands
    /// </summary>
    public void DisconnectFromAllBands()
    {
        bandPlugin.Call("disconnectFromAllBands");
    }

    /// <summary>
    /// Method that attempts to disconnect a band with a given id
    /// </summary>
    /// <param name="id"></param>
    public void DisconnectFromPairedBand(int id)
    {
        bandPlugin.Call("disconnectFromPairedBand", id);
    }

    #endregion Connection calls

    #region Enable & Disable Sensors

    /// <summary>
    /// 
    /// </summary>
    /// <param name="id"></param>
    public void EnableAllSensors(int id)
    {
        bandPlugin.Call("enableAllSensors", id);
    }

    /// <summary>
    /// 
    /// </summary>
    /// <param name="id"></param>
    public void EnableNamedSensors(int id)
    {
        bandPlugin.Call("enableNamedSensors", id, SensorsToEnable.ToArray());
    }

    /// <summary>
    /// 
    /// </summary>
    /// <param name="id"></param>
    public void DisableAllSensors(int id)
    {
        bandPlugin.Call("disableAllSensors", id);
    }

    /// <summary>
    /// 
    /// </summary>
    /// <param name="id"></param>
    public void DisableNamedSensors(int id)
    {
        bandPlugin.Call("disableNamedSensors", id, SensorsToEnable.ToArray());
    }

    #endregion Enable & Disable Sensors

    // Connection Events

    /// <summary>
    /// Plugin call that occurs when a band is connected
    /// </summary>
    /// <param name="data">string</param>
    void ThrowConnectedBandEvent(string data)
    {
        if (BandDictionary == null)
            BandDictionary = new Dictionary<int, MsBand>();

        BandDictionary.Add(int.Parse(data), new MsBand(int.Parse(data)));

        if (ConnectedBandEvent != null)
            ConnectedBandEvent(int.Parse(data), BandDictionary[int.Parse(data)]);

        print("Added band: " + int.Parse(data).ToString() + " to band dictionary");
    }

    /// <summary>
    /// Plugin call that occurs when a band is not connected
    /// </summary>
    /// <param name="data">string</param>
    void ThrowNotConnectedBandEvent(string data)
    {
        if (BandDictionary.ContainsKey(int.Parse(data)) != false)
            BandDictionary.Remove(int.Parse(data));
    }

    /// <summary>
    /// Plugin call that occurs when the paired device list is updated
    /// </summary>
    /// <param name="data">string</param>
    void ThrowDeviceListUpdateEvent(string data)
    {
        string[] devices = data.Split(',');

        bandNameDictionary = new Dictionary<int, string>();
        DebugWindow.text = "";

        for (int i = 0; i < devices.Length; i++)
        {
            bandNameDictionary.Add(i, devices[i]);

            if (DebugWindow != null)
                DebugWindow.text = DebugWindow.text + devices[i] + "\n";
        }

        if (DeviceListUpdateEvent != null)
            DeviceListUpdateEvent(bandNameDictionary);
    }

    // Error Event

    /// <summary>
    /// Plugin call that occurs when an error is triggered (TODO!)
    /// </summary>
    /// <param name="data"></param>
    void ThrowdBandErrorEvent(string data)
    {
        string[] error = data.Split(',');

        if (BandErrorEvent != null)
            BandErrorEvent(int.Parse(error[0]), error[1]);
    }

    // Heart Rate Sensor

    /// <summary>
    /// Method that fires the HeartRate sensor permissions intent
    /// </summary>
    public void CheckHeartSensorPermissions()
    {
        bandPlugin.Call("checkHeartRateSensor");
    }

    /// <summary>
    /// Method that enables the HeartRate sensor
    /// </summary>
    public void EnableHeartRateSensor()
    {
        bandPlugin.Call("enableHeartSensors");
    }

    // Please note that static calls are not enabled in this release. The following events
    // are all based upon the UnityPlayer.UnitySendMessage method.

    #region Band Sensor Update Methods

    #region Accelerometer

    void ThrowRawAccUpdateEvent(string data)
    {
        if (data == null)
            return;

        string[] accData = data.Split(',');

        int id = int.Parse(accData[0]);

        if (BandDictionary.ContainsKey(id))
        {
            BandDictionary[id].Accelerometer.RawX = float.Parse(accData[1]);
            BandDictionary[id].Accelerometer.RawY = float.Parse(accData[2]);
            BandDictionary[id].Accelerometer.RawZ = float.Parse(accData[3]);

            if (RawAccUpdateEvent != null)
                RawAccUpdateEvent(
                    BandDictionary[id].Accelerometer.RawX,
                    BandDictionary[id].Accelerometer.RawY,
                    BandDictionary[id].Accelerometer.RawZ,
                    BandDictionary[id]);
        }        
    }

    void ThrowFilteredAccUpdateEvent(string data)
    {
        if (data == null)
            return;

        string[] accData = data.Split(',');

        int id = int.Parse(accData[0]);

        if (BandDictionary.ContainsKey(id))
        {
            BandDictionary[id].Accelerometer.FilteredX = float.Parse(accData[1]);
            BandDictionary[id].Accelerometer.FilteredY = float.Parse(accData[2]);
            BandDictionary[id].Accelerometer.FilteredZ = float.Parse(accData[3]);

            if (FilteredAccUpdateEvent != null)
                FilteredAccUpdateEvent(
                    BandDictionary[id].Accelerometer.FilteredX,
                    BandDictionary[id].Accelerometer.FilteredY,
                    BandDictionary[id].Accelerometer.FilteredZ,
                    BandDictionary[id]);
        }
    }

    void ThrowValueAccUpdateEvent(string data)
    {
        if (data == null)
            return;

        string[] accData = data.Split(',');

        int id = int.Parse(accData[0]);

        if (BandDictionary.ContainsKey(id))
        {
            BandDictionary[id].Accelerometer.Pitch= float.Parse(accData[1]);
            BandDictionary[id].Accelerometer.Roll = float.Parse(accData[2]);

            if (ValueAccUpdateEvent != null)
                ValueAccUpdateEvent(
                    BandDictionary[id].Accelerometer.Pitch,
                    BandDictionary[id].Accelerometer.Roll,
                    BandDictionary[id]);
        }
    }

    #endregion Accelerometer

    #region Gyroscope

    void ThrowRawGyrUpdateEvent(string data)
    {
        if (data == null)
            return;

        string[] gyrData = data.Split(',');

        int id = int.Parse(gyrData[0]);

        if (BandDictionary.ContainsKey(id))
        {
            BandDictionary[id].Gyroscope.RawX = float.Parse(gyrData[1]);
            BandDictionary[id].Gyroscope.RawY = float.Parse(gyrData[2]);
            BandDictionary[id].Gyroscope.RawZ = float.Parse(gyrData[3]);

            if (RawGyrUpdateEvent != null)
                RawGyrUpdateEvent(
                    BandDictionary[id].Gyroscope.RawX,
                    BandDictionary[id].Gyroscope.RawY,
                    BandDictionary[id].Gyroscope.RawZ,
                    BandDictionary[id]);
        }
    }

    void ThrowFilteredGyrUpdateEvent(string data)
    {
        if (data == null)
            return;

        string[] gyrData = data.Split(',');

        int id = int.Parse(gyrData[0]);

        if (BandDictionary.ContainsKey(id))
        {
            BandDictionary[id].Gyroscope.ValueX = float.Parse(gyrData[1]);
            BandDictionary[id].Gyroscope.ValueY = float.Parse(gyrData[2]);
            BandDictionary[id].Gyroscope.ValueZ = float.Parse(gyrData[3]);

            if (RawGyrUpdateEvent != null)
                RawGyrUpdateEvent(
                    BandDictionary[id].Gyroscope.ValueX,
                    BandDictionary[id].Gyroscope.ValueY,
                    BandDictionary[id].Gyroscope.ValueZ,
                    BandDictionary[id]);
        }
    }

    #endregion Gyroscope

    #region Altimeter

    void ThrowAltUpdateEvent(string data)
    {
        if (data == null)
            return;

        string[] altData = data.Split(',');

        int id = int.Parse(altData[0]);

        if (BandDictionary.ContainsKey(id))
        {
            BandDictionary[id].Altimeter.BandId = id;

            BandDictionary[id].Altimeter.FlightsDescended =
                long.Parse(altData[1]);
            BandDictionary[id].Altimeter.Rate =
                float.Parse(altData[2]);
            BandDictionary[id].Altimeter.SteppingGain =
                long.Parse(altData[3]);
            BandDictionary[id].Altimeter.SteppingLoss =
                long.Parse(altData[4]);
            BandDictionary[id].Altimeter.StepsAscended =
                long.Parse(altData[5]);
            BandDictionary[id].Altimeter.StepsDescended =
                long.Parse(altData[6]);
            BandDictionary[id].Altimeter.TotalGain =
                long.Parse(altData[7]);
            BandDictionary[id].Altimeter.TotalLoss =
                long.Parse(altData[8]);
            BandDictionary[id].Altimeter.FlightsAscendedToday =
                long.Parse(altData[9]);
            BandDictionary[id].Altimeter.TotalGainToday =
                long.Parse(altData[10]);

            if (AltimeterUpdateEvent != null)
                AltimeterUpdateEvent(BandDictionary[id].Altimeter, BandDictionary[id]);
        }
    }

    #endregion Altimeter

    #region AmbientLight

    void ThrowAmbientLightUpdateEvent(string data)
    {
        if (data == null)
            return;

        string[] alData = data.Split(',');

        int id = int.Parse(alData[0]);

        if (BandDictionary.ContainsKey(id))
        {
            BandDictionary[id].AmbientLight.Brightness = int.Parse(alData[1]);

            if (AmbientLightUpdateEvent != null)
                AmbientLightUpdateEvent(BandDictionary[id].AmbientLight, BandDictionary[id]);
        }
    }

    #endregion AmbientLight

    #region Barometer

    void ThrowBarometerUpdateEvent(string data)
    {
        if (data == null)
            return;

        string[] barData = data.Split(',');

        int id = int.Parse(barData[0]);

        if (BandDictionary.ContainsKey(id))
        {
            BandDictionary[id].Barometer.AirPressure = 
                double.Parse(barData[1]);
            BandDictionary[id].Barometer.Temperature = 
                double.Parse(barData[2]);

            if (BarometerUpdateEvent != null)
                BarometerUpdateEvent(BandDictionary[id].Barometer, BandDictionary[id]);
        }
    }

    #endregion Barometer

    #region Calories

    void ThrowCaloriesUpdateEvent(string data)
    {
        if (data == null)
            return;

        string[] calData = data.Split(',');

        int id = int.Parse(calData[0]);

        if (BandDictionary.ContainsKey(id))
        {
            BandDictionary[id].Calories.TotalCalories = 
                long.Parse(calData[1]);

            BandDictionary[id].Calories.SessionCalories =
                long.Parse(calData[2]);

            BandDictionary[id].Calories.TotalToday =
               long.Parse(calData[3]);

            if (CaloriesUpdateEvent != null)
                CaloriesUpdateEvent(BandDictionary[id].Calories, BandDictionary[id]);
        }
    }

    #endregion Calories

    #region Contact

    void ThrowContactUpdateEvent(string data)
    {
        if (data == null)
            return;

        string[] conData = data.Split(',');

        int id = int.Parse(conData[0]);

        if (BandDictionary.ContainsKey(id))
        {
            BandDictionary[id].Contact.State =
                conData[1];

            BandDictionary[id].Contact.TimeStamp =
                long.Parse(conData[2]);

            if (ContactUpdateEvent != null)
                ContactUpdateEvent(BandDictionary[id].Contact, BandDictionary[id]);
        }
    }

    #endregion Contact

    #region Distance

    void ThrowDistanceUpdateEvent(string data)
    {
        if (data == null)
            return;

        string[] dstData = data.Split(',');

        int id = int.Parse(dstData[0]);

        if (BandDictionary.ContainsKey(id))
        {
            BandDictionary[id].Distance.CurrentMotion =
                dstData[1];

            BandDictionary[id].Distance.Pace =
                float.Parse(dstData[2]);

            BandDictionary[id].Distance.Speed = 
                float.Parse(dstData[3]);

            BandDictionary[id].Distance.TotalDistance =
                long.Parse(dstData[4]);

            BandDictionary[id].Distance.SessionDistance =
                long.Parse(dstData[5]);

            BandDictionary[id].Distance.TotalDistanceToday =
                long.Parse(dstData[6]);

            if (DistanceUpdateEvent != null)
                DistanceUpdateEvent(BandDictionary[id].Distance, BandDictionary[id]);
        }
    }

    #endregion Distance

    #region Gsr

    void ThrowGsrUpdateEvent(string data)
    {
        if (data == null)
            return;

        string[] gsrData = data.Split(',');

        int id = int.Parse(gsrData[0]);

        if (BandDictionary.ContainsKey(id))
        {
            BandDictionary[id].Gsr.Resistance =
                int.Parse(gsrData[1]);

            if (GsrUpdateEvent != null)
                GsrUpdateEvent(BandDictionary[id].Gsr, BandDictionary[id]);
        }
    }

    #endregion Gsr

    #region HeartRate

    void ThrowHeartRateUpdateEvent(string data)
    {
        if (data == null)
            return;

        string[] hrtData = data.Split(',');

        int id = int.Parse(hrtData[0]);

        if (BandDictionary.ContainsKey(id))
        {
            BandDictionary[id].HeartRate.Rate = int.Parse(hrtData[1]);
            BandDictionary[id].HeartRate.Quality = hrtData[2];

            if (HeartRateUpdateEvent != null)
                HeartRateUpdateEvent(BandDictionary[id].HeartRate, BandDictionary[id]);
        }
    }

    #endregion HeartRate

    #region Pedometer

    void ThrowPedometerUpdateEvent(string data)
    {
        if (data == null)
            return;

        string[] pedData = data.Split(',');

        int id = int.Parse(pedData[0]);

        if (BandDictionary.ContainsKey(id))
        {
            BandDictionary[id].Pedometer.StepCount =
                long.Parse(pedData[1]);

            BandDictionary[id].Pedometer.SessionCount =
                long.Parse(pedData[2]);

            BandDictionary[id].Pedometer.StepCountToday =
                long.Parse(pedData[3]);

            if (PedometerUpdateEvent != null)
                PedometerUpdateEvent(BandDictionary[id].Pedometer, BandDictionary[id]);
        }
    }

    #endregion Pedometer

    #region SkinTemperature

    void ThrowSkinTemperatureUpdateEvent(string data)
    {
        if (data == null)
            return;

        string[] sknData = data.Split(',');

        int id = int.Parse(sknData[0]);

        if (BandDictionary.ContainsKey(id))
        {
            BandDictionary[id].SkinTemperature.Temperature =
                float.Parse(sknData[1]);

            if (SkinTemperatureUpdateEvent != null)
                SkinTemperatureUpdateEvent(BandDictionary[id].SkinTemperature, BandDictionary[id]);
        }
    }

    #endregion SkinTemperature

    #region UV

    void ThrowUVUpdateEvent(string data)
    {
        if (data == null)
            return;

        string[] uvData = data.Split(',');

        int id = int.Parse(uvData[0]);

        if (BandDictionary.ContainsKey(id))
        {
            BandDictionary[id].UVIndex.IndexLevel =
                uvData[1];

            BandDictionary[id].UVIndex.TimeStamp =
                long.Parse(uvData[2]);

            BandDictionary[id].UVIndex.IndexLevelToday =
                long.Parse(uvData[3]);

            if (UVIndexUpdateEvent != null)
                UVIndexUpdateEvent(BandDictionary[id].UVIndex, BandDictionary[id]);
        }
    }

    #endregion UV

    #region RRInterval

    void ThrowRRIntervalRateUpdateEvent(string data)
    {
        if (data == null)
            return;

        string[] rrData = data.Split(',');

        int id = int.Parse(rrData[0]);

        if (BandDictionary.ContainsKey(id))
        {
            BandDictionary[id].RRInterval.Interval =
                double.Parse(rrData[1]);

            if (RRIntervalUpdateEvent != null)
                RRIntervalUpdateEvent(BandDictionary[id].RRInterval, BandDictionary[id]);
        }
    }

    #endregion RRInterval

    #endregion Band Sensor Update Methods
}
