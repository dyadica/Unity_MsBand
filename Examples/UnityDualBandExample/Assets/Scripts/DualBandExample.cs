// <copyright file="DualBandExample.cs" company="dyadica.co.uk">
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
using System;

using UnityEngine.UI;

public class DualBandExample : MonoBehaviour
{
    public Text Band0Acc;
    public Text Band1Acc;

    // Use this for initialization
    void Start ()
    {
        // Register for raw acc events.

        MsBandAndroidBridge.RawAccUpdateEvent +=
            MsBandAndroidBridge_RawAccUpdateEvent;

        // Connect to all paired and avaliable bands

        MsBandAndroidBridge.Instance.ConnectToPairedBands();
    }

    private void MsBandAndroidBridge_RawAccUpdateEvent(float x, float y, float z, MsBand band)
    {
        if (band.BandId == 0)
        {
            if(Band0Acc != null)
                Band0Acc.text = x.ToString() + ", " + y.ToString() + ", " + z.ToString();
        }

        if (band.BandId == 1)
        {
            if (Band1Acc != null)
                Band1Acc.text = x.ToString() + ", " + y.ToString() + ", " + z.ToString();
        }
    }

    void Update ()
    {
	    // Some update stuff!
	}

    void OnDestroy()
    {
        MsBandAndroidBridge.RawAccUpdateEvent -=
            MsBandAndroidBridge_RawAccUpdateEvent;
    }
}
