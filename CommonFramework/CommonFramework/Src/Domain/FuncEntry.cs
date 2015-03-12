#region 版本说明
/*****************************************************************************
 * 
 * 项    目 : 
 * 作    者 : 张鹏 
 * 创建时间 : 2014/8/1 21:57:52 
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
    /// FuncEntry 功能入口的摘要说明。
    /// </summary>
    public class FuncEntry:DataPacket
    {
        #region 私有字段
        
        private string   _Title    ;      // 功能名称
        private string   _Url      ;      // 功能地址
        private string   _IconUrl  ;      // 图标
        private string   _StateUrl ;      // 状态图标
        private int      _Option   ;      // 选项

        #endregion


        #region 属性定义

        /// <summary>
        /// 功能名称 
        /// </summary>
        [XmlAttrib]
        public string   Title     { get { return _Title    ;} set { _Title     = value; } }
        /// <summary>
        /// 功能地址 
        /// </summary>
        [XmlAttrib]
        public string   Url       { get { return _Url      ;} set { _Url       = value; } }
        /// <summary>
        /// 图标 
        /// </summary>
        [XmlAttrib]
        public string   IconUrl   { get { return _IconUrl  ;} set { _IconUrl   = value; } }
        /// <summary>
        /// 状态图标 
        /// </summary>
        [XmlAttrib]
        public string   StateUrl  { get { return _StateUrl ;} set { _StateUrl  = value; } }
        /// <summary>
        /// 选项 
        /// </summary>
        [XmlAttrib]
        public int      Option    { get { return _Option   ;} set { _Option    = value; } }

        #endregion
        
  

        #region 重载公共函数

        /// <summary>
        /// 清除所有数据。
        /// </summary>
        public override void Clear()
        {
            base.Clear ();

            _Title     = null;
            _Url       = null;
            _IconUrl   = null;
            _StateUrl  = null;
            _Option    = 0;
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
            WriteXmlAttrValue(node, "Url", _Url);
            WriteXmlAttrValue(node, "IconUrl", _IconUrl);
            WriteXmlAttrValue(node, "StateUrl", _StateUrl);
            WriteXmlAttrValue(node, "Option", _Option);
        }

        /// <summary>
        /// 用指定节点反序列化整个数据对象。
        /// </summary>
        /// <param name="node">用于反序列化的 XmlNode 节点。</param>
        public override void XMLDecode(System.Xml.XmlNode node)
        {
            base.XMLDecode (node);

            ReadXmlAttrValue(node, "Title", ref _Title);
            ReadXmlAttrValue(node, "Url", ref _Url);
            ReadXmlAttrValue(node, "IconUrl", ref _IconUrl);
            ReadXmlAttrValue(node, "StateUrl", ref _StateUrl);
            ReadXmlAttrValue(node, "Option", ref _Option);
        }
#endif

        /// <summary>
        /// 复制数据对象
        /// </summary>
        /// <param name="sou">源对象,需从DataPacket继承</param>
        public override void AssignFrom(DataPacket sou)
        {
            base.AssignFrom (sou);

            FuncEntry s = sou as FuncEntry;
            if (s != null)
            {
                _Title     = s._Title    ;
                _Url       = s._Url      ;
                _IconUrl   = s._IconUrl  ;
                _StateUrl  = s._StateUrl ;
                _Option    = s._Option   ;
            }
        }

        #endregion
    }
}

