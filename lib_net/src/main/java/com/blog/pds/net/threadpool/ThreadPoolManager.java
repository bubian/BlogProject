package com.blog.pds.net.threadpool;

import android.os.Build;
import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileFilter;
import java.util.concurrent.*;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author: pengdaosong
 * CreateTime:  2019/1/24 10:54 AM
 * Email：pengdaosong@medlinker.com
 * Description:线程池设计,避免不断创建线程,导致的性能损耗,以及资源占用
 * 1. 避免线程频繁创建消毁
 * 2. 避免系统资源紧张
 * 3. 更好地管理线程
 */
public class ThreadPoolManager {

	public static final int POLICY_EXECUTOR_DEFAULT = 0;
	public static final int POLICY_EXECUTOR_FIXED = 1;
	public static final int POLICY_EXECUTOR_SINGLE = 2;
	public static final int POLICY_EXECUTOR_CACHED = 3;
	public static final int POLICY_EXECUTOR_SCHEDULED = 4;

	private static final TimeUnit MILLISECONDS = TimeUnit.SECONDS;

	/**
	 * 未知设备
	 */
	public static final int DEVICE_INFO_UNKNOWN = 0;
	/**
	 * 获取cpu的数量
	 */
	private static final int CPU_COUNT = getCountOfCPU();

	/**
	 * Linux中的设备都是以文件的形式存在，CPU也不例外，因此CPU的文件个数就等价与核数。
	 * Android的CPU 设备文件位于/sys/devices/system/cpu/目录，文件名的的格式为cpu\d+。
	 */
	public static int getCountOfCPU() {
		if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
			return 1;
		}
		int count;
		try {
			count = new File("/sys/devices/system/cpu/").listFiles(CPU_FILTER).length;
		} catch (SecurityException | NullPointerException e) {
			count = DEVICE_INFO_UNKNOWN;
		}
		return count;
	}

	private static final FileFilter CPU_FILTER = new FileFilter() {
		@Override
		public boolean accept(File pathname) {
			String path = pathname.getName();
			if (path.startsWith("cpu")) {
				for (int i = 3; i < path.length(); i++) {
					if (path.charAt(i) < '0' || path.charAt(i) > '9') {
						return false;
					}
				}
				return true;
			}
			return false;
		}
	};

	/**
	 *
	 */
	public final static ExecutorService EVENT_EXECUTOR;
	/**
	 * 线程池中核心线程的数量
	 */
	private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
	/**
	 * 线程池中最大线程数量
	 */
	private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
	/**
	 * 非核心线程的超时时长，当系统中非核心线程闲置时间超过keepAliveTime之后，
	 * 则会被回收。如果ThreadPoolExecutor的allowCoreThreadTimeOut属性设置为true，则该参数也表示核心线程的超时时长
	 */
	private static final int KEEP_ALIVE = 1;
	/**
	 * 拒绝策略，当线程无法执行新任务时（一般是由于线程池中的线程数量已经达到最大数或者线程池关闭导致的,
	 * 默认情况下，当线程池无法处理新线程时，会抛出一个RejectedExecutionException
	 */
	private static final RejectedExecutionHandler EVENT_HANDLER = new CallerRunsPolicy();
	/**
	 *  线程池中的任务队列，该队列主要用来存储已经被提交但是尚未执行的任务。存储在这里的任务是由ThreadPoolExecutor的execute方法提交来的
	 */
	private static final BlockingQueue<Runnable> EVENT_POLL_WAIT_QUEUE = new LinkedBlockingQueue<>(128);
	/**
	 * 为线程池提供创建新线程的功能
	 */
	private static final ThreadFactory EVENT_THREAD_FACTORY = new ThreadFactory() {
		/**
		 * 线程id
		 */
		private final AtomicInteger COUNT = new AtomicInteger(1);

		@Override
		public Thread newThread(@NonNull Runnable r) {
			return new Thread(r, "thread id = " + COUNT.getAndIncrement());
		}
	};

	static {
		EVENT_EXECUTOR = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE,
				MILLISECONDS, EVENT_POLL_WAIT_QUEUE, EVENT_THREAD_FACTORY, EVENT_HANDLER);
	}

	public ExecutorService executorPolicy(){
		return executorPolicy(POLICY_EXECUTOR_DEFAULT);
	}

	public ExecutorService executorPolicy(int type){
		switch (type){
			case POLICY_EXECUTOR_CACHED:
				return Executors.newCachedThreadPool();
			case POLICY_EXECUTOR_SINGLE:
				return Executors.newSingleThreadExecutor();
			case POLICY_EXECUTOR_FIXED:
				return Executors.newFixedThreadPool(CORE_POOL_SIZE);
			case POLICY_EXECUTOR_SCHEDULED:
				return Executors.newScheduledThreadPool(CORE_POOL_SIZE);
			default:return EVENT_EXECUTOR;
		}
	}
}
