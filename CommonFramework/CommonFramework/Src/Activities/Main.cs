using System;
using System.Linq;
using System.Text;

using Android.App;
using Android.Content;
using Android.OS;
using Android.Runtime;
using Android.Views;
using Android.Widget;
using Java.Lang;
using PengeSoft.Mob;
using PengeSoft.Client;
using CommonFramework.Src.Commons.Util;
using CommonFramework.Src.Commons.Wedget;
using Android.Webkit;
using PengeSoft.Data;
using Android.Graphics;
using Java.Net;
using Java.IO;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using Android.Graphics.Drawables;


namespace CommonFramework.Src.Activities
{
    [Activity(Label = "Main", Theme = "@style/AppTheme")]
    public class Main : Activity
    {

        private LinearLayout linearLayout2;
        private MainHandler mainhandler;
        private WebView webView1;
        private ISharedPreferences mSharedPreference;
        private LinearLayout linearLayout1;
        private Refreshfun reffun;
        private SaveLoadImg saveloadimg;
        private List<MyImageButton> myfunslist;
        private FixGridLayout fixGridLayout;
        ICollection<string> savelist;
        protected override void OnCreate(Bundle bundle)
        {
            base.OnCreate(bundle);
            SetContentView(Resource.Layout.main);
            findview();
            savelist = new  Collection<string>();
            webView1 = new WebView(this);
            mSharedPreference = GetSharedPreferences(Constant.Com_Fra, FileCreationMode.WorldWriteable);
            myfunslist = new List<MyImageButton>();
            mainhandler = new MainHandler(this);
            showwebviewfirst();
            showfunsold();
            GetNewinfo getnew = new GetNewinfo();
            getnew.context = this;
            getnew.Start();

            // Create your application here
        }
        private void findview()
        {
            linearLayout2 = (LinearLayout)FindViewById(Resource.Id.linearLayout2);
            linearLayout1 = (LinearLayout)FindViewById(Resource.Id.linearLayout1);

        }
        private void showfunsold()
        {
            ICollection<string> oldlist = mSharedPreference.GetStringSet("funslist",new Collection<string>());
            if (oldlist.Count > 0)
            {
                int i = 0;
                if (fixGridLayout != null)
                {
                    fixGridLayout.RemoveAllViews();
                }
                else
                {
                    fixGridLayout = new FixGridLayout(this);
                    fixGridLayout.setmCellHeight(200);
                    fixGridLayout.setmCellWidth(150);
                    linearLayout1.AddView(fixGridLayout);
                }
                foreach (string fun in oldlist)
                {
                    
                    string[] result = fun.Split('|');
                    string title = result[0];
                    string tourl = result[1];
                   
                     if (Android.OS.Environment.MediaMounted.Equals(Android.OS.Environment.ExternalStorageState))
                        {
                            string imgsavepath = Android.OS.Environment.ExternalStorageDirectory +
                                             File.Separator + "CommFramework" + File.Separator;
                            string savepath = imgsavepath + "funimg" + i.ToString() + ".png";
                            Bitmap bm = Loading.getLoacalBitmap(savepath);
                            if (bm != null)
                            {
                                MyImageButton oldfun = new MyImageButton(this, bm, title, tourl,"");
                               
                               
                                fixGridLayout.AddView(oldfun);
                            }
                     }
                     i++;
                    
                }
                //linearLayout1.AddView(fixGridLayout);
            }
        }
        private void showwebviewfirst()
        {
            webView1.Settings.JavaScriptEnabled = true;
            webView1.Settings.CacheMode = CacheModes.CacheElseNetwork;
            webView1.LoadUrl(mSharedPreference.GetString(Constant.HEADER_URL, ""));

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FillParent, mSharedPreference.GetInt(Constant.HEADERHEIGHT, 200));
            linearLayout2.AddView(webView1, lp);
        }

