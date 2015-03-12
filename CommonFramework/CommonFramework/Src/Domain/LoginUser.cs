#region �汾˵��
/*****************************************************************************
 * 
 * ��    Ŀ : 
 * ��    �� : ���ƺ 
 * ����ʱ�� : 2014/8/12 11:16:14 
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
using PengeSoft.Service.Auther;

namespace CommonFramework.Src.Domain
{
    /// <summary>
    /// LoginUser ��ժҪ˵����
    /// </summary>
    public class LoginUser : DataPacket
    {
        #region ˽���ֶ�

        private string _UserName;      // 
        private string _Password;      // 
        private string _IMEI;      // 
        private LoginResult _LoginResult;      // 

        #endregion


        #region ���Զ���

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



        #region ���ع�������

        /// <summary>
        /// ����������ݡ�
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
        /// ��ָ���ڵ����л��������ݶ���
        /// </summary>
        /// <param name="node">�������л��� XmlNode �ڵ㡣</param>
        public override void XMLEncode(System.Xml.XmlNode node)
        {
            base.XMLEncode (node);

            WriteXMLValue(node, "UserName", _UserName);
            WriteXMLValue(node, "Password", _Password);
            WriteXMLValue(node, "IMEI", _IMEI);
            WriteXMLValue(node, "LoginResult", _LoginResult);
        }

        /// <summary>
        /// ��ָ���ڵ㷴���л��������ݶ���
        /// </summary>
        /// <param name="node">���ڷ����л��� XmlNode �ڵ㡣</param>
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
        /// �������ݶ���
        /// </summary>
        /// <param name="sou">Դ����,���DataPacket�̳�</param>
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
