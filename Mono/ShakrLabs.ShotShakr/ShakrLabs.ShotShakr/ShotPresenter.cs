using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Json;

using Android.App;
using Android.Content;
using Android.OS;
using Android.Runtime;
using Android.Views;
using Android.Widget;
using System.IO;
using Android.Util;


namespace ShakrLabs.ShotShakr
{
    public class ShotPresenter
    {
        public int FirstIndex;
        public int PreviousIndex;
        private List<Shot> _shots;
        public List<Shot> Shots {
            get
            {
                return _shots;
            }
            set
            {
                _shots = value;
            }
        }
        private static ShotPresenter _presenter;
        /// <summary>
        /// This property gets a reference to the singleton instance of the presenter.
        /// </summary>
        /// <value>
        /// The singleton presenter.
        /// </value>
        public static ShotPresenter Current
        {
            get { return _presenter ?? (_presenter = new ShotPresenter()); }
        }
        private List<Shot> _myShakenShots;
        public List<Shot> MyShakenShots 
        {
            get { return _myShakenShots ?? (_myShakenShots = new List<Shot>()); } 
            
        }
       


        public Shot GetRandomShot()
        {
            Random random = new Random();
            int i = random.Next(0, Shots.Count()-1);

            return Shots[i];
        }

    }
}