#region 版本说明
/*****************************************************************************
 * 
 * 项    目 : 
 * 作    者 : 张鹏 
 * 创建时间 : 2014/8/1 21:57:39 
 *
 * Copyright (C) 2008 - 鹏业软件公司
 * 
 *****************************************************************************/
#endregion

using System;
using System.Collections;
using System.Text;
using PengeSoft.Data;
using PengeSoft.WorkZoneData;
using PengeSoft.db;

namespace PengeSoft.Mob
{
    /// <summary>
    /// AppEnvironment 应用环境的摘要说明。
    /// </summary>
    public class AppEnvironment:DataPacket
    {
        #region 私有字段
        
        private string    _Title     ;      // 标题
        private string    _AppId     ;      // 标识
        private string    _SplashImg ;      // Splash页
        private AppPage   _MainPage  ;      // 首页定义

        #endregion

        #region 构造函数

        /// <summary>
        /// 初始化新实例 
        /// </summary>
        public AppEnvironment()
        {
            _MainPage   = new AppPage();
        }

        #endregion

        #region 属性定义

        /// <summary>
        /// 标题 
        /// </summary>
        [XmlAttrib]
        public string    Title      { get { return _Title     ;} set { _Title      = value; } }
        /// <summary>
        /// 标识 
        /// </summary>
        [XmlAttrib]
        public string    AppId      { get { return _AppId     ;} set { _AppId      = value; } }
        /// <summary>
        /// Splash页 
        /// </summary>
        public string    SplashImg  { get { return _SplashImg ;} set { _SplashImg  = value; } }
        /// <summary>
        /// 首页定义 
        /// </summary>
        public AppPage   MainPage   { get { return _MainPage  ;} set { _MainPage   = value; } }

        #endregion
        
  

        #region 重载公共函数

        /// <summary>
        /// 清除所有数据。
        /// </summary>
        public override void Clear()
        {
            base.Clear ();

            _Title      = null;
            _AppId      = null;
            _SplashImg  = null;
            _MainPage  .Clear();
        }
        

#if SILVERLIGHT
#else
        /// <summary>
        /// 用指定节点序列化整个数据对象。
        /// </summary>
        /// <param name="node">用于序列化的 XmlNode 节点。</param>
        public override void XMLEncode(System.Xml.XmlNode node)
        {
            base.XMLEncode (node);

            WriteXmlAttrValue(node, "Title", _Title);
            WriteXmlAttrValue(node, "AppId", _AppId);
            WriteXMLValue(node, "SplashImg", _SplashImg);
            WriteXMLValue(node, "MainPage", _MainPage);
        }

        /// <summary>
        /// 用指定节点反序列化整个数据对象。
        /// </summary>
        /// <param name="node">用于反序列化的 XmlNode 节点。</param>
        public override void XMLDecode(System.Xml.XmlNode node)
        {
            base.XMLDecode (node);

            ReadXmlAttrValue(node, "Title", ref _Title);
            ReadXmlAttrValue(node, "AppId", ref _AppId);
            ReadXMLValue(node, "SplashImg", ref _SplashImg);
            ReadXMLValue(node, "MainPage", _MainPage);
        }
#endif

        /// <summary>
        /// 复制数据对象
        /// </summary>
        /// <param name="sou">源对象,需从DataPacket继承</param>
        public override void AssignFrom(DataPacket sou)
        {
            base.AssignFrom (sou);

            AppEnvironment s = sou as AppEnvironment;
            if (s != null)
            {
                _Title      = s._Title     ;
                _AppId      = s._AppId     ;
                _SplashImg  = s._SplashImg ;
                _MainPage  .AssignFrom(s._MainPage  );
            }
        }

        #endregion
    }
}

