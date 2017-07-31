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