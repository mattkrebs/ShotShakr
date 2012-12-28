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

namespace ShakrLabs.ShotShakr
{
    [Activity(Label = "Shot Shakr")]
    public class ShotPicker : Activity, ShakrLabs.ShotShakr.ShakeEventListener.OnShakeListener
    {
        private const int SHAKE_THRESHOLD = 500;
        private ViewFlipper mFlipper;
        private ViewFlipper sFlipper;

        private ShotPresenter presenter;
        private ShotNameTextView shotName;
        private ShotNameTextView shotName2;

        private TextView shotDetailName;
        private TextView shotDetailIngred;

      

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

            // /set Global Animations
            left_out = AnimationUtils.LoadAnimation(this, Resource.Animation.push_left_out);
            left_in = AnimationUtils.LoadAnimation(this, Resource.Animation.push_left_in);
            right_in = AnimationUtils.LoadAnimation(this, Resource.Animation.push_right_in);
            right_out = AnimationUtils.LoadAnimation(this, Resource.Animation.push_right_out);


            mFlipper = ((ViewFlipper)this.FindViewById<ViewFlipper>(Resource.Id.flipper));

            mFlipper.SetInAnimation(this, Resource.Animation.push_left_in);
            mFlipper.SetOutAnimation(this,Resource.Animation.push_left_out);
           
            vibe = (Vibrator) GetSystemService(Context.VibratorService);


            shotName =  FindViewById<ShotNameTextView>(Resource.Id.shot);
            shotName2 = FindViewById<ShotNameTextView>(Resource.Id.shot_2);

            shotDetailName = FindViewById<TextView>(Resource.Id.shotName);
            shotDetailIngred = FindViewById<TextView>(Resource.Id.shotIngredients);

            mFlipper.Touch +=mFlipper_Touch;

            fontFace = Typeface.CreateFromAsset(Assets,"Fonts/homework normal.TTF");

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

            presenter = ShotPresenter.Current;
            List<Shot> shots = presenter.Shots;
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

       
    }
}