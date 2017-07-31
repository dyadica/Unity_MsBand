// <copyright file="HeartRateCall.cs" company="dyadica.co.uk">
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

using UnityEngine.UI;

public class HeartRateCall : MonoBehaviour
{
    // Text field set by the unity editor

    public Text HeartRateDisplay;

    public Text AccDisplay;

	void Start ()
    {
        // Register for heart rate events.

        MsBandAndroidBridge.HeartRateUpdateEvent += 
            MsBandAndroidBridge_HeartRateUpdateEvent;

        // Register for raw acc events.

        MsBandAndroidBridge.RawAccUpdateEvent += 
            MsBandAndroidBridge_RawAccUpdateEvent;

        MsBandAndroidBridge.Instance.ConnectToPairedBands();

        // In order to use the heart rte sensor you need to run the app once on the phone
        // in a non vr context (standard manifest) to allow for the permissions check to be 
        // performed and heart sensor allowed etc. Once done swap the manifest to reflect 
        // that of your vr app and enjoy. (I am looking into this!)

        // MsBandAndroidBridge.Instance.CheckHeartSensorPermissions();

    }

    private void MsBandAndroidBridge_RawAccUpdateEvent(float x, float y, float z, MsBand band)
    {
        if (AccDisplay != null)
        {
            AccDisplay.text = x.ToString() + ", " + y.ToString() + ", " + z.ToString();
        }
    }

    private void MsBandAndroidBridge_HeartRateUpdateEvent(HeartRate heartRate, MsBand band)
    {
        if(HeartRateDisplay != null)
        {
            HeartRateDisplay.text = heartRate.Rate.ToString();
        }
    }

    void OnDestroy()
    {
        MsBandAndroidBridge.RawAccUpdateEvent -=
             MsBandAndroidBridge_RawAccUpdateEvent;

        MsBandAndroidBridge.HeartRateUpdateEvent -=
            MsBandAndroidBridge_HeartRateUpdateEvent;
    }
}

