#region �汾˵��
/*****************************************************************************
 * 
 * ��    Ŀ : 
 * ��    �� : ���� 
 * ����ʱ�� : 2014/8/1 21:57:39 
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
    /// AppEnvironment Ӧ�û�����ժҪ˵����
    /// </summary>
    public class AppEnvironment:DataPacket
    {
        #region ˽���ֶ�
        
        private string    _Title     ;      // ����
        private string    _AppId     ;      // ��ʶ
        private string    _SplashImg ;      // Splashҳ
        private AppPage   _MainPage  ;      // ��ҳ����

        #endregion

        #region ���캯��

        /// <summary>
        /// ��ʼ����ʵ�� 
        /// </summary>
        public AppEnvironment()
        {
            _MainPage   = new AppPage();
        }

        #endregion

        #region ���Զ���

        /// <summary>
        /// ���� 
        /// </summary>
        [XmlAttrib]
        public string    Title      { get { return _Title     ;} set { _Title      = value; } }
        /// <summary>
        /// ��ʶ 
        /// </summary>
        [XmlAttrib]
        public string    AppId      { get { return _AppId     ;} set { _AppId      = value; } }
        /// <summary>
        /// Splashҳ 
        /// </summary>
        public string    SplashImg  { get { return _SplashImg ;} set { _SplashImg  = value; } }
        /// <summary>
        /// ��ҳ���� 
        /// </summary>
        public AppPage   MainPage   { get { return _MainPage  ;} set { _MainPage   = value; } }

        #endregion
        
  

        #region ���ع�������

        /// <summary>
        /// ����������ݡ�
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
        /// ��ָ���ڵ����л��������ݶ���
        /// </summary>
        /// <param name="node">�������л��� XmlNode �ڵ㡣</param>
        public override void XMLEncode(System.Xml.XmlNode node)
        {
            base.XMLEncode (node);

            WriteXmlAttrValue(node, "Title", _Title);
            WriteXmlAttrValue(node, "AppId", _AppId);
            WriteXMLValue(node, "SplashImg", _SplashImg);
            WriteXMLValue(node, "MainPage", _MainPage);
        }

        /// <summary>
        /// ��ָ���ڵ㷴���л��������ݶ���
        /// </summary>
        /// <param name="node">���ڷ����л��� XmlNode �ڵ㡣</param>
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
        /// �������ݶ���
        /// </summary>
        /// <param name="sou">Դ����,���DataPacket�̳�</param>
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

