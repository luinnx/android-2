using Android.App;
using Android.Content;
using Android.Locations;
using Android.Net;
using Android.OS;
using Android.Provider;
using Android.Telephony;
using Android.Util;
using Java.Text;
using Java.Util;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace CommonFramework.Src.Commons.Util
{
    class CommonUtils
    { /// <summary>
        /// 获取软件版本号
        /// </summary>
        /// <returns></returns>
        public static string getVersionName(Context context)
        {
            string softVerCode = null;
            try
            {
                // 获取软件版本号，对应AndroidManifest.xml下android:versionName
                softVerCode = context.PackageManager.GetPackageInfo("CRMAndroid.CRMAndroid", 0).VersionName;
            }
            catch (Android.Content.PM.PackageManager.NameNotFoundException e)
            {
                e.PrintStackTrace();
                Log.Error("getVersionCode", e.ToString());
            }
            return softVerCode;
        }
        /// <summary>
        /// 获取手机IMEI号
        /// </summary>
        /// <returns></returns>
        public static String getImei(Context context)
        {
            TelephonyManager telephonyManager = (TelephonyManager)context.ApplicationContext.GetSystemService(Activity.TelephonyService);
            return telephonyManager.DeviceId;
        }
        /// <summary>
        /// 检测网络状态，是否可用
        /// </summary>
        /// <param name="context"></param>
        /// <returns></returns>
        public static bool isNetworkAvailable(Context context)
        {
            ConnectivityManager cm = (ConnectivityManager)context.ApplicationContext.GetSystemService(Context.ConnectivityService);
            if (cm != null)
            {
                NetworkInfo networkinfo = cm.ActiveNetworkInfo;
                return networkinfo != null && networkinfo.IsAvailable;
            }
            return false;
        }
        /// <summary>
        /// 判断网络类型 wifi  3G
        /// </summary>
        /// <param name="context"></param>
        /// <returns></returns>
        public static bool isWifiNetwrokType(Context context)
        {
            ConnectivityManager cm = (ConnectivityManager)context.ApplicationContext.GetSystemService(Context.ConnectivityService);
            NetworkInfo networkinfo = cm.ActiveNetworkInfo;
            if (networkinfo != null && networkinfo.IsAvailable)
            {
                if (networkinfo.TypeName.Equals("wifi"))
                {
                    return true;
                }
            }
            return false;
        }
        /// <summary>
        /// 判断GPS是否打开
        /// </summary>
        /// <param name="context"></param>
        /// <returns></returns>
        public static bool gpsIsEnable(Context context)
        {
            LocationManager lm = (LocationManager)context.ApplicationContext.GetSystemService(Context.LocationService);
            return lm.IsProviderEnabled(LocationManager.GpsProvider);
        }

        /// <summary>
        /// 打开或关闭GPS
        /// </summary>
        /// <param name="context"></param>
        public static void setGPSOn(Context context)
        {
            Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
            intent.PutExtra("enabled", true);
            context.SendBroadcast(intent);

            String provider = Settings.Secure.GetString(context.ContentResolver, Settings.Secure.LocationProvidersAllowed);
            if (!provider.Contains("gps"))
            { //if gps is disabled
                Intent poke = new Intent();
                poke.SetClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
                poke.AddCategory(Intent.CategoryAlternative);
                poke.SetData(Android.Net.Uri.Parse("3"));
                context.SendBroadcast(poke);
            }
        }
        /// <summary>
        /// 直接跳转到GPS设置界面
        /// </summary>
        /// <param name="context"></param>
        public static void turnGPSOn(Context context)
        {
            Intent intent = new Intent(Settings.ActionLocationSourceSettings);
            context.StartActivity(intent);
        }
        /// <summary>
        /// 去打开网络
        /// </summary>
        /// <param name="context"></param>
        public static void turnOpenNet(Context context)
        {
            Intent intent = new Intent();
            // 判断手机系统的版本 即API大于10 就是3.0或以上版本
            if (Build.VERSION.SdkInt > BuildVersionCodes.Honeycomb)
            {
                intent.SetAction(Settings.ActionWirelessSettings);
            }
            else
            {
                ComponentName component = new ComponentName(
                        "com.android.settings",
                        "com.android.settings.WirelessSettings");
                intent.SetComponent(component);
                intent.SetAction("android.intent.action.VIEW");
            }
            context.StartActivity(intent);
        }

        /// <summary>
        /// 判断设备是否有GPS模块
        /// </summary>
        /// <param name="context"></param>
        /// <returns></returns>
        public static bool hasGPSDevice(Context context)
        {
            LocationManager lm = (LocationManager)context.ApplicationContext.GetSystemService(Context.LocationService);
            if (lm == null)
                return false;
            IList<string> providers = lm.AllProviders;
            if (providers == null)
                return false;
            return providers.Contains(LocationManager.GpsProvider);
        }
        /// <summary>
        /// 将String转换成 Calendar
        /// </summary>
        /// <param name="dateStr"></param>
        /// <returns></returns>
        public static Calendar stringToCalendar(string dateStr)
        {
            SimpleDateFormat sdf = new SimpleDateFormat(Constant.DATE_STYLE1);
            Date date = null;
            Calendar calendar = null;
            try
            {
                date = sdf.Parse(dateStr);
                calendar = Calendar.Instance;
                calendar.Time = date;
            }
            catch (Java.Text.ParseException e)
            {
                // TODO Auto-generated catch block
                e.PrintStackTrace();
            }
            return calendar;
        }
        /// <summary>
        /// 指定时间是否晚于当前时间
        /// </summary>
        /// <param name="dateStr"></param>
        /// <returns></returns>
        public static bool isBeforeNow(string dateStr)
        {
            Calendar calendarA = Calendar.Instance;
            Calendar calendarB = stringToCalendar(dateStr);

            SimpleDateFormat sdf = new SimpleDateFormat(Constant.DATE_STYLE1);
            try
            {
                return calendarB.After(calendarA) || dateStr.Equals(sdf.Format(calendarA.Time));
            }
            catch (Java.Lang.IllegalArgumentException e)
            {
                // TODO Auto-generated catch block
                e.PrintStackTrace();
            }
            return false;
        }
        /// <summary>
        /// 时间选择器
        /// </summary>
        /// <param name="context"></param>
        /// <param name="strTime"></param>
        /// <param name="callBack"></param>
        public static void onCreateDataDialog(Context context, string strTime, EventHandler<DatePickerDialog.DateSetEventArgs> callBack)
        {
            Calendar calender;
            if (strTime == null || strTime.Equals(""))
            {
                calender = Calendar.Instance;
            }
            else
            {
                calender = CommonUtils.stringToCalendar(strTime);
            }

            new DatePickerDialog(context, callBack, calender.Get(CalendarField.Year), calender.Get(CalendarField.Month), calender.Get(CalendarField.DayOfMonth)).Show();
        }
        /// <summary>
        /// 获取现在时间
        /// </summary>
        /// <returns>返回字符串格式 yyyy-MM-dd HH:mm:ss</returns>
        public static String getStringDate()
        {
            Date currentTime = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat(Constant.DTIME_STYLE1);
            String dateString = formatter.Format(currentTime);
            return dateString;
        }
        /// <summary>
        /// 获取现在日期
        /// </summary>
        /// <returns>返回短日期字符串格式yyyy-MM-dd</returns>
        public static String getStringDateShort()
        {
            Date currentTime = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat(Constant.DATE_STYLE1);
            String dateString = formatter.Format(currentTime);
            return dateString;
        }
        /// <summary>
        /// 获取时间 小时:分;秒
        /// </summary>
        /// <returns>返回短时间字符串格式 HH:mm:ss</returns>
        public static String getTimeShort()
        {
            SimpleDateFormat formatter = new SimpleDateFormat(Constant.TIME_STYLE1);
            Date currentTime = new Date();
            String dateString = formatter.Format(currentTime);
            return dateString;
        }

    }
}
