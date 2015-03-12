#region �汾˵��
/*****************************************************************************
 * 
 * ��    Ŀ : 
 * ��    �� : ���� 
 * ����ʱ�� : 2014/8/1 21:57:44 
 *
 * Copyright (C) 2008 - ��ҵ�����˾
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
    /// AppPage ҳ�涨���ժҪ˵����
    /// </summary>
    public class AppPage:DataPacket
    {
        #region ˽���ֶ�
        
        private string    _Title    ;      // ����
        private string    _HeadUrl  ;      // ҳ��Head
        private int _HeadHeight;
        private ObjList   _FuncDefs ;      // ���ܶ��� ������ ObjList ��������,��ָ��ItemType

        #endregion

        #region ���캯��

        /// <summary>
        /// ��ʼ����ʵ�� 
        /// </summary>
        public AppPage()
        {
            _FuncDefs  = new ObjList();
            _FuncDefs .ItemType = typeof(FuncEntry);
        }

        #endregion

        #region ���Զ���

        /// <summary>
        /// ���� 
        /// </summary>
        [XmlAttrib]
        public string    Title     { get { return _Title    ;} set { _Title     = value; } }
        /// <summary>
        /// ҳ��Head 
        /// </summary>
        public string    HeadUrl   { get { return _HeadUrl  ;} set { _HeadUrl   = value; } }
        public int HeadHeight { get { return _HeadHeight; } set { _HeadHeight = value; } }
        /// <summary>
        /// ���ܶ��� 
        /// </summary>
        public ObjList   FuncDefs  { get { return _FuncDefs ;} set { _FuncDefs  = value; } }

        #endregion
        
  

        #region ���ع�������

        /// <summary>
        /// ����������ݡ�
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
        /// ��ָ���ڵ����л��������ݶ���
        /// </summary>
        /// <param name="node">�������л��� XmlNode �ڵ㡣</param>
        public override void XMLEncode(System.Xml.XmlNode node)
        {
            base.XMLEncode (node);

            WriteXmlAttrValue(node, "Title", _Title);
            WriteXMLValue(node, "HeadUrl", _HeadUrl);
            WriteXMLValue(node, "HeadUrl", _HeadHeight);
            WriteXMLValue(node, "FuncDefs", _FuncDefs);
        }

        /// <summary>
        /// ��ָ���ڵ㷴���л��������ݶ���
        /// </summary>
        /// <param name="node">���ڷ����л��� XmlNode �ڵ㡣</param>
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
        /// �������ݶ���
        /// </summary>
        /// <param name="sou">Դ����,���DataPacket�̳�</param>
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

