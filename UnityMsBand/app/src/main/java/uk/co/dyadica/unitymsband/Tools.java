package uk.co.dyadica.unitymsband;

/**
 * Created by dyadica.co.uk on 31/01/2016.

 * This source is subject to the dyadica.co.uk Permissive License.
 * Please see the http://www.dyadica.co.uk/permissive-license file for more information.
 * All other rights reserved.

 * THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY
 * KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A
 * PARTICULAR PURPOSE.
 */

public class Tools
{
    // region Lowpass Filter

    public static float[] lowPass( float[] input, float[] output )
    {
        float ALPHA = 0.15f;

        if ( output == null ) return input;

        for ( int i=0; i<input.length; i++ ) {
            output[i] = output[i] + ALPHA * (input[i] - output[i]);
        }

        return output;
    }

    public static float[] lowPass( float[] input, float[] output, float ALPHA)
    {
        if ( output == null ) return input;

        for ( int i=0; i<input.length; i++ ) {
            output[i] = output[i] + ALPHA * (input[i] - output[i]);
        }

        return output;
    }

    public static long[] lowPass( long[] input, long[] output, float ALPHA)
    {
        if ( output == null ) return input;

        for ( int i=0; i<input.length; i++ ) {
            output[i] = output[i] + (long)ALPHA * (input[i] - output[i]);
        }

        return output;
    }

    // endregion Lowpass Filter

    // region IMU

    public static float[] getBasicOrientation(float x, float y, float z)
    {
        float pitch, roll;

        pitch = (float)Math.atan2(x, Math.sqrt(y * y) + (z * z));
        roll = (float)Math.atan2(y, Math.sqrt(x * x) + (z * z));

        pitch *= 180.0 / Math.PI;
        roll *= 180.0 / Math.PI;

        return new float[]{ pitch, roll };
    }

    // 6DOF Removed for release!

    // endregion IMU
}
