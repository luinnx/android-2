using Android.App;
using Android.Content;
using Android.Widget;
using System;
using System.Linq;
using System.Text;

namespace CommonFramework.Src.Commons.Wedget
{
    class LoadingDialog
    {
        Dialog loadingDialog;
        private Context context;
        private TextView msgTxT;
        private String msgStr;

        public LoadingDialog(Context context, string msg)
        {
            this.context = context;
            this.msgStr = msg;
            //初始化
            loadingDialog = new Dialog(context, Resource.Style.dialog);
            //设置布局文件
            loadingDialog.SetContentView(Resource.Layout.loadingdialog);
            //点击边框不关闭
            loadingDialog.SetCanceledOnTouchOutside(false);
            msgTxT = (TextView)loadingDialog.FindViewById(Resource.Id.loading_msg);
            msgTxT.Text = msgStr;
        }

        public void showDialog()
        {
            loadingDialog.Show();
        }

        public void dismissDialog()
        {
            loadingDialog.Dismiss();
        }

        public bool isShowing()
        {
            return loadingDialog.IsShowing;
        }
    }
}
