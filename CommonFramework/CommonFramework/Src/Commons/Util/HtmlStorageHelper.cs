using Android.Content;
using Android.Database;
using Android.Database.Sqlite;
using com.androidquery;
using com.androidquery.callback;
using Java.IO;
using Java.Util;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace CommonFramework.Src.Commons.Util
{
    class HtmlStorageHelper
    {
        private string URL = "http://eduproject.sinaapp.com/fetchurl.php/getcontent/";
        private AQuery aq;
        private SQLiteDatabase mDB;
        private string mDownloadPath;

        public HtmlStorageHelper(Context context)
        {

            aq = new com.androidquery.AQuery(context, new object());
            mDB = context.OpenOrCreateDatabase("data.db", FileCreationMode.WorldWriteable, null);
            mDB.ExecSQL("create table if not exists download_html(_id INTEGER PRIMARY KEY AUTOINCREMENT, content_id TEXT NOT NULL, title TEXT NOT NULL)");

            mDownloadPath = "/sdcard/Commonframework/";
            File dir_file = new File(mDownloadPath);
            if (!dir_file.Exists())
                dir_file.Mkdir();
        }
        public void saveHtml(string id, string title) {

  aq.ajax(URL+id, typeof(string),);
 }
        AjaxCallback ajxcall= new AjaxCallback() {
   
   public override void callback(String url, String html, AjaxStatus status) {
    File dir_file = new File(mDownloadPath + id);
    if(!dir_file.exists())
     dir_file.mkdir();

    Pattern pattern = Pattern.compile("(?<=src=\")[^\"]+(?=\")");
    Matcher matcher = pattern.matcher(html);
    StringBuffer sb = new StringBuffer(); 
    while(matcher.find()){
     downloadPic(id, matcher.group(0));
     matcher.appendReplacement(sb, formatPath(matcher.group(0))); 
    }
    matcher.appendTail(sb);
    html = sb.toString();

    writeHtml(id, title, html);
   }
  }

        public Boolean isHtmlSaved(String id)
        {
            File file = new File(mDownloadPath + id);
            if (file.Exists())
            {
                file = new File(mDownloadPath + id + "/index.html");
                if (file.Exists())
                    return true;
            }
            deleteHtml(id);
            return false;
        }

        public String getTitle(String id)
        {
            ICursor c = mDB.RawQuery("select * from download_html where content_id=?", new String[] { id });
            if (c.Count == 0)
                return null;

            c.MoveToFirst();
            int index1 = c.GetColumnIndex("title");
            return c.GetString(index1);
        }

        public LinkedList<Subscribe> getHtmlList()
        {
            ICursor c = mDB.RawQuery("select * from download_html", null);
            LinkedList<Subscribe> list = new LinkedList<Subscribe>();


            if (c.Count != 0)
            {
                c.MoveToFirst();
                int index1 = c.GetColumnIndex("content_id");
                int index2 = c.GetColumnIndex("title");

                while (!c.IsAfterLast)
                {
                    String id = c.GetString(index1);
                    if (isHtmlSaved(id))
                    {
                        Subscribe sub = new Subscribe();
                        sub.id = id;
                        sub.title = c.GetString(index2);
                        list.AddLast(sub);
                    }

                    c.MoveToNext();
                }
            }

            return list;
        }

        public void deleteHtml(String id)
        {
            mDB.Delete("download_html", "content_id=?", new String[] { id });
            File dir_file = new File(mDownloadPath + id);
            deleteFile(dir_file);
        }
        private void deleteFile(File file)
        {
            if (file.Exists())
            { // 判断文件是否存在
                if (file.IsFile)
                { // 判断是否是文件
                    file.Delete(); // delete()方法 你应该知道 是删除的意思;
                }
                else if (file.IsDirectory)
                { // 否则如果它是一个目录
                    File[] files = file.ListFiles(); // 声明目录下所有的文件 files[];
                    for (int i = 0; i < files.Length; i++)
                    { // 遍历目录下所有的文件
                        this.deleteFile(files[i]); // 把每个文件 用这个方法进行迭代
                    }
                }
                file.Delete();
            }
            else
            {
                //
            }
        }

        private String formatPath(String path)
        {
            if (path != null && path.Length > 0)
            {
                path = path.Replace("\\", "_");
                path = path.Replace("/", "_");
                path = path.Replace(":", "_");
                path = path.Replace("*", "_");
                path = path.Replace("?", "_");
                path = path.Replace("\"", "_");
                path = path.Replace("<", "_");
                path = path.Replace("|", "_");
                path = path.Replace(">", "_");
            }
            return path;
        }
    }
}
