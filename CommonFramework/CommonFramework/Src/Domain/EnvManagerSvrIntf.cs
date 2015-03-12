#region 版本说明
/*****************************************************************************
 * 
 * 项    目 : 
 * 作    者 : 张鹏 
 * 创建时间 : 2014/8/1 20:53:13 
 *
 * Copyright (C) 2008 - 鹏业软件公司
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
    /// IEnvManagerSvr 接口定义。    
    /// </summary>
    [PublishName("EnvManager")]
    public interface IEnvManagerSvr : IApplication
    {
        /// <summary>
        /// 取用户环境   
        /// </summary>
        /// <param name="utag">用户登录标识</param>
        /// <param name="option">选项</param>
        [PublishMethod]
        AppEnvironment GetUserEnv(string utag, int option);

    }
}
