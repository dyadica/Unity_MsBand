// <copyright file="MsBand.cs" company="dyadica.co.uk">
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

using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

public class MsBand
{
    public int BandId;

    #region Sensors

    public Accelerometer Accelerometer =
       new Accelerometer();

    public Gyroscope Gyroscope =
        new Gyroscope();

    public Pedometer Pedometer =
        new Pedometer();

    public Altimeter Altimeter =
        new Altimeter();

    public Distance Distance =
        new Distance();

    public AmbientLight AmbientLight =
        new AmbientLight();

    public Barometer Barometer =
        new Barometer();

    public Calories Calories =
        new Calories();

    public Contact Contact =
        new Contact();

    public Gsr Gsr =
        new Gsr();

    public SkinTemperature SkinTemperature =
        new SkinTemperature();

    public UVIndex UVIndex =
        new UVIndex();

    public RRInterval RRInterval =
        new RRInterval();

    public HeartRate HeartRate =
        new HeartRate();

    #endregion Sensors

    public MsBand(int id)
    {
        BandId = id;
    }
}