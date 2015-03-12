#region 版本说明
/*****************************************************************************
 * 
 * 项    目 : 
 * 作    者 : 李金坪 
 * 创建时间 : 2014/8/12 16:31:07 
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

namespace CommonFramework.Src.Commons.Util
{
    /// <summary>
    /// Subscribe 的摘要说明。
    /// </summary>
    public class Subscribe : DataPacket
    {
        #region 私有字段

        private string _id;      // 
        private string _title;      // 

        #endregion


        #region 属性定义

        /// <summary>
        ///  
        /// </summary>
        public string id { get { return _id; } set { _id = value; } }
        /// <summary>
        ///  
        /// </summary>
        public string title { get { return _title; } set { _title = value; } }

        #endregion



        #region 重载公共函数

        /// <summary>
        /// 清除所有数据。
        /// </summary>
        public override void Clear()
        {
            base.Clear();

            _id = null;
            _title = null;
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

            WriteXMLValue(node, "id", _id);
            WriteXMLValue(node, "title", _title);
        }

        /// <summary>
        /// 用指定节点反序列化整个数据对象。
        /// </summary>
        /// <param name="node">用于反序列化的 XmlNode 节点。</param>
        public override void XMLDecode(System.Xml.XmlNode node)
        {
            base.XMLDecode (node);

            ReadXMLValue(node, "id", ref _id);
            ReadXMLValue(node, "title", ref _title);
        }
#endif

        /// <summary>
        /// 复制数据对象
        /// </summary>
        /// <param name="sou">源对象,需从DataPacket继承</param>
        public override void AssignFrom(DataPacket sou)
        {
            base.AssignFrom(sou);

            Subscribe s = sou as Subscribe;
            if (s != null)
            {
                _id = s._id;
                _title = s._title;
            }
        }

        #endregion
    }
}
