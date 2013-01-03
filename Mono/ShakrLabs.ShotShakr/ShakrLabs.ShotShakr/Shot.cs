using SQLite;
using SQLite;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;


namespace ShakrLabs.ShotShakr
{
    public class Shot
    {   
        [PrimaryKey, AutoIncrement]
        public int shot_id { get; set; }

        public string shot_name { get; set; }

        public string instructions { get; set; }

        public string ingredients { get; set; }

        public string filters { get; set; }

    }

    public class Database : SQLiteConnection
    {
        protected static Database db = null;
        protected static string dbLocation;

        protected Database(string path)
            : base(path)
        {
            CreateTable<Shot>();
        }
        static Database()
        {
            dbLocation = DatabaseFilePath;
            db = new Database(dbLocation);
        }

        public static string DatabaseFilePath
        {
            get
            {
#if SILVERLIGHT
            var path = "MwcDB.db3";
#else

#if __ANDROID__
                string libraryPath = Environment.GetFolderPath(Environment.SpecialFolder.Personal); ;
#else
			// we need to put in /Library/ on iOS5.1 to meet Apple's iCloud terms
			// (they don't want non-user-generated data in Documents)
			string documentsPath = Environment.GetFolderPath (Environment.SpecialFolder.Personal); // Documents folder
			string libraryPath = Path.Combine (documentsPath, "../Library/");
#endif
                var path = Path.Combine(libraryPath, "MwcDB.db3");
#endif
                return path;
            }
        }


        public static IEnumerable<Shot> QueryAllStots(List<String> Filters)
        {
            List<Shot> q = db.Table<Shot>().ToList();
            if (Filters != null)
                return q.Where(d => !Filters.Any(e => d.filters.Contains(e)));
            else
                return q;
            
                   
        }

        public static void InsertShots(List<Shot> shots)
        {
            
            db.InsertAll(shots);
        }
    }
   
}