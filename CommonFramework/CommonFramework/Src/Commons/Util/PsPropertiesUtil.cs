using Android.Content;
using Android.Content.Res;
using Android.Util;
using Java.Util;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;

namespace CommonFramework.Src.Commons.Util
{
    class PsPropertiesUtil
    {
        private const string LOG_FLAG = "PsPropertiesUtil";
        /// <summary>
        /// 读配置文件
        /// </summary>
        /// <param name="context"></param>
        /// <param name="key"></param>
        /// <returns></returns>
        public static String GetProperties(Context context, string key)
        {
            String value = "";
            // 获取资源对象
            Properties props = new Properties();
            try
            {
                // 读取文件流,通过activity中的context攻取Config.properties的FileInputStream
                Stream inStream = context.Assets.Open("Config.properties");
                //装载Properties对象
                props.Load(inStream);
                // 关闭流
                inStream.Close();
                // 读取值
                value = props.GetProperty(key);
            }
            catch (Java.IO.IOException e)
            {
                // TODO Auto-generated catch block
                e.PrintStackTrace();
                Log.Error(LOG_FLAG, e.ToString());
                LogUtil.SaveLogInfoToFile(e.ToString());
            }
            return value;
        }
        /// <summary>
        /// 写配置文件
        /// </summary>
        /// <param name="context"></param>
        /// <param name="propertyKey"></param>
        /// <param name="propertyValue"></param>
        public static void changeValueByPropertyName(Context context, string propertyKey, string propertyValue)
        {
            // 实例化Properties对象
            Properties prop = new Properties();
            try
            {
                // 读取文件流
                Stream ins = context.Assets.Open("Config.properties", Access.Random);
                // 装载Properties对象
                prop.Load(ins);
                // 打开流
                Stream fos = context.OpenFileOutput("Config.properties", FileCreationMode.Private);
                IEnumeration ie = prop.PropertyNames();
                // 循环每一个节点
                while (ie.HasMoreElements)
                {
                    // 取出值
                    string s = ie.NextElement().ToString();
                    // 发是否是指定修改值得名称
                    if (!s.Equals(propertyKey))
                    {
                        // 设置值
                        prop.SetProperty(s, prop.GetProperty(s));
                    }
                }
                // 设置值
                prop.SetProperty(propertyKey, propertyValue);
                // 保存配置文件
                prop.Store(fos, "");
                ins.Close();
                // 关闭Properties对象
                fos.Close();
            }
            catch (FileNotFoundException e)
            {
                Log.Error(LOG_FLAG, e.ToString());
                LogUtil.SaveLogInfoToFile(e.ToString());
            }
            catch (IOException e)
            {
                Log.Error(LOG_FLAG, e.ToString());
                LogUtil.SaveLogInfoToFile(e.ToString());
            }
        }
    }
}
