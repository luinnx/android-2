#region �汾˵��
/*****************************************************************************
 * 
 * ��    Ŀ : 
 * ��    �� : ���� 
 * ����ʱ�� : 2014/8/1 21:57:52 
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
    /// FuncEntry ������ڵ�ժҪ˵����
    /// </summary>
    public class FuncEntry:DataPacket
    {
        #region ˽���ֶ�
        
        private string   _Title    ;      // ��������
        private string   _Url      ;      // ���ܵ�ַ
        private string   _IconUrl  ;      // ͼ��
        private string   _StateUrl ;      // ״̬ͼ��
        private int      _Option   ;      // ѡ��

        #endregion


        #region ���Զ���

        /// <summary>
        /// �������� 
        /// </summary>
        [XmlAttrib]
        public string   Title     { get { return _Title    ;} set { _Title     = value; } }
        /// <summary>
        /// ���ܵ�ַ 
        /// </summary>
        [XmlAttrib]
        public string   Url       { get { return _Url      ;} set { _Url       = value; } }
        /// <summary>
        /// ͼ�� 
        /// </summary>
        [XmlAttrib]
        public string   IconUrl   { get { return _IconUrl  ;} set { _IconUrl   = value; } }
        /// <summary>
        /// ״̬ͼ�� 
        /// </summary>
        [XmlAttrib]
        public string   StateUrl  { get { return _StateUrl ;} set { _StateUrl  = value; } }
        /// <summary>
        /// ѡ�� 
        /// </summary>
        [XmlAttrib]
        public int      Option    { get { return _Option   ;} set { _Option    = value; } }

        #endregion
        
  

        #region ���ع�������

        /// <summary>
        /// ����������ݡ�
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
        /// ��ָ���ڵ����л��������ݶ���
        /// </summary>
        /// <param name="node">�������л��� XmlNode �ڵ㡣</param>
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
        /// ��ָ���ڵ㷴���л��������ݶ���
        /// </summary>
        /// <param name="node">���ڷ����л��� XmlNode �ڵ㡣</param>
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
        /// �������ݶ���
        /// </summary>
        /// <param name="sou">Դ����,���DataPacket�̳�</param>
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

