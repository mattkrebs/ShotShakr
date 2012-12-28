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
using Android.Opengl;

namespace ShakrLabs.ShotShakr
{
    public class ShotNameTextView : TextView
    {
        	private Matrix mForward = new Matrix();


            public ShotNameTextView(Context context) :base(context)
            {
            }
            public ShotNameTextView(Context context, Android.Util.IAttributeSet attr)
                : base(context, attr)
            { }
            protected override void OnDraw(Android.Graphics.Canvas canvas)
            {
                canvas.Save();
                canvas.Rotate(4);
                base.OnDraw(canvas);
                canvas.Restore();
            }


    }
}