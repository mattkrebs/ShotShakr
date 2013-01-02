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
using System.Xml;

namespace ShakrLabs.ShotShakr
{
    [Activity(Label = "ShotShakr", Icon="@drawable/icon")]
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

            sFlipper = this.FindViewById<ViewFlipper>(Resource.Id.background_flipper);
            mFlipper = this.FindViewById<ViewFlipper>(Resource.Id.flipper);

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
            if (!homescene)
                return;

            if (e.Event.Action == MotionEventActions.Up)
            {

                sFlipper.SetInAnimation(this, Resource.Animation.push_left_in);
                sFlipper.SetOutAnimation(this, Resource.Animation.push_left_out);



                if (!isnew)
                {
                    if (isfirst)
                    {
                        SetShotDetails(shotName2.Text.ToString());
                    }
                    else
                    {
                        SetShotDetails(shotName.Text.ToString());
                    }

                    homescene = false;

                    sFlipper.ShowNext();
                   // ShowFirstTimeMsg();

                }
                else
                {
                    Toast.MakeText(this, "You must first shake to select a shot!", ToastLength.Long).Show();

                }

            }

        }

      




        public void SetShotDetails(String shotName)
        {
            Console.WriteLine("SelectedShot", "Shot Name = " + shotName);

            //tracker.trackEvent("Touches", "Touch", shotName , 3);
            //tracker.dispatch();

            var myShot = presenter.MyShakenShots[presenter.MyShakenShots.Count - 1];

            if (myShot != null)
            {
                shotDetailName.Typeface = fontFace;
                shotDetailName.Text = myShot.shot_name;
                shotDetailIngred.Typeface = fontFace;
                shotDetailIngred.Text = myShot.ingredients.Replace("||", "\n");

            }
        }

        public void onShake()
        {
            if (homescene)
            {
                isnew = false;
                vibe.Vibrate(100);
                var shakenShot = presenter.GetRandomShot();
                //Added Here to conserve a History of shots
                presenter.MyShakenShots.Add(shakenShot);
                if (isfirst)
                {
                    shotName.SetTypeface(fontFace, TypefaceStyle.Normal);
                    shotName.Text = shakenShot.shot_name;
                    isfirst = false;
                }
                else
                {
                    shotName2.SetTypeface(fontFace, TypefaceStyle.Normal);
                    shotName2.Text = shakenShot.shot_name;
                    isfirst = true;
                }

                mFlipper.ShowNext();
            }

        }

        public override bool OnKeyDown(Keycode keyCode, KeyEvent e)
        {
            // TODO Auto-generated method stub
            if ((keyCode == Keycode.Back) && (!homescene))
            {
                goBack();
                return true;
            }
            return base.OnKeyDown(keyCode, e);
        }

        public void goBack()
        {
            sFlipper.SetInAnimation(this, Resource.Animation.push_right_in);
            sFlipper.SetOutAnimation(this, Resource.Animation.push_right_out);

            sFlipper.ShowNext();
            homescene = true;
        }


        #region Shake Listener

        private static int FORCE_THRESHOLD = 1050;
        private static int TIME_THRESHOLD = 100;
        private static int SHAKE_TIMEOUT = 500;
        private static int SHAKE_DURATION = 1000;
        private static int SHAKE_COUNT = 3;
        private float mLastX = -1.0f, mLastY = -1.0f, mLastZ = -1.0f;

        private int mShakeCount = 0;
        private long mLastShake;
        private long mLastForce;
        private long mLastTime;


        public void OnAccuracyChanged(Sensor sensor, SensorStatus accuracy)
        {

        }

        public void OnSensorChanged(SensorEvent e)
        {
            // Console.WriteLine("Sensor Changed");
            // get sensor data
            float x = e.Values[1];
            float y = e.Values[2];
            float z = e.Values[0];

            if (e.Sensor.Type != SensorType.Accelerometer)
                return;

            long now = CurrentTimeMillis();
            //Console.WriteLine("Now minus lastForce {0}", (now - mLastForce));
            if ((now - mLastForce) > SHAKE_TIMEOUT)
            {
                mShakeCount = 0;
            }
            //Console.WriteLine("Now Minus LastTime {0}", (now - mLastTime));
            if ((now - mLastTime) > TIME_THRESHOLD)
            {
                long diff = now - mLastTime;
                float speed = Math.Abs(x + y + z - mLastX - mLastY - mLastZ) / diff * 10000;
                //   Console.WriteLine("Speed {0}", speed);
                if (speed > FORCE_THRESHOLD)
                {
                    if ((++mShakeCount >= SHAKE_COUNT) && (now - mLastShake > SHAKE_DURATION))
                    {
                        mLastShake = now;
                        mShakeCount = 0;
                        onShake();
                    }
                    mLastForce = now;
                }


                mLastTime = now;
                mLastX = x;
                mLastY = y;
                mLastZ = z;
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