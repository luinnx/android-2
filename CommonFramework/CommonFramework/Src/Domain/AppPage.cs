#region 版本说明
/*****************************************************************************
 * 
 * 项    目 : 
 * 作    者 : 张鹏 
 * 创建时间 : 2014/8/1 21:57:44 
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
    /// AppPage 页面定义的摘要说明。
    /// </summary>
    public class AppPage:DataPacket
    {
        #region 私有字段
        
        private string    _Title    ;      // 标题
        private string    _HeadUrl  ;      // 页面Head
        private int _HeadHeight;
        private ObjList   _FuncDefs ;      // 功能定义 必须用 ObjList 的派生类,或指定ItemType

        #endregion

        #region 构造函数

        /// <summary>
        /// 初始化新实例 
        /// </summary>
        public AppPage()
        {
            _FuncDefs  = new ObjList();
            _FuncDefs .ItemType = typeof(FuncEntry);
        }

        #endregion

        #region 属性定义

        /// <summary>
        /// 标题 
        /// </summary>
        [XmlAttrib]
        public string    Title     { get { return _Title    ;} set { _Title     = value; } }
        /// <summary>
        /// 页面Head 
        /// </summary>
        public string    HeadUrl   { get { return _HeadUrl  ;} set { _HeadUrl   = value; } }
        public int HeadHeight { get { return _HeadHeight; } set { _HeadHeight = value; } }
        /// <summary>
        /// 功能定义 
        /// </summary>
        public ObjList   FuncDefs  { get { return _FuncDefs ;} set { _FuncDefs  = value; } }

        #endregion
        
  

        #region 重载公共函数

        /// <summary>
        /// 清除所有数据。
        /// </summary>
        public override void Clear()
        {
            base.Clear ();

            _Title     = null;
            _HeadUrl   = null;
            _HeadHeight = 0;
            _FuncDefs .Clear();
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
            WriteXMLValue(node, "HeadUrl", _HeadUrl);
            WriteXMLValue(node, "HeadUrl", _HeadHeight);
            WriteXMLValue(node, "FuncDefs", _FuncDefs);
        }

        /// <summary>
        /// 用指定节点反序列化整个数据对象。
        /// </summary>
        /// <param name="node">用于反序列化的 XmlNode 节点。</param>
        public override void XMLDecode(System.Xml.XmlNode node)
        {
            base.XMLDecode (node);

            ReadXmlAttrValue(node, "Title", ref _Title);
            ReadXMLValue(node, "HeadUrl", ref _HeadUrl);
            ReadXMLValue(node, "HeadUrl", ref _HeadHeight);
            ReadXMLValue(node, "FuncDefs", _FuncDefs);
        }
#endif

        /// <summary>
        /// 复制数据对象
        /// </summary>
        /// <param name="sou">源对象,需从DataPacket继承</param>
        public override void AssignFrom(DataPacket sou)
        {
            base.AssignFrom (sou);

            AppPage s = sou as AppPage;
            if (s != null)
            {
                _Title     = s._Title    ;
                _HeadUrl   = s._HeadUrl  ;
                _HeadHeight = s._HeadHeight;
                _FuncDefs .AssignFrom(s._FuncDefs );
            }
        }

        #endregion
    }
}

