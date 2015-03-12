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
using PengeSoft.Client;
using PengeSoft.Service.Auther;
using CommonFramework.Src.Commons.Util;
using CommonFramework.Src.Domain;
using System.Net;
using Android.Util;
using CommonFramework.Src.Commons.Wedget;
using CommonFramework.Src.Activities;

namespace CommonFramework
{
    [Activity(Label = "@string/appname", Theme = "@style/AppTheme", WindowSoftInputMode = SoftInput.AdjustPan)]
    public class Login : Activity,View.IOnClickListener
    {
        private ISharedPreferences mSharedPreference;
        private EditText login_edit_account;
        private EditText login_edit_pwd;
        private Button login_btn_loginnew;
        private LoginUser user;
        private LoginHandler handler;
        private LoadingDialog loadingDialog;
        private LoginThread loginThread;
        public const int RETURN = 1;
        public const int NETERROR = 2;
        public const int ERROR = 3;

        protected override void OnCreate(Bundle bundle)
        {
            base.OnCreate(bundle);
            SetContentView(Resource.Layout.login);
            handler = new LoginHandler(this);
            getview();
            // Create your application here
            
        }
        private void getview()
        {
            login_edit_account = (EditText)FindViewById(Resource.Id.login_edit_account);
            login_edit_pwd = (EditText)FindViewById(Resource.Id.login_edit_pwd);
            login_btn_loginnew = (Button)FindViewById(Resource.Id.login_btn_loginnew);
            login_btn_loginnew.SetOnClickListener(this);
            mSharedPreference = GetSharedPreferences(Constant.Com_Fra, FileCreationMode.WorldWriteable);
            login_edit_account.Text = mSharedPreference.GetString(Constant.PS_UNAME, "zcj");
            login_edit_pwd.Text = mSharedPreference.GetString(Constant.PS_PWD, "");
        }

        public void OnClick(View v)
        {
            switch (v.Id)
            {
                case Resource.Id.login_btn_loginnew:
                    btnlogin();
                    break;
            }
        }
        private class LoginThread: Thread{
            public Login context;

            public override void Run()
            {

                try
                {
                    IUserAuther auther = RometeClientManager.GetClient(PsPropertiesUtil.GetProperties(context, "loginUrl"), typeof(IUserAuther), 60000) as IUserAuther;
                    context.user.LoginResult = auther.Login(context.user.UserName, context.user.Password, context.user.IMEI);
                    context.handler.SendEmptyMessage(Login.RETURN);
                }
                catch (WebException e)
                {
                    Log.Error("LoginActivity", e.ToString());
                    LogUtil.SaveLogInfoToFile(e.ToString());
                    context.handler.SendEmptyMessage(Login.NETERROR);
                }
                catch (System.Exception e1)
                {
                    Log.Error("LoginActivity", e1.ToString());
                    LogUtil.SaveLogInfoToFile(e1.ToString());
                    context.handler.SendEmptyMessage(Login.ERROR);
                }
            }

        }
        private class LoginHandler : Handler
        {
            Login context;
            public LoginHandler(Login context)
            {
                this.context = context;
            }
            public override void HandleMessage(Message msg)
            {
                //关闭加载提示
                context.loadingDialog.dismissDialog();
                switch (msg.What)
                {
                    case Login.RETURN:
                        if (context.user.LoginResult != null)
                        {
                            if (context.user.LoginResult.Code == 0)
                            {
                                //登录成功
                                context.loginSuccess();
                            }
                            else
                            {
                                Toast.MakeText(context, context.user.LoginResult.Msg, ToastLength.Short).Show();
                            }
                        }
                        else
                        {
                            Toast.MakeText(context, Constant.LOGINFAIELD, ToastLength.Short).Show();
                        }
                        break;
                    case Login.NETERROR:
                        Toast.MakeText(context, Constant.NETNOTAVIABLE, ToastLength.Short).Show();
                        break;
                    case Login.ERROR:
                        Toast.MakeText(context, Constant.UNKNOWNEXCEPTION, ToastLength.Short).Show();
                        break;
                    default:
                        break;
                }
            }
        }
        public override bool OnKeyDown(Keycode keyCode, KeyEvent e)
        {
            if (keyCode == Keycode.Back)
            {
                if (loadingDialog != null && loadingDialog.isShowing())
                {
                    loadingDialog.dismissDialog();
                    if (!loginThread.IsInterrupted)
                    {
                        loginThread.Interrupt();
                    }
                }
            }
            return base.OnKeyDown(keyCode, e);
        }
        protected void btnlogin()
        {
            //先判断是否有网络
            if (CommonUtils.isNetworkAvailable(this))
            {
                user = new LoginUser();
                user.UserName = login_edit_account.Text.Trim();
                user.Password = login_edit_pwd.Text.Trim();
                user.IMEI = CommonUtils.getImei(this);
                loadingDialog = new LoadingDialog(this,Constant.LOGINING);
                loadingDialog.showDialog();
                loginThread = new LoginThread();
                loginThread.context = this;
                loginThread.Start();
            }
            else
            {
                Toast.MakeText(this, Constant.NETNOTAVIABLE, ToastLength.Short).Show();
            }
        }
        protected void loginSuccess()
        {
            //登录返回
            PsApplication.getInstance().setLoginUser(user);
            //保存登录用户至SharedPreference中
            mSharedPreference.Edit().PutString(Constant.PS_UNAME, user.UserName).PutString(Constant.PS_UTAG, user.LoginResult.Utag).PutString(Constant.PS_PWD, user.Password).Commit();
          
            Intent mainIntent = new Intent();
            mainIntent.SetClass(this, typeof(Main));
            StartActivity(mainIntent);
            Finish();
        }
       
    }
}