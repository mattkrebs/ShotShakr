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

namespace ShakrLabs.ShotShakr
{
    public class Shot
    {
        public int shot_id { get; set; }
        public string shot_name { get; set; }
        public string instructions { get; set; }
        public string ingredients { get; set; }
        public string filters { get; set; }

    }
}