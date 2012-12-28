using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

using Android.App;
using Android.Content;
using Android.OS;
using Android.Runtime;
using Android.Views;
using Android.Widget;
using Android.Graphics;
using Android.Views.Animations;
using Android.Hardware;

namespace ShakrLabs.ShotShakr
{
    [Activity(Label = "Shot Shakr")]
    public class ShotPicker : Activity, ISensorEventListener
    {
        private const int SHAKE_THRESHOLD = 500;
        private ViewFlipper mFlipper;
        private ViewFlipper sFlipper;

        private ShotPresenter presenter;
        private ShotNameTextView shotName;
        private ShotNameTextView shotName2;

        private TextView shotDetailName;
        private TextView shotDetailIngred;

        private SensorManager _shakeListener;

        private ImageButton btnTookThis;

        private SlidingDrawer filterDrawer;

        public ImageView coaster;
        public ImageView coaster2;
        private Typeface fontFace;

        private Boolean homescene;

        private Animation left_out;
        private Animation left_in;
        private Animation right_in;
        private Animation right_out;


        private const int SWIPE_MIN_DISTANCE = 120;
        private const int SWIPE_MAX_OFF_PATH = 250;
        private const int SWIPE_THRESHOLD_VELOCITY = 200;

        private Vibrator vibe;
        private bool isnew = true;
        private bool isfirst = false;

        public CheckBox chkVodka;
        public CheckBox chkTequila;

        List<String> filters = new List<String>();
        private GestureDetector gestureScanner;



        protected override void OnCreate(Bundle bundle)
        {
            base.OnCreate(bundle);
            SetContentView(Resource.Layout.shot_picker);
            // Create your application here
            _shakeListener = (SensorManager)GetSystemService(Context.SensorService);
         


            // /set Global Animations
            left_out = AnimationUtils.LoadAnimation(this, Resource.Animation.push_left_out);
            left_in = AnimationUtils.LoadAnimation(this, Resource.Animation.push_left_in);
            right_in = AnimationUtils.LoadAnimation(this, Resource.Animation.push_right_in);
            right_out = AnimationUtils.LoadAnimation(this, Resource.Animation.push_right_out);


            mFlipper = ((ViewFlipper)this.FindViewById<ViewFlipper>(Resource.Id.flipper));

            mFlipper.SetInAnimation(this, Resource.Animation.push_left_in);
            mFlipper.SetOutAnimation(this, Resource.Animation.push_left_out);

            vibe = (Vibrator)GetSystemService(Context.VibratorService);


            shotName = FindViewById<ShotNameTextView>(Resource.Id.shot);
            shotName2 = FindViewById<ShotNameTextView>(Resource.Id.shot_2);

            shotDetailName = FindViewById<TextView>(Resource.Id.shotName);
            shotDetailIngred = FindViewById<TextView>(Resource.Id.shotIngredients);

            mFlipper.Touch += mFlipper_Touch;

            fontFace = Typeface.CreateFromAsset(Assets, "Fonts/homework normal.TTF");

            homescene = true;

            shotName.SetTypeface(fontFace, TypefaceStyle.Normal);

            shotName.Text = "Shake to get this party started!";






            View inflatedDrawerLayout = LayoutInflater.Inflate(Resource.Layout.filterdrawer, null);

            int width = Window.Attributes.Width;
            int height = Window.Attributes.Height;

            Android.Widget.LinearLayout.LayoutParams param = new Android.Widget.LinearLayout.LayoutParams(width, height);
            Window.AddContentView(inflatedDrawerLayout, param);





        }
        protected override void OnResume()
        {
            base.OnResume();

            _shakeListener.RegisterListener(this, _shakeListener.GetDefaultSensor(SensorType.Accelerometer), SensorDelay.Ui);
            presenter = ShotPresenter.Current;
            List<Shot> shots = presenter.Shots;
        }

        protected override void OnPause()
        {
            base.OnPause();


        }

        void mFlipper_Touch(object sender, View.TouchEventArgs e)
        {
            throw new NotImplementedException();
        }

        public void onShake()
        {
            if (homescene)
            {
                vibe.Vibrate(100);
                if (isfirst)
                {
                    shotName.SetTypeface(fontFace, TypefaceStyle.Normal);
                    shotName.Text = presenter.GetRandomShot().shot_name;
                    isfirst = false;
                }
                else
                {
                    shotName2.SetTypeface(fontFace, TypefaceStyle.Normal);
                    shotName2.Text = presenter.GetRandomShot().shot_name;
                    isfirst = true;
                }

                mFlipper.ShowNext();
            }

        }


        #region Shake Listener

        /** Minimum movement force to consider. */
        private const int MIN_FORCE = 10;

        /**
         * Minimum times in a shake gesture that the direction of movement needs to
         * change.
         */
        private const int MIN_DIRECTION_CHANGE = 3;

        /** Maximum pause between movements. */
        private const int MAX_PAUSE_BETHWEEN_DIRECTION_CHANGE = 500;

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
                long now = CurrentTimeMillis();

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
                            onShake();
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
        private static readonly DateTime Jan1st1970 = new DateTime(1970, 1, 1, 0, 0, 0, DateTimeKind.Utc);

        public long CurrentTimeMillis()
        {
            return (long)(DateTime.UtcNow - Jan1st1970).TotalMilliseconds;
        }
        #endregion
    }
}