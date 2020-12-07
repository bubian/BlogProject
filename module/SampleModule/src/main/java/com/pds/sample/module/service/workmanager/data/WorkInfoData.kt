package com.pds.sample.module.service.workmanager.data

import androidx.lifecycle.Lifecycle
import androidx.work.Constraints
import androidx.work.WorkInfo

/**
 * @author: pengdaosong
 * CreateTime:  2019-10-24 10:28
 * Email：pengdaosong@medlinker.com
 * Description:
 */

data class WorkInfoData(var name: String = "NONE"){

    // 当前生命周期状态
    var lifecycle : Lifecycle.State = Lifecycle.State.INITIALIZED
    // 当前工作信息
    var workInfo : WorkInfo? = null
    //
    var constraint: Constraints? = null

    override fun toString(): String {
        return "任务类型：${name}<br/><br/>" +
                "生命周期：$lifecycle<br/><br/>" +
                "work info：<br/><br/>" +
                "\t\t<font color=\"#ff0000\">id = </font><font color=\"#007eff\">${workInfo?.id}</font><br/><br/>"+
                "\t\t<font color=\"#ff0000\">state = </font><font color=\"#007eff\">${workInfo?.state}</font><br/><br/>"+
                "\t\t<font color=\"#ff0000\">outputData = </font><font color=\"#007eff\">${workInfo?.outputData}</font><br/><br/>"+
                "\t\t<font color=\"#ff0000\">tags = </font><font color=\"#007eff\">${workInfo?.tags}</font><br/>"+
                "\t\t<font color=\"#ff0000\">progress = </font><font color=\"#007eff\">${workInfo?.progress}</font><br/><br/>"+
                "\t\t<font color=\"#ff0000\">runAttemptCount = </font><font color=\"#007eff\">${workInfo?.runAttemptCount}</font><br/><br/>"

    }

}