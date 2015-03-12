using Java.IO;
using Java.Net;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;

namespace CommonFramework.Src.Commons.Util
{
    class Savehtmltolocal
    {
        private URL mURL;
        private string SavePath;
        private string savename;
        public Savehtmltolocal(string curl, string msavepath,string msavename)
        {
            mURL = new URL(curl);
            SavePath = msavepath;
            savename = msavename;
        }
        public bool savelocal()
        {
            try
            {
                if (Android.OS.Environment.ExternalStorageState.Equals(Android.OS.Environment.MediaMounted))
                {
                    Java.IO.File dir = new Java.IO.File(SavePath);
                    if (!dir.Exists())
                    {
                        dir.Mkdir();
                    }

                    HttpURLConnection connection = (HttpURLConnection)mURL.OpenConnection();
                    connection.RequestMethod = "GET";
                    connection.ReadTimeout = 5000;
                    Stream inStream = connection.InputStream;
                    StreamReader reader = new StreamReader(inStream);
                    StreamWriter writer = new StreamWriter(SavePath+savename);
                    writer.Write(reader.ReadToEnd());
                    writer.Flush();
                    reader.Close();
                    writer.Close();
                    return true;
                }
                return false;
            }
            catch (Exception)
            {
                return false;
                throw;
            }
            
        }


    }
}
