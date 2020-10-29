/*
**        DroidPlugin Project
**
** Copyright(c) 2015 Andy Zhang <zhangyong232@gmail.com>
**
** This file is part of DroidPlugin.
**
** DroidPlugin is free software: you can redistribute it and/or
** modify it under the terms of the GNU Lesser General Public
** License as published by the Free Software Foundation, either
** version 3 of the License, or (at your option) any later version.
**
** DroidPlugin is distributed in the hope that it will be useful,
** but WITHOUT ANY WARRANTY; without even the implied warranty of
** MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
** Lesser General Public License for more details.
**
** You should have received a copy of the GNU Lesser General Public
** License along with DroidPlugin.  If not, see <http://www.gnu.org/licenses/lgpl.txt>
**
**/

package com.pds.plugin.core;

public class Env {
    public static final String ACTION_INSTALL_SHORTCUT = "com.android.launcher.action.INSTALL_SHORTCUT";
    public static final String ACTION_UNINSTALL_SHORTCUT = "com.android.launcher.action.UNINSTALL_SHORTCUT";

    public static final String EXTRA_TARGET_INTENT = "oldIntent";
    public static final String EXTRA_TARGET_INTENT_URI = "com.pds.plugin.OldIntent.Uri";
    public static final String EXTRA_TARGET_INFO = "com.pds.plugin.OldInfo";
    public static final String EXTRA_STUB_INFO = "com.pds.plugin.NewInfo";
    public static final String EXTRA_TARGET_AUTHORITY = "TargetAuthority";
    public static final String EXTRA_TYPE = "com.pds.plugin.EXTRA_TYPE";
    public static final String EXTRA_ACTION = "com.pds.plugin.EXTRA_ACTION";

}
