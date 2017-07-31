// <copyright file="GUIManager.cs" company="dyadica.co.uk">
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
using System.Collections;
using System.Collections.Generic;

using UnityEngine.UI;

public class GUIManager : MonoBehaviour
{
    public static GUIManager Instance;

    private MsBand CurrentBand;

    public Text AccX, AccY, AccZ;
    public Text GyrX, GyrY, GyrZ;
    public Text CalT, CalS;

    public Text FlightsDescended, Rate, SteppingGain, SteppingLoss, StepsAscended, StepsDescended, TotalGain, TotalLoss;

    public Text Brightness;

    public Text AirPressure, AirTemperature;

    public Text ContactState;

    public Text CurrentMotion, Pace, Speed, TotalDistance, SessionDistance;

    public Text Resistance;

    public Text StepCount, SessionCount;

    public Text SkinTemperature;

    public Text IndexLevel;

    public Text RRInterval;

    public Text HRate, HQuality;

    public GameObject FeedbackPanel;
    public GameObject ConnectionPanel;

    public GameObject SelectButtonA;
    public GameObject SelectButtonB;

    void Awake()
    {
        Instance = this;

        if (FeedbackPanel != null)
            FeedbackPanel.SetActive(false);

        if (ConnectionPanel != null)
            ConnectionPanel.SetActive(true);
    }

	void Start ()
    {
        // bandManager = MsBandAndroidBridge.Instance;

        MsBandAndroidBridge.DeviceListUpdateEvent += 
            MsBandAndroidBridge_DeviceListUpdateEvent;

        MsBandAndroidBridge.ConnectedBandEvent += 
            MsBandAndroidBridge_ConnectedBandEvent;
    }

    private void MsBandAndroidBridge_DeviceListUpdateEvent(Dictionary<int, string> devices)
    {
        // throw new System.NotImplementedException();
    }

    private void MsBandAndroidBridge_ConnectedBandEvent(int id, MsBand band)
    {
        // throw new System.NotImplementedException();

        if (FeedbackPanel != null)
            FeedbackPanel.SetActive(true);

        if (ConnectionPanel != null)
            ConnectionPanel.SetActive(false);

        if (SelectButtonA != null)
            SelectButtonA.SetActive(MsBandAndroidBridge.Instance.BandDictionary.ContainsKey(0));

        if (SelectButtonB != null)
            SelectButtonB.SetActive(MsBandAndroidBridge.Instance.BandDictionary.ContainsKey(1));
    }

    void Update ()
    {
        if (CurrentBand == null || CurrentBand.BandId == -1)
            return;
        
        #region Band 1 Sensor Display

        // Update Acc

        if (AccX != null)
            AccX.text = CurrentBand.Accelerometer.RawX.ToString();

        if (AccY != null)
            AccY.text = CurrentBand.Accelerometer.RawY.ToString();

        if (AccZ != null)
            AccZ.text = CurrentBand.Accelerometer.RawZ.ToString();

        // Update Gyr

        if (GyrX != null)
            GyrX.text = CurrentBand.Gyroscope.RawX.ToString();

        if (GyrY != null)
            GyrY.text = CurrentBand.Gyroscope.RawY.ToString();

        if (GyrZ != null)
            GyrZ.text = CurrentBand.Gyroscope.RawZ.ToString();

        // Update Calories

        if (CalT != null)
            CalT.text = CurrentBand.Calories.TotalCalories.ToString();

        if (CalS != null)
            CalS.text = CurrentBand.Calories.SessionCalories.ToString();    

        // Update Contact

        if (ContactState != null)
            ContactState.text = CurrentBand.Contact.State.ToString();

        // Update Distance

        if (CurrentMotion != null)
            CurrentMotion.text = CurrentBand.Distance.CurrentMotion.ToString();

        if (Pace != null)
            Pace.text = CurrentBand.Distance.Pace.ToString();

        if (Speed != null)
            Speed.text = CurrentBand.Distance.Speed.ToString();

        if (TotalDistance != null)
            TotalDistance.text = CurrentBand.Distance.TotalDistance.ToString();

        if (SessionDistance != null)
            SessionDistance.text = CurrentBand.Distance.SessionDistance.ToString();

        // Update Pedometer

        if (StepCount != null)
            StepCount.text = CurrentBand.Pedometer.StepCount.ToString();

        if (SessionCount != null)
            SessionCount.text = CurrentBand.Pedometer.SessionCount.ToString();

        // Update Skin

        if (SkinTemperature != null)
            SkinTemperature.text = CurrentBand.SkinTemperature.Temperature.ToString();

        // Update UVIndex

        if (IndexLevel != null)
            IndexLevel.text = CurrentBand.UVIndex.IndexLevel.ToString();

        // Update HeartRate

        if (HRate != null)
            HRate.text = CurrentBand.HeartRate.Rate.ToString();

        if (HQuality != null)
            HQuality.text = CurrentBand.HeartRate.Quality.ToString();

        #endregion Band 1 Sensor Display

        #region Band 2 Sensor Display

        // Update AmbientLight

        if (Brightness != null)
            Brightness.text = CurrentBand.AmbientLight.Brightness.ToString();

        // Update Altimeter

        if (FlightsDescended != null)
            FlightsDescended.text = CurrentBand.Altimeter.FlightsDescended.ToString();

        if (Rate != null)
            Rate.text = CurrentBand.Altimeter.Rate.ToString();

        if (SteppingGain != null)
            SteppingGain.text = CurrentBand.Altimeter.SteppingGain.ToString();

        if (SteppingLoss != null)
            SteppingLoss.text = CurrentBand.Altimeter.SteppingLoss.ToString();

        if (StepsAscended != null)
            StepsAscended.text = CurrentBand.Altimeter.StepsAscended.ToString();

        if (StepsDescended != null)
            StepsDescended.text = CurrentBand.Altimeter.StepsDescended.ToString();

        if (TotalGain != null)
            TotalGain.text = CurrentBand.Altimeter.TotalGain.ToString();

        if (TotalLoss != null)
            TotalLoss.text = CurrentBand.Altimeter.TotalLoss.ToString();

        // Update RRInterval

        if (RRInterval != null)
            RRInterval.text = CurrentBand.RRInterval.Interval.ToString();

        // Update Gsr

        if (Resistance != null)
            Resistance.text = CurrentBand.Gsr.Resistance.ToString();

        // Update Barometer

        if (AirPressure != null)
            AirPressure.text = CurrentBand.Barometer.AirPressure.ToString();

        if (AirTemperature != null)
            AirTemperature.text = CurrentBand.Barometer.Temperature.ToString();

        #endregion Band 2 Sensor Display
    }

    public void SelectBand(int id)
    {

        CurrentBand = MsBandAndroidBridge.Instance.BandDictionary[id];
    }

    public void LoadConnectionScreen()
    {
        MsBandAndroidBridge.Instance.DisconnectFromAllBands();

        if (FeedbackPanel != null)
            FeedbackPanel.SetActive(false);

        if (ConnectionPanel != null)
            ConnectionPanel.SetActive(true);
    }
}
