using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;



namespace ShotShakr.Core
{
    public class ShotPresenter
    {
        public int FirstIndex;
        public int PreviousIndex;
        private List<Shot> _shots;
        public List<Shot> Shots {
            get
            {
                if (_shots == null || _shots.Count == 0)
                {
                    _shots = Database.QueryAllStots(Filters).ToList();
                }
                return _shots;
            }
            set
            {
                _shots = value;
            }
        }
        private List<String> _filters;
        public List<String> Filters {
            get { return _filters ?? (_filters = new List<String>()); } 
            
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

        public void InsertShots(List<Shot> shots)
        {
            if (Shots == null || Shots.Count == 0)
                Database.InsertShots(shots);
        }
        

        public Shot GetRandomShot()
        {
            Random random = new Random();
            int i = random.Next(0, Shots.Count()-1);

            return Shots[i];
        }


        public void Invalidate()
        {
            _shots = new List<Shot>();
        }
    }
}