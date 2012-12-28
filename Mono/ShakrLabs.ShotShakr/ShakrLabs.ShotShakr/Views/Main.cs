using System;

using Android.App;
using Android.Content;
using Android.Runtime;
using Android.Views;
using Android.Widget;
using Android.OS;
using System.Collections.Generic;
using System.IO;

namespace ShakrLabs.ShotShakr
{
    [Activity(Label = "ShakrLabs.ShotShakr", MainLauncher = true, Icon = "@drawable/icon")]
    public class Main : Activity
    {
        int count = 1;

        protected override void OnCreate(Bundle bundle)
        {
            base.OnCreate(bundle);

            // Set our view from the "main" layout resource
            SetContentView(Resource.Layout.splash);


            //Load Shots Into Memory

            string content;
            //Reads the HTML file from the asset folder and puts it into a string.
            using (var sr = new StreamReader(Assets.Open("alldata.txt")))
                content = sr.ReadToEnd();

            List<Shot> shots = Newtonsoft.Json.JsonConvert.DeserializeObject<List<Shot>>(content);

            ShotPresenter.Current.Shots =  shots;

            StartActivity(typeof(ShotPicker));



           
        }
    }
}

