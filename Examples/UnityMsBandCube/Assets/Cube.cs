using UnityEngine;
using System.Collections;

using UnityEngine.UI;
using System;

public class Cube : MonoBehaviour
{
    public GameObject CubeObject;

    public Text RollDisplay;
    public Text PitchDisplay;

    private float roll = 0;
    private float pitch = 0;

    void Start ()
    {
        // Register for acc events

        MsBandAndroidBridge.RawAccUpdateEvent +=
            MsBandAndroidBridge_RawAccUpdateEvent;

        // Connect to a band or more

        MsBandAndroidBridge.Instance.ConnectToPairedBands();
    }

    void Update()
    {
        if (RollDisplay != null)
            RollDisplay.text = "Roll: " + roll.ToString();

        if (PitchDisplay != null)
            PitchDisplay.text = "Pitch: " + pitch.ToString();

        if (CubeObject != null)
        {
            Vector3 cubeRot = new Vector3(roll, pitch, 0);
            CubeObject.transform.localEulerAngles = cubeRot;
        }
    }

    void OnDestroy()
    {
        MsBandAndroidBridge.RawAccUpdateEvent -=
            MsBandAndroidBridge_RawAccUpdateEvent;
    }

    private void MsBandAndroidBridge_RawAccUpdateEvent(float x, float y, float z, MsBand band)
    {
        roll = band.Accelerometer.Roll;
        pitch = band.Accelerometer.Pitch;
    }
}
