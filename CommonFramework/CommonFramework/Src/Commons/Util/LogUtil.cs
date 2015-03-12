using Java.IO;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace CommonFramework.Src.Commons.Util
{
    class LogUtil
    {
        /// <summary>
        /// 保存错误信息到文件中 
        /// </summary>
        /// <param name="logStr"></param>
        public static void SaveLogInfoToFile(string logStr)
        {
            try
            {
                string fileName = "PS_COMF.log";
                if (Android.OS.Environment.ExternalStorageState.Equals(Android.OS.Environment.MediaMounted))
                {
                    string path = "/sdcard/Download/Log/";

                    File dir = new File(path);
                    if (!dir.Exists())
                    {
                        dir.Mkdir();
                    }
                    System.IO.FileStream fs = new System.IO.FileStream(path + fileName, System.IO.FileMode.Append);
                    System.IO.StreamWriter sw = new System.IO.StreamWriter(fs, System.Text.Encoding.Default);
                    sw.WriteLine("\n--------开始" + CommonUtils.getStringDate() + "------");
                    sw.Write(logStr);
                    sw.WriteLine("--------------结束线---------------\n");
                    sw.Close();
                    fs.Close();
                }
            }
            catch (IOException e)
            {
                e.PrintStackTrace();
            }
        }
    }
}
