﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

using Android.App;
using Android.Content;
using Android.OS;
using Android.Runtime;
using Android.Views;
using Android.Widget;
using Android.Media;

namespace MonoCross.Utilities.Notification
{
    public class Notify
    {
        static Notify()
        {
        }

        public static void PlaySound(Context context, int resID)
        {
            MediaPlayer mp = MediaPlayer.Create(context, resID);
            mp.Completion += new EventHandler(mp_Completion);
            mp.Error +=new EventHandler<MediaPlayer.ErrorEventArgs>(mp_Error);
            mp.Start();
        }

        static void mp_Error(object sender, MediaPlayer.ErrorEventArgs e)
        {
            e.Mp.Release();
            e.Handled = false;
        }

        static void mp_Completion(object sender, EventArgs e)
        {
            MediaPlayer mp = sender as MediaPlayer;
            mp.Release();
        }

        public static void PlaySound(Context context, string uriString)
        {
            Android.Net.Uri uri = Android.Net.Uri.Parse(uriString);
            MediaPlayer mp = MediaPlayer.Create(context, uri);
            mp.Completion += new EventHandler(mp_Completion);
            mp.Error += new EventHandler<MediaPlayer.ErrorEventArgs>(mp_Error);
            mp.Start();
        }

        // simple shortcuts
        public static void Vibrate(Context context, int duration)
        {
            Vibrator v = (Android.OS.Vibrator)context.GetSystemService(Context.VibratorService);
            v.Vibrate(duration);
        }
    }
}