package lib.pds.com.runtime;

import android.util.Log;
import lib.pds.com.runtime.utils.MethodPrint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import java.util.concurrent.TimeUnit;

@Aspect
public class AspectBlog {
    private static volatile boolean enabled = true;

    public static void setEnabled(boolean enabled) {
        AspectBlog.enabled = enabled;
    }

    @Pointcut("within(@lib.pds.com.aspect.anno.DebugLog *)")
    public void withinAnnotatedClass() {
    }

    @Pointcut("execution(!synthetic * *(..)) && withinAnnotatedClass()")
    public void methodInsideAnnotatedType() {
    }

    @Pointcut("execution(!synthetic *.new(..)) && withinAnnotatedClass()")
    public void constructorInsideAnnotatedType() {
    }

    @Pointcut("execution(@lib.pds.com.aspect.anno.DebugLog * *(..)) || methodInsideAnnotatedType()")
    public void method() {
    }

    @Pointcut("execution(@lib.pds.com.aspect.anno.DebugLog *.new(..)) || constructorInsideAnnotatedType()")
    public void constructor() {
    }

    @Around("method() || constructor()")
    public Object logAndExecute(ProceedingJoinPoint joinPoint) throws Throwable {
        Log.e("-->:", "--------------start---------------");
        MethodPrint.enterMethod(joinPoint);

        long startNanos = System.nanoTime();
        Object result = joinPoint.proceed();
        long stopNanos = System.nanoTime();
        long lengthMillis = TimeUnit.NANOSECONDS.toMillis(stopNanos - startNanos);

        MethodPrint.exitMethod(joinPoint, result, lengthMillis);

        return result;
    }
}
