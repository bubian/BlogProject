package lib.pds.com.runtime;

import android.app.Fragment;
import android.util.Log;
import android.view.View;
import lib.pds.com.runtime.utils.MethodPrint;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

/**
 * @author: pengdaosong
 * CreateTime:  2019/3/18 4:21 PM
 * Email：pengdaosong@medlinker.com
 * Description:
 */
@Aspect
public class EventUpload {
    private static final String TAG = "EventUpload";
    @Before("execution(* android.app.Activity+.on**(..))")
    public void onActivityMethodBefore(JoinPoint joinPoint) throws Throwable {
        MethodPrint.enterMethod(joinPoint);
    }

    @Pointcut("execution(void onHiddenChanged(boolean)) && within(android.support.v4.app.Fragment) && target(fragment) && args(hidden)")
    public void onHiddenChanged(Fragment fragment, boolean hidden) {
        Log.e(TAG,"Aspect=======:"+ "fun = onHiddenChanged");
    }

    @Pointcut("execution(void setUserVisibleHint(..)) && within(android.support.v4.app.Fragment) && target(fragment) && args(visible)")
    public void setUserVisibleHint(Fragment fragment, boolean visible) {
        Log.e(TAG,"Aspect=======:"+ "fun = setUserVisibleHint");
    }

    @Pointcut("execution(void onResume()) && within(android.support.v4.app.Fragment) && target(fragment)")
    public void onResume(Fragment fragment) {
        Log.e(TAG,"Aspect=======:"+ "fun = frag onResume");
    }

    @Pointcut("execution(void onPause()) && within(android.support.v4.app.Fragment) && target(fragment)")
    public void onPause(Fragment fragment) {
        Log.e(TAG,"Aspect=======:"+ "fun = frag onPause");
    }

    /**
     * 实现 OnClickListener 接口的类则无法切入,
     * 可以使用@Pointcut("execution(void android.view.View.OnClickListener+.onClick(..))  && args(view)")
     * @param view
     */
    @Pointcut("execution(void android.view.View.OnClickListener.onClick(..))  && args(view)")
    public void onClick(View view) {
        Log.e(TAG,"Aspect=======:"+ "fun = onClick");
    }
}