        private class GetNewinfo : Thread
        {
            public Main context;
            public override void Run()
            {
                try
                {
                    ISharedPreferences mSharedPreference = context.GetSharedPreferences(Constant.Com_Fra, FileCreationMode.WorldWriteable);
                    IEnvManagerSvr newinfo = RometeClientManager.GetClient(PsPropertiesUtil.GetProperties(context, "loadinfoUrl"), typeof(IEnvManagerSvr), 60000) as IEnvManagerSvr;
                    AppEnvironment appenvironment = newinfo.GetUserEnv(mSharedPreference.GetString(Constant.PS_UTAG, ""), 0);
                    Message msg = context.mainhandler.ObtainMessage();
                    Bundle bundle = new Bundle();
                    bundle.PutString("HeaderURL", appenvironment.MainPage.HeadUrl);
                    bundle.PutInt("HeaderHeight", appenvironment.MainPage.HeadHeight);
                    bundle.PutString("LoadImg", appenvironment.SplashImg);
                    ObjList list = appenvironment.MainPage.FuncDefs;
                    string[] funsname = new string[list.Count];
                    string[] funurl = new string[list.Count];
                    string[] funiconurl = new string[list.Count];
                    string[] funsbdnurl = new string[list.Count];
                    for (int i = 0; i < list.Count; i++)
                    {
                        funsname[i] = (list[i] as FuncEntry).Title;
                        funurl[i] = (list[i] as FuncEntry).Url;
                        funiconurl[i] = (list[i] as FuncEntry).IconUrl;
                        funsbdnurl[i] = (list[i] as FuncEntry).StateUrl;
                    }
                    bundle.PutStringArray("funsname", funsname);
                    bundle.PutStringArray("funsurl", funurl);
                    bundle.PutStringArray("funsiconurl", funiconurl);
                    bundle.PutStringArray("funsbdnurl", funsbdnurl);
                    msg.Data = bundle;
                    msg.What = 1;
                    msg.Target = context.mainhandler;
                    msg.SendToTarget();
                }
                catch (System.Exception e)
                {

                    throw;
                }
            }
        }
        private class MainHandler : Handler
        {
            Main context;
            public MainHandler(Main cont)
            {
                context = cont;
            }
            public override void HandleMessage(Message msg)
            {
                switch (msg.What)
                {
                    case 1:
                        context.setwebview(msg.Data);
                        context.reffun = new Refreshfun(context, msg.Data);
                        context.reffun.Start();

                        context.saveloadimg = new SaveLoadImg(context, msg.Data.GetString("LoadImg"));
                        context.saveloadimg.Start();
                        break;
                    case 2:
                        if (context.myfunslist.Count > 0)
                        {
                            context.Addmyfuns();
                        }
                        break;
                }
            }
        }
        private void Addmyfuns()
        {
            if (fixGridLayout != null)
            {                
                fixGridLayout.RemoveAllViews();
            }
            else
            {
                fixGridLayout = new FixGridLayout(this);
                fixGridLayout.setmCellHeight(200);
                fixGridLayout.setmCellWidth(150);
                linearLayout1.AddView(fixGridLayout);
            }
            int i = 0;
            foreach (MyImageButton mbtn in myfunslist)
            {
                
                fixGridLayout.AddView(mbtn);
                if (mbtn.mbadgeurl != "")
                {
                    string pngpath = Android.OS.Environment.ExternalStorageDirectory +
                                               File.Separator + "CommFramework" +
                                               File.Separator +
                                               "funicoimg" + i.ToString() + ".png";
                   Bitmap icobm= Loading.getLoacalBitmap(pngpath);
                   {
                       if (icobm != null)
                       {
                           BadgeView badge5 = new BadgeView(this, mbtn, fixGridLayout);
                           badge5.Text=(i.ToString());
                          // badge5.SetBackgroundDrawable(new BitmapDrawable(icobm));
                           badge5.SetBackgroundResource(Resource.Drawable.badge_ifaux);
                           badge5.TextSize=(16);
                           badge5.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
                           badge5.show();
                       }
                   }
                }
                i++;
            }
             
            
        }
        public void setwebview(Bundle bundle)
        {

            webView1.Settings.JavaScriptEnabled = true;
            webView1.Settings.CacheMode = CacheModes.Default;
            string headurl = bundle.GetString("HeaderURL");
            webView1.LoadUrl(headurl);
            LinearLayout.LayoutParams lp;
            int h=bundle.GetInt("HeaderHeight");

            lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FillParent, h);
            linearLayout2.RemoveAllViews();
            linearLayout2.AddView(webView1, lp);
            mSharedPreference.Edit().PutString(Constant.HEADER_URL, bundle.GetString("HeaderURL")).PutInt(Constant.HEADERHEIGHT, bundle.GetInt("HeaderHeight")).Commit();


        }

        public Bitmap getbitmap(string url)
        {
            URL myfileurl = null;
            Bitmap bm = null;
            try
            {
                myfileurl = new URL(url);
            }
            catch (MalformedURLException e)
            {

                e.PrintStackTrace();
            }
            try
            {
                HttpURLConnection conn = (HttpURLConnection)myfileurl.OpenConnection();
                conn.DoInput = true;
                conn.Connect();
                System.IO.Stream instream = conn.InputStream;
                bm = BitmapFactory.DecodeStream(instream);
            }
            catch (IOException e)
            {
                throw e;
            }
            return bm;
        }
        private class Refreshfun : Thread
        {
            Main context;
            Bundle bundle;
            public Refreshfun(Main mcontext, Bundle mbundle)
            {
                context = mcontext;
                bundle = mbundle;
            }
            public override void Run()
            {
                string[] funsname = bundle.GetStringArray("funsname");
                string[] funsurl = bundle.GetStringArray("funsurl");
                string[] funsiconurl = bundle.GetStringArray("funsiconurl");
                string[] funsbdnurl = bundle.GetStringArray("funsbdnurl");
                for (int i = 0; i < funsname.Length; i++)
                {
                    Bitmap bm = context.getbitmap(funsiconurl[i]);
                    Bitmap bdgbm = context.getbitmap(funsbdnurl[i]);
                    if (bm != null)
                    {
                        MyImageButton fun = new MyImageButton(context, bm, funsname[i], funsurl[i],funsbdnurl[i]);
                        context.myfunslist.Add(fun);
                        fun.Click += (sender, e) =>
                        {
                            Intent intent = new Intent(context, typeof(Detail));
                            intent.PutExtra("Tourl", fun.mToURL);
                            context.StartActivity(intent);
                        };
                        if (Android.OS.Environment.MediaMounted.Equals(Android.OS.Environment.ExternalStorageState))
                        {
                            string imgsavepath = Android.OS.Environment.ExternalStorageDirectory +
                                             File.Separator + "CommFramework" + File.Separator;
                            File tmpfile = new File(imgsavepath, "funimg" + i.ToString() + ".png");
                            File tmpicofile = new File(imgsavepath, "funicoimg" + i.ToString() + ".png");
                            tmpfile.ParentFile.Mkdirs();
                            if (tmpfile.Exists())
                            {
                                tmpfile.Delete();
                            }
                            if (tmpicofile.Exists())
                            {
                                tmpicofile.Delete();
                            }
                            try
                            {
                                System.IO.FileStream fos = System.IO.File.Create(imgsavepath + "funimg" + i.ToString() + ".png");

                                bm.Compress(Bitmap.CompressFormat.Png, 90, (System.IO.Stream)fos);
                                fos.Flush();
                                fos.Close();

                                System.IO.FileStream ficos = System.IO.File.Create(imgsavepath + "funicoimg" + i.ToString() + ".png");

                                bdgbm.Compress(Bitmap.CompressFormat.Png, 90, (System.IO.Stream)ficos);
                                ficos.Flush();
                                ficos.Close();
                                context.savelist.Add(funsname[i]+"|"+funsurl[i]);

                            }
                            catch (FileNotFoundException e)
                            {
                                // TODO Auto-generated catch block
                                e.PrintStackTrace();
                            }
                            catch (IOException e)
                            {
                                // TODO Auto-generated catch block
                                e.PrintStackTrace();
                            }
                        }
                        context.mSharedPreference.Edit().PutStringSet("funslist", context.savelist).Commit();

                    }

                }
                if (context.myfunslist.Count > 0)
                {

                    Message msg = context.mainhandler.ObtainMessage();
                    //Bundle bundlefun = new Bundle();
                    msg.What = 2;
                    // bundlefun.PutParcelableArrayList(Constant.MYBUTTON, context.myfunslist);

                    msg.Target = context.mainhandler;
                    msg.SendToTarget();
                }
            }
        }
        private class SaveLoadImg : Thread
        {
            string imgurl;
            Main context;
            public SaveLoadImg(Main mcontext, string murl)
            {
                context = mcontext;
                imgurl = murl;
            }
            public override void Run()
            {
                Bitmap bm = context.getbitmap(imgurl);
                if (bm != null)
                {                
                if (Android.OS.Environment.MediaMounted.Equals(Android.OS.Environment.ExternalStorageState))
                {
                    string imgsavepath = Android.OS.Environment.ExternalStorageDirectory +
                                     File.Separator + "CommFramework" + File.Separator;
                    File tmpfile = new File(imgsavepath, "loadimg.png");
                    tmpfile.ParentFile.Mkdirs();
                    if (tmpfile.Exists())
                    {
                        tmpfile.Delete();
                    }
                    try
                    {
                        System.IO.FileStream fos = System.IO.File.Create(imgsavepath + "loadimg.png");

                        bm.Compress(Bitmap.CompressFormat.Png, 90, (System.IO.Stream)fos);
                        fos.Flush();
                        fos.Close();
                        context.mSharedPreference.Edit().PutString("LoadingImg", imgsavepath+"loadimg.png").Commit();
                        
                    }
                    catch (FileNotFoundException e)
                    {
                        // TODO Auto-generated catch block
                        e.PrintStackTrace();
                    }
                    catch (IOException e)
                    {
                        // TODO Auto-generated catch block
                        e.PrintStackTrace();
                    }
                }
                }
            }
        }
    }
}