using Android.App;
using CommonFramework.Src.Domain;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace CommonFramework.Src.Commons.Util
{
    class PsApplication : Application
    {
        private static PsApplication mInstance = null;
        private LoginUser loginUser;

        public override void OnCreate()
        {
            base.OnCreate();
            mInstance = this;
        }
        public static PsApplication getInstance()
        {
            if (mInstance == null)
            {
                mInstance = new PsApplication();
            }
            return mInstance;
        }

        public LoginUser getLoginUser()
        {
            return loginUser;
        }

        public void setLoginUser(LoginUser User)
        {
            loginUser = User;
        }
    }
}
