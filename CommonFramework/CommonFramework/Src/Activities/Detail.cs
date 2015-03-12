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
using Android.Webkit;

namespace CommonFramework.Src.Activities
{
    [Activity(Label = "Detail",Theme = "@style/AppTheme")]
    public class Detail : Activity
    {
        private WebView webView1;
        protected override void OnCreate(Bundle bundle)
        {
            base.OnCreate(bundle);
            SetContentView(Resource.Layout.detail);
            string loadurl =Intent.GetStringExtra("Tourl");
            webView1 = (WebView)FindViewById(Resource.Id.webView1);
            webView1.Settings.JavaScriptEnabled = true;
            webView1.Settings.CacheMode = CacheModes.Default ;
            webView1.LoadUrl(loadurl);
        }
    }
}