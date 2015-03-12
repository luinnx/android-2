using Android.Content;
using Android.Graphics;
using Android.OS;
using Android.Runtime;
using Android.Widget;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace CommonFramework.Src.Commons.Wedget
{
    class MyImageButton : LinearLayout 
    {

        public MyImageButton(Context context ,Bitmap bm, string texttitle,string tourl,string badgeurl):base(context)
        {
            mToURL = tourl;
            mbadgeurl = badgeurl;
            mButtonImage = new ImageView(context);
            mButtonText = new TextView(context);
            setImagebitmap(bm);
            mButtonImage.SetPadding(30, 30, 0, 0);
            setText(texttitle);
            Color color = new Color(0xFFFFFF);
            setTextColor(Color.Black);
            mButtonText.SetPadding(50, 0, 0, 0);
            // 设置本布局的属性
            Clickable = true;
            Focusable = true;

            SetBackgroundColor(new Color(0xFFFFFF));
            //setBackgroundResource(android.R.drawable.btn_default); // 布局才用普通按钮的背景
             Orientation=Orientation.Vertical; // 垂直布局 3
            // 首先添加Image，然后才添加Text
            // 添加顺序将会影响布局效果
            AddView(mButtonImage);
            AddView(mButtonText);
        }
        public MyImageButton(Context context, int bmid, string texttitle, string tourl)
            : base(context)
        {
            mButtonImage = new ImageView(context);
            mButtonText = new TextView(context);
            setImageResource(bmid);
            mButtonImage.SetPadding(30, 30, 0, 0);
            setText(texttitle);
            Color color = new Color(0xFFFFFF);
            setTextColor(Color.Black);
            mButtonText.SetPadding(50, 0, 0, 0);
            // 设置本布局的属性
            Clickable = true;
            Focusable = true;

            SetBackgroundColor(new Color(0xFFFFFF));
            //setBackgroundResource(android.R.drawable.btn_default); // 布局才用普通按钮的背景
            Orientation = Orientation.Vertical; // 垂直布局 3
            // 首先添加Image，然后才添加Text
            // 添加顺序将会影响布局效果
            AddView(mButtonImage);
            AddView(mButtonText);
        }  
        
        
        // ----------------public method-----------------------------
        /*
         * setImageResource方法
         */
        public void setImageResource(int resId)
        {
            mButtonImage.SetImageResource(resId);

        }
        public void setImagebitmap(Bitmap bm)
        {
            mButtonImage.SetImageBitmap(bm);

        }

        /*
         * setText方法
         */
        public void setText(int resId)
        {
            mButtonText.SetText(resId);
            mButtonText.SetTextSize(Android.Util.ComplexUnitType.Dip, 20);
        }

        public void setText(string buttonText)
        {
            mButtonText.SetText(buttonText,TextView.BufferType.Normal);
        }

        /*
         * setTextColor方法
         */
        public void setTextColor(Color color)
        {
            mButtonText.SetTextColor(color);
        }

        // ----------------private attribute-----------------------------
        private ImageView mButtonImage = null;
        private TextView mButtonText = null;
        public string mToURL = "";
        public string mbadgeurl = "";


        //序列化
        

    }
}
