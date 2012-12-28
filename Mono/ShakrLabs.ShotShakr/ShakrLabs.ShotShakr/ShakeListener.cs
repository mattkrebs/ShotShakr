using System;
using System.Collections.Generic;
using System.Text;
using Android.Hardware;
using Android.Content;
using Java.Lang;
namespace ShakrLabs.ShotShakr
{


    /**
     * Listener that detects shake gesture.
     */
    public class ShakeEventListener : ISensorEventListener
    {


        /** Minimum movement force to consider. */
        private const int MIN_FORCE = 10;

        /**
         * Minimum times in a shake gesture that the direction of movement needs to
         * change.
         */
        private const int MIN_DIRECTION_CHANGE = 3;

        /** Maximum pause between movements. */
        private const int MAX_PAUSE_BETHWEEN_DIRECTION_CHANGE = 200;

        /** Maximum allowed time for shake gesture. */
        private const int MAX_TOTAL_DURATION_OF_SHAKE = 400;

        /** Time when the gesture started. */
        private long mFirstDirectionChangeTime = 0;

        /** Time when the last movement started. */
        private long mLastDirectionChangeTime;

        /** How many movements are considered so far. */
        private int mDirectionChangeCount = 0;

        /** The last x position. */
        private float lastX = 0;

        /** The last y position. */
        private float lastY = 0;

        /** The last z position. */
        private float lastZ = 0;

        /** OnShakeListener that is called when shake is detected. */
        private OnShakeListener mShakeListener;

        /**
         * Interface for shake gesture.
         */
        public interface OnShakeListener
        {

            /**
             * Called when shake gesture is detected.
             */
            void onShake();
        }

        public void setOnShakeListener(OnShakeListener listener)
        {
            mShakeListener = listener;
        }

       

        public void Dispose()
        {
           
        }

        public IntPtr Handle
        {
            get { throw new NotImplementedException(); }
        }
        

        /**
         * Resets the shake parameters to their default values.
         */
        private void resetShakeParameters()
        {
            mFirstDirectionChangeTime = 0;
            mDirectionChangeCount = 0;
            mLastDirectionChangeTime = 0;
            lastX = 0;
            lastY = 0;
            lastZ = 0;
        }







        public void OnAccuracyChanged(Sensor sensor, SensorStatus accuracy)
        {
           
        }

        public void OnSensorChanged(SensorEvent e)
        {
            Console.WriteLine("Sensor Changed");
            // get sensor data
            float x = e.Values[1];
            float y = e.Values[2];
            float z = e.Values[0];

            // calculate movement
            float totalMovement = System.Math.Abs(x + y + z - lastX - lastY - lastZ);

            if (totalMovement > MIN_FORCE)
            {

                // get time
                long now = DateTime.Now.Millisecond;

                // store first movement time
                if (mFirstDirectionChangeTime == 0)
                {
                    mFirstDirectionChangeTime = now;
                    mLastDirectionChangeTime = now;
                }

                // check if the last movement was not long ago
                long lastChangeWasAgo = now - mLastDirectionChangeTime;
                if (lastChangeWasAgo < MAX_PAUSE_BETHWEEN_DIRECTION_CHANGE)
                {

                    // store movement data
                    mLastDirectionChangeTime = now;
                    mDirectionChangeCount++;

                    // store last sensor data 
                    lastX = x;
                    lastY = y;
                    lastZ = z;

                    // check how many movements are so far
                    if (mDirectionChangeCount >= MIN_DIRECTION_CHANGE)
                    {

                        // check total duration
                        long totalDuration = now - mFirstDirectionChangeTime;
                        if (totalDuration < MAX_TOTAL_DURATION_OF_SHAKE)
                        {
                            mShakeListener.onShake();
                            resetShakeParameters();
                        }
                    }

                }
                else
                {
                    resetShakeParameters();
                }
            }
        }
    }
}
