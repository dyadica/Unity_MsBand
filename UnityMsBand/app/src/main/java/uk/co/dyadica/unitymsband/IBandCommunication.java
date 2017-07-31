package uk.co.dyadica.unitymsband;

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

public interface IBandCommunication
{
    /**
     * Method called upon connection of a band
     * @param id the bands id value
     */
    public void bandConnected(int id);

    /**
     * Method called upon a failed connection of a band
     * @param id the bands id value
     */
    public void bandNotConnected(int id);
}
