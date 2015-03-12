#region 版本说明
/*****************************************************************************
 * 
 * 项    目 : 
 * 作    者 : 李金坪 
 * 创建时间 : 2014/8/12 11:16:14 
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
using PengeSoft.Service.Auther;

namespace CommonFramework.Src.Domain
{
    /// <summary>
    /// LoginUser 的摘要说明。
    /// </summary>
    public class LoginUser : DataPacket
    {
        #region 私有字段

        private string _UserName;      // 
        private string _Password;      // 
        private string _IMEI;      // 
        private LoginResult _LoginResult;      // 

        #endregion


        #region 属性定义

        /// <summary>
        ///  
        /// </summary>
        public string UserName { get { return _UserName; } set { _UserName = value; } }
        /// <summary>
        ///  
        /// </summary>
        public string Password { get { return _Password; } set { _Password = value; } }
        /// <summary>
        ///  
        /// </summary>
        public string IMEI { get { return _IMEI; } set { _IMEI = value; } }
        /// <summary>
        ///  
        /// </summary>
        public LoginResult LoginResult { get { return _LoginResult; } set { _LoginResult = value; } }

        #endregion



        #region 重载公共函数

        /// <summary>
        /// 清除所有数据。
        /// </summary>
        public override void Clear()
        {
            base.Clear();

            _UserName = null;
            _Password = null;
            _IMEI = null;
            _LoginResult = null;
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

            WriteXMLValue(node, "UserName", _UserName);
            WriteXMLValue(node, "Password", _Password);
            WriteXMLValue(node, "IMEI", _IMEI);
            WriteXMLValue(node, "LoginResult", _LoginResult);
        }

        /// <summary>
        /// 用指定节点反序列化整个数据对象。
        /// </summary>
        /// <param name="node">用于反序列化的 XmlNode 节点。</param>
        public override void XMLDecode(System.Xml.XmlNode node)
        {
            base.XMLDecode (node);

            ReadXMLValue(node, "UserName", ref _UserName);
            ReadXMLValue(node, "Password", ref _Password);
            ReadXMLValue(node, "IMEI", ref _IMEI);
            ReadXMLValue(node, "LoginResult", ref _LoginResult);
        }
#endif

        /// <summary>
        /// 复制数据对象
        /// </summary>
        /// <param name="sou">源对象,需从DataPacket继承</param>
        public override void AssignFrom(DataPacket sou)
        {
            base.AssignFrom(sou);

            LoginUser s = sou as LoginUser;
            if (s != null)
            {
                _UserName = s._UserName;
                _Password = s._Password;
                _IMEI = s._IMEI;
                _LoginResult = s._LoginResult;
            }
        }

        #endregion
    }
}
