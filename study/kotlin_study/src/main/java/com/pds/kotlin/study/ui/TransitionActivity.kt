package com.pds.kotlin.study.ui

import android.os.Build
import android.os.Bundle
import android.transition.ChangeBounds
import android.transition.Scene
import android.transition.Transition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.pds.kotlin.study.R
import kotlinx.android.synthetic.main.activity_transition.*

/**
 * @author: pengdaosong
 * CreateTime:  2020-05-26 16:51
 * Email：pengdaosong@medlinker.com
 * Description:
 */


// Transition动画
// Transition内部使用了属性动画实现，所以它可以认为是属性动画的封装。Transition两个核心概念为：场景（scenes）和变换（transitions），场景是UI当前状态，变换则定义了在不同场景之间动画变化的过程。
// 所以Transition主要负责两个方面的事，一是保存开始和结束场景的两种状态，二是在两种状态之间创建动画。由于场景记录了内部所有View的开始和结束状态，所以Transition动画更具连贯性。
// 谁执行动画呢？TransitionManager负责执行动画的任务。
class TransitionActivity : AppCompatActivity() {

    private var togger = true
    private lateinit var scene1 : Scene
    private lateinit var scene2 : Scene

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transition)
        view_container.setOnClickListener {

        }
        // initScene()
        // sceneRoot:根ViewGroup，内部包含多个场景viewGroup。
        // layoutId:view的布局文件资源id，代表一个场景。
        // context：上下文
        scene1 = Scene.getSceneForLayout(view_container,
            R.layout.scene_one, this)
        scene2 = Scene.getSceneForLayout(view_container,
            R.layout.scene_two, this)
    }

    // 在R.layout.scene_one，R.layout.scene_two对应转换的View的id名称是一样的，如果不一样这没有相关动画效果
    fun tran(view: View) {
        if (togger){
            // 继承Transiton类的效果
            //
            // ChangeBounds：检测view的位置边界创建移动和缩放动画
            // ChangeTransform：检测view的scale和rotation创建缩放和旋转动画
            // ChangeClipBounds：检测view的剪切区域的位置边界，和ChangeBounds类似。不过ChangeBounds针对的是view而ChangeClipBounds针对的是view的剪切区域(setClipBound(Rect rect) 中的rect)。如果没有设置则没有动画效果
            // ChangeImageTransform:检测ImageView的尺寸，位置以及ScaleType，并创建相应动画。
            // Fade,Slide,Explode:根据view的visibility的状态执行渐入渐出，滑动，分解动画。
            TransitionManager.go(scene1, ChangeBounds())
        }else{
            TransitionManager.go(scene2, ChangeBounds())
        }
        togger = !togger
    }

    // TransitionManager.go函数需要生成对应的Scene，beginDelayedTransiton则不需要，只需要填入sceneRoot和Transition就可以实现Transition动画。
    // 执行TransitionManager.beginDelayedTransition后，系统会保存一个当前视图树状态的场景，修改view的属性信息，在下一次绘制时，系统会自动对比之前保存的视图树，然后执行一步动画
    //重要提醒：如果想让beginDelayedTransition有效果，必须每次改变视图属性之后，重新调用beginDelayedTransition，或者改变之前调用beginDelayedTransition，这样才能够保存当前view的状态，否则存储的属性没有改变，不会有动画效果。

    // 这个函数里面没有Scene，那它何时执行动画呢，很简单当view的某些属性信息改变时，就会执行动画，上面介绍go函数时使用了ChangeTransform，ChangeBounds，ChangeClipBounds
    //，ChangeImageTransform，但感觉他们效果差不多，其实这些属性主要用于beginDelayedTransition这个函数，当对应的属性改变时，会自动触发Transition动画。

    fun tranBe(){
        val transition = ChangeBounds()
        transition.duration = 1000;
        TransitionManager.beginDelayedTransition(view_container,transition);
        if (togger){
            val layoutParams1 =  test.layoutParams
            layoutParams1.height =100
            layoutParams1.width =100
            test.layoutParams = layoutParams1
        }else{
            val layoutParams2 = test.layoutParams
            layoutParams2.height = 700
            layoutParams2.width = 700
            test.layoutParams = layoutParams2
        }
        togger = !togger;
    }

    // 可以利用Transition的addTarget（），removeTarget（），只对某些view做动画，或者不对某些view做动画。
    // 如果调用了addTarget则只对调用了这个函数的View做动画，其他View直接完成最终状态，如果调用了removeTarget则是对没有调用这个函数的其他view做动画。
    // 如果同时调用了两个函数，则调用removeTarget会从调用了addTarget中的view查找，然后剔除。

    private fun getChangeBounds() : Transition {
        val transition: Transition = ChangeBounds()
        transition.addTarget(R.id.image_view1)
        return transition
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun initScene(){
        val view1 = LayoutInflater.from(this).inflate(R.layout.scene_one, null)
        val view2 = LayoutInflater.from(this).inflate(R.layout.scene_two, null)
        scene1 = Scene(view_container, view1)
        scene2 = Scene(view_container, view1)
    }
}