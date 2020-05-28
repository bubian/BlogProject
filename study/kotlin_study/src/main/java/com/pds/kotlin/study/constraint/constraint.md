### 注意
- constraintlayout和coordinatorlayout
  很像，但是有不同的功能
  1. ConstraintLayout
  
     主要目标是提供一种方便的方法来创建具有多个子元素的平面布局
     
  2. CoordinatorLayout
  
     旨在作为用于管理行为的活动的顶层布局,实现联动需要用这个类

### CoordinatorLayout

##### 其它类
- [Guideline](https://developer.android.google.cn/reference/android/support/constraint/Guideline.html)
表示ConstraintLayout的Guideline辅助对象的实用工具类。辅助对象不会显示在设备上（它们标记为View.GONE），仅用于布局目的。它们仅在ConstraintLayout中工作。
- [Barrier](https://constraintlayout.com/basics/barriers.html)它跟 Guideline  一样属于Virtual Helper objects，在运行时的界面上看不到。Barrier 和 Guideline 的区别在于它是由多个 view 的大小决定的
##### 文章

- [Handling Scrolls with CoordinatorLayout](https://guides.codepath.com/android/Handling-Scrolls-with-CoordinatorLayout)


##### 库
- Bottom Sheet
  [AndroidSlidingUpPanel](https://github.com/soarcn/BottomSheet) 
  [Flipboard/bottomsheet](https://github.com/Flipboard/bottomsheet) 
  [ThreePhasesBottomSheet](https://github.com/AndroidDeveloperLB/ThreePhasesBottomSheet) 
  [Foursquare BottomSheet Tutorial ](http://android.amberfog.com/?p=915) 