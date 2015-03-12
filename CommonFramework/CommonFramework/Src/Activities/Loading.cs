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
using Java.Lang;
using CommonFramework.Src.Commons.Util;
using PengeSoft.Mob;
using Android.Graphics;
using Java.IO;
using System.IO;
using Android.Graphics.Drawables;



namespace CommonFramework
{
    [Activity(Label = "@string/ApplicationName", MainLauncher = true, Theme = "@style/LoadingTheme", Icon = "@drawable/icon")]
    public class Loading : Activity
    {
        public static int MSG_INIT_OK = 1;
        public static int MSG_INIT_INFO = 2;
        public static int MSG_INIT_TIMEOUT = 9;
        private ISharedPreferences mSharedPreference;
        public static bool isTimeout = false;
        public static LinearLayout mainlayout;
        private string lodimgsrc;
        private IRunnable timeoutrunable;
        protected override void OnCreate(Bundle bundle)
        {
            base.OnCreate(bundle);
            // Create your application here
            SetContentView(Resource.Layout.loading);
            mainlayout = (LinearLayout)FindViewById(Resource.Id.mainlayout);
            mSharedPreference = GetSharedPreferences(Constant.Com_Fra, FileCreationMode.WorldWriteable);

            lodimgsrc = mSharedPreference.GetString(Constant.Lod_Img, "");
            if (lodimgsrc != "")
            {
                Bitmap bmp = getLoacalBitmap(lodimgsrc);
                if (bmp != null)
                {
                    mainlayout.SetBackgroundDrawable(new BitmapDrawable(bmp));
                }
            }
            else
            {

            }


            mHandler.mcontext = this;

            //timeoutrunable = new Runnable(() =>
            //{
            //    isTimeout = true;
            //    Message msg = Message.Obtain();
            //    msg.What = MSG_INIT_TIMEOUT;
            //    mHandler.SendMessage(msg);


            //});
            //mHandler.PostDelayed(timeoutrunable, 60000);

            LoadThread loadthread = new LoadThread();
            loadthread.context = this;
            loadthread.Start();


        }
        public static Bitmap getLoacalBitmap(string url)
        {
            
            try
            {
                FileStream fis = System.IO.File.Open(url, FileMode.Open);
                //FileInputStream fis = new FileInputStream(url);
                Bitmap bm= BitmapFactory.DecodeStream((Stream)fis);
                fis.Close();
                return bm;

            }
            catch (Java.IO.FileNotFoundException e)
            {
                e.PrintStackTrace();
                return null;
            }
            
        }
        public void sendInitInfo(string info, int msgtype)
        {
            Message msg1 = Message.Obtain();
            msg1.What = msgtype;
            msg1.Obj = info;
            mHandler.SendMessage(msg1);
        }


        private MessageHandler mHandler = new MessageHandler();

        private class MessageHandler : Handler
        {
            public Loading mcontext;
            public override void HandleMessage(Message msg)
            {
                base.HandleMessage(msg);

                if (msg.What == MSG_INIT_TIMEOUT)
                {
                    Toast.MakeText(mcontext, "timeout", ToastLength.Long).Show();
                    mcontext.Finish();
                }
                else if (msg.What == MSG_INIT_OK)
                {
                    Intent intent = new Intent(mcontext, typeof(Login));
                    mcontext.StartActivity(intent);
                    mcontext.Finish();

                }
                else if (msg.What == MSG_INIT_INFO)
                {


                }
            }
        }
        public class LoadThread : Thread
        {
            public Loading context;

            public override void Run()
            {
                base.Run();
                try
                {
                    if (!isTimeout)
                    {
                        if (context.lodimgsrc != "")
                        {
                            Thread.Sleep(1500);
                        }                        
                        context.sendInitInfo("Íê³É", MSG_INIT_OK);

                    }


                }
                catch (System.Exception)
                {

                    throw;
                }
            }
        }

    }
}