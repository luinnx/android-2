#region �汾˵��
/*****************************************************************************
 * 
 * ��    Ŀ : 
 * ��    �� : ���� 
 * ����ʱ�� : 2014/8/1 20:53:13 
 *
 * Copyright (C) 2008 - ��ҵ�����˾
 * 
 *****************************************************************************/
#endregion

using System;
using System.Collections;
using System.Text;
using PengeSoft.Data;
using PengeSoft.Enterprise.Appication;
using PengeSoft.WorkZoneData;
using PengeSoft.Service;

namespace PengeSoft.Mob 
{
    /// <summary>
    /// IEnvManagerSvr �ӿڶ��塣    
    /// </summary>
    [PublishName("EnvManager")]
    public interface IEnvManagerSvr : IApplication
    {
        /// <summary>
        /// ȡ�û�����   
        /// </summary>
        /// <param name="utag">�û���¼��ʶ</param>
        /// <param name="option">ѡ��</param>
        [PublishMethod]
        AppEnvironment GetUserEnv(string utag, int option);

    }
}
