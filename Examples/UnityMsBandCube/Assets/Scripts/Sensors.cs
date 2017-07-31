using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

public class Accelerometer
{
    public int BandId;

    public float RawX;
    public float RawY;
    public float RawZ;

    public float FilteredX;
    public float FilteredY;
    public float FilteredZ;

    public float Pitch;
    public float Roll;
}

public class Altimeter
{
    public int BandId;

    public long FlightsDescended;
    public float Rate;
    public long SteppingGain;
    public long SteppingLoss;
    public long StepsAscended;
    public long StepsDescended;
    public long TotalGain;
    public long TotalLoss;

    public long FlightsAscendedToday;
    public long TotalGainToday;
}

public class AmbientLight
{
    public int BandId;
    public int Brightness;
}

public class Barometer
{
    public int BandId;

    public double AirPressure;
    public double Temperature;
}

public class Calories
{
    public int BandId;

    public long TotalCalories;
    public long SessionCalories;

    public long TotalToday;
}

public class Contact
{
    public int BandId;

    public string State = "";
    public long TimeStamp;
}

public class Distance
{
    public int BandId;

    public string CurrentMotion = "";
    public float Pace;
    public float Speed;
    public long TotalDistance;
    public long SessionDistance;

    public long TotalDistanceToday;
}

public class Gsr
{
    public int BandId;
    public int Resistance;
}

public class Gyroscope
{
    public int BandId;

    public float RawX;
    public float RawY;
    public float RawZ;

    public float ValueX;
    public float ValueY;
    public float ValueZ;
}

public class Pedometer
{
    public int BandId;

    public long StepCount;
    public long SessionCount;

    public long StepCountToday;
}

public class SkinTemperature
{
    public int BandId;
    public float Temperature;
}

public class UVIndex
{
    public int BandId;

    public string IndexLevel = "";
    public long TimeStamp;

    public long IndexLevelToday;
}

public class RRInterval
{
    public int BandId;
    public double Interval;
}

public class HeartRate
{
    public int Rate;
    public string Quality = "";
}
