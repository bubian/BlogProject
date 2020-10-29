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

package com.pds.plugin.stub;

import android.app.Activity;

public abstract class ActivityMode extends Activity {

    //    Standard模式是Android的默认启动模式Activity可以有多个实例，
    // 每次启动Activity，无论任务栈中是否已经有这个Activity的实例，
    //    系统都会创建一个新的Activity实例，以下是实验验证。
    private static class StandardStub extends ActivityMode {
    }

    /**
     * SingleTop模式和standard模式非常相似，主要区别就是当一个singleTop模式的Activity已经位于任务栈的栈顶
     * 再去启动它时，不会再创建新的实例,如果不位于栈顶，就会创建新的实例，
     * 现在把配置文件中FirstActivity的启动模式改为SingleTop，
     * 我们的应用只有一个Activity，FirstActivity自然处于任务栈的栈顶。
     */
    private static class SingleTopStub extends ActivityMode {
    }

    /**
     *   SingleTask模式的Activity在同一个Task内只有一个实例，如果Activity已经位于栈顶，
     *   系统不会创建新的Activity实例，和singleTop模式一样。但Activity已经存在但不位于栈顶时，
     *   系统就会把该Activity移到栈顶，并把它上面的activity出栈。修改上面的程序，新建一个SecondActivity
     *   ,将FirstActivity设置为singleTask启动模式，并让它启动SecondActivity，
     *   再让SecondActivity来启动FirstActivity。
     */
    private static class SingleTaskStub extends ActivityMode {
    }
    /**
     * singleInstance模式也是单例的，但和singleTask不同，singleTask只是任务栈内单例，
     * 系统里是可以有多个singleTask Activity实例的，而singleInstance Activity在整个系统里只有一个实例，
     * 启动一singleInstanceActivity时，系统会创建一个新的任务栈，并且这个任务栈只有他一个Activity。
     */
    private static class SingleInstanceStub extends ActivityMode {
    }
    //p1
    public static class P00{

        public static class SingleInstance00 extends SingleInstanceStub {
        }

        public static class SingleTask00 extends SingleTaskStub {
        }

        public static class SingleTop00 extends SingleTopStub {
        }

        public static class SingleInstance01 extends SingleInstanceStub {
        }

        public static class SingleTask01 extends SingleTaskStub {
        }

        public static class SingleTop01 extends SingleTopStub {
        }

        public static class SingleInstance02 extends SingleInstanceStub {
        }

        public static class SingleTask02 extends SingleTaskStub {
        }

        public static class SingleTop02 extends SingleTopStub {
        }

        public static class SingleInstance03 extends SingleInstanceStub {
        }

        public static class SingleTask03 extends SingleTaskStub {
        }

        public static class SingleTop03 extends SingleTopStub {
        }

        public static class Standard00 extends StandardStub {
        }
    }



    //p2
    public static class P01{

        public static class SingleInstance00 extends SingleInstanceStub {
        }

        public static class SingleTask00 extends SingleTaskStub {
        }

        public static class SingleTop00 extends SingleTopStub {
        }

        public static class SingleInstance01 extends SingleInstanceStub {
        }

        public static class SingleTask01 extends SingleTaskStub {
        }

        public static class SingleTop01 extends SingleTopStub {
        }

        public static class SingleInstance02 extends SingleInstanceStub {
        }

        public static class SingleTask02 extends SingleTaskStub {
        }

        public static class SingleTop02 extends SingleTopStub {
        }

        public static class SingleInstance03 extends SingleInstanceStub {
        }

        public static class SingleTask03 extends SingleTaskStub {
        }

        public static class SingleTop03 extends SingleTopStub {
        }

        public static class Standard00 extends StandardStub {
        }
    }

    //p3
    public static class P02{

        public static class SingleInstance00 extends SingleInstanceStub {
        }

        public static class SingleTask00 extends SingleTaskStub {
        }

        public static class SingleTop00 extends SingleTopStub {
        }

        public static class SingleInstance01 extends SingleInstanceStub {
        }

        public static class SingleTask01 extends SingleTaskStub {
        }

        public static class SingleTop01 extends SingleTopStub {
        }

        public static class SingleInstance02 extends SingleInstanceStub {
        }

        public static class SingleTask02 extends SingleTaskStub {
        }

        public static class SingleTop02 extends SingleTopStub {
        }

        public static class SingleInstance03 extends SingleInstanceStub {
        }

        public static class SingleTask03 extends SingleTaskStub {
        }

        public static class SingleTop03 extends SingleTopStub {
        }

        public static class Standard00 extends StandardStub {
        }
    }


    //p4
    public static class P03{

        public static class SingleInstance00 extends SingleInstanceStub {
        }

        public static class SingleTask00 extends SingleTaskStub {
        }

        public static class SingleTop00 extends SingleTopStub {
        }

        public static class SingleInstance01 extends SingleInstanceStub {
        }

        public static class SingleTask01 extends SingleTaskStub {
        }

        public static class SingleTop01 extends SingleTopStub {
        }

        public static class SingleInstance02 extends SingleInstanceStub {
        }

        public static class SingleTask02 extends SingleTaskStub {
        }

        public static class SingleTop02 extends SingleTopStub {
        }

        public static class SingleInstance03 extends SingleInstanceStub {
        }

        public static class SingleTask03 extends SingleTaskStub {
        }

        public static class SingleTop03 extends SingleTopStub {
        }

        public static class Standard00 extends StandardStub {
        }
    }
}
