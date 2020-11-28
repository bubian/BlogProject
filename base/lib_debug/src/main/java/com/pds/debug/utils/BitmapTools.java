package com.pds.debug.utils;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;

import com.pds.debug.ModuleDebug;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class BitmapTools {
	private static final String TAG = "BitmapTools";
	private static final Config defaultConfig = Config.ARGB_8888;
	public static final int BITMAP_LIFETIME = 5000;
	public static final String TAG_RECYCLER = "Xul BMP Recycler";
	private static ThreadLocal<byte[]> _local_buf;
	private static ThreadLocal<XdBufferedInputStream> _localBufferedInputStream;

	static {
		_local_buf = new ThreadLocal<byte[]>();
		_localBufferedInputStream = new ThreadLocal<XdBufferedInputStream>();
	}

	private static final ArrayList<Pair<Long, Bitmap>> _recycledBitmapQueue = new ArrayList<Pair<Long, Bitmap>>();

	private static int _totalBitmapCacheSize = 0;
	private static final int _maxBitmapCacheSize = 24 * 1024 * 1024;
	private static final int _maximumSameDimension = 72;

	private static Method getAllocationByteCountMethod = null;
	private static Method reconfigMethod = null;

	public static final int MINIMUM_API_LEVEL = 19;

	static {
		if (Build.VERSION.SDK_INT >= MINIMUM_API_LEVEL) {
			try {
				getAllocationByteCountMethod = Bitmap.class.getMethod("getAllocationByteCount");
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				reconfigMethod = Bitmap.class.getMethod("reconfigure", int.class, int.class, Config.class);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static int calBitmapByteSize(Bitmap bmp) {
		if (getAllocationByteCountMethod != null) {
			try {
				return (Integer) getAllocationByteCountMethod.invoke(bmp);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return bmp.getByteCount();
	}

	public static int calBitmapPixelsCount(Bitmap bmp) {
		return bmp.getWidth() * bmp.getHeight();
	}

	private static int calBitmapPixelSize(Bitmap bmp) {
		Config config = bmp.getConfig();
		int pixelSize = 4;
		if (Config.ARGB_8888.equals(config)) {
			pixelSize = 4;
		} else if (Config.RGB_565.equals(config)) {
			pixelSize = 2;
		} else if (Config.ARGB_4444.equals(config)) {
			pixelSize = 2;
		}
		return pixelSize;
	}

	public static void cleanRecycledBitmaps(long timestamp) {
		synchronized (_recycledBitmapQueue) {
			for (int i = _recycledBitmapQueue.size() - 1; i >= 0; --i) {
				Pair<Long, Bitmap> item = _recycledBitmapQueue.get(i);
				if (item.first <= timestamp) {
					Bitmap bmp = item.second;
					int byteCount = calBitmapByteSize(bmp);
					if (ModuleDebug.DEBUG && byteCount <= 0) {
						Log.e(TAG_RECYCLER, "invalid bitmap size(clean phase)!!! " + byteCount);
					}

					int pixelCount = calBitmapPixelsCount(bmp);
					_totalBitmapCacheSize -= pixelCount;
					_recycledBitmapQueue.remove(i);
				}
			}
		}
	}

	public static Bitmap createBitmapFromRecycledBitmaps(int width, int height) {
		synchronized (_recycledBitmapQueue) {
			int requestBmpSize = width * height * 4;
			int diffSize = Integer.MAX_VALUE;
			int selectedCacheItem = -1;
			for (int i = 0, recycledBitmapQueueSize = _recycledBitmapQueue.size(); i < recycledBitmapQueueSize; i++) {
				Pair<Long, Bitmap> item = _recycledBitmapQueue.get(i);
				Bitmap bmp = item.second;

				if (ModuleDebug.DEBUG) {
					if (!bmp.isMutable() || bmp.isRecycled()) {
						if (!bmp.isMutable()) {
							Log.e(TAG_RECYCLER, "invalid bitmap immutable(reuse phase)!!!");
						} else {
							Log.e(TAG_RECYCLER, "invalid bitmap recycled(reuse phase)!!!");
						}
						_recycledBitmapQueue.remove(i);
						--i;
						_totalBitmapCacheSize -= calBitmapPixelsCount(bmp);
						continue;
					}
				}

				if (Build.VERSION.SDK_INT >= MINIMUM_API_LEVEL) {
					int bmpSize = calBitmapByteSize(bmp);
					int minimumOutSize = (int) (1.1f * requestBmpSize + 1024);
					if (requestBmpSize <= bmpSize && bmpSize <= minimumOutSize) {
						int newDiffSize = bmpSize - requestBmpSize;
						if (newDiffSize <= diffSize) {
							diffSize = newDiffSize;
							selectedCacheItem = i;
						}
					}
				} else if (bmp.getWidth() == width && bmp.getHeight() == height && Config.ARGB_8888.equals(bmp.getConfig())) {
					selectedCacheItem = i;
					break;
				}
			}

			if (selectedCacheItem >= 0) {
				Pair<Long, Bitmap> item = _recycledBitmapQueue.remove(selectedCacheItem);
				Bitmap bmp = item.second;
				int pixelCount = calBitmapPixelsCount(bmp);
				int byteCount = calBitmapByteSize(bmp);
				if (ModuleDebug.DEBUG && byteCount <= 0) {
					Log.e(TAG_RECYCLER, "invalid bitmap size(reuse phase)!!! " + byteCount);
				}
				_totalBitmapCacheSize -= pixelCount;
				if (ModuleDebug.DEBUG) {
					Log.d(TAG_RECYCLER, "reuse bitmap!!! " + bmp.getWidth() + "x" + bmp.getHeight());
				}
				bmp.eraseColor(Color.TRANSPARENT);
				if (reconfigMethod != null) {
					try {
						reconfigMethod.invoke(bmp, width, height, Config.ARGB_8888);
					} catch (Exception e) {
						Log.e(TAG_RECYCLER, "reconfigure bitmap failed(reuse phase)!!! " + byteCount);
					}
				}
				return bmp;
			}
		}
		if (ModuleDebug.DEBUG) {
			Log.d(TAG_RECYCLER, "new bitmap!!! " + width + "x" + height);
		}
		return Bitmap.createBitmap(width, height, Config.ARGB_8888);
	}

	public static void recycleBitmap(Bitmap bmp) {
		if (bmp == null) {
			return;
		}
		if (!bmp.isMutable()) {
			if (ModuleDebug.DEBUG) {
				Log.e(TAG_RECYCLER, "bitmap immutable!!!");
			}
			return;
		}
		if (bmp.isRecycled()) {
			if (ModuleDebug.DEBUG) {
				Log.e(TAG_RECYCLER, "bitmap recycled!!!");
			}
			return;
		}
		int pixelCount = calBitmapPixelsCount(bmp);
		if (_totalBitmapCacheSize + pixelCount >= _maxBitmapCacheSize) {
			// exceed the cache limits
			if (ModuleDebug.DEBUG) {
				Log.d(TAG_RECYCLER, "exceed the cache limits");
			}
			return;
		}

		int byteCount = calBitmapByteSize(bmp);
		if (byteCount <= 0) {
			if (ModuleDebug.DEBUG) {
				Log.e(TAG_RECYCLER, "invalid bitmap bytecount! " + byteCount);
			}
			return;
		}

		synchronized (_recycledBitmapQueue) {
			if (ModuleDebug.DEBUG) {
				for (int i = 0, recycledBitmapQueueSize = _recycledBitmapQueue.size(); i < recycledBitmapQueueSize; i++) {
					Pair<Long, Bitmap> item = _recycledBitmapQueue.get(i);
					Bitmap recycledBmp = item.second;
					if (recycledBmp.equals(bmp)) {
						Log.e(TAG_RECYCLER, "duplicate bitmap! " + byteCount);
						return;
					}
				}
			}

			int sameSizeBmpCount = 0;
			for (int i = 0, recycledBitmapQueueSize = _recycledBitmapQueue.size(); i < recycledBitmapQueueSize && sameSizeBmpCount < _maximumSameDimension; i++) {
				Pair<Long, Bitmap> item = _recycledBitmapQueue.get(i);
				Bitmap recycledBmp = item.second;
				if (pixelCount == calBitmapPixelsCount(recycledBmp)) {
					++sameSizeBmpCount;
				}
			}

			if (sameSizeBmpCount >= _maximumSameDimension) {
				if (ModuleDebug.DEBUG) {
					Log.d(TAG_RECYCLER, "too many recycled bitmap with same dimension");
				}
				return;
			}

			_recycledBitmapQueue.add(Pair.create(XdUtils.timestamp() + BITMAP_LIFETIME, bmp));
			_totalBitmapCacheSize += pixelCount;
		}
	}


	/**
	 * 解码图片,并转换图片像素格式及尺寸
	 * 进行缩放时, 会保持原图片的宽高比.
	 * 当水平方向与竖直方向上的缩放比例不同时.会选择其中较大的比例进行缩放.
	 * 保持输出尺寸大于或等于目标尺寸.
	 *
	 * @param path        图片路径
	 * @param pixelFormat 解码图片像素格式, null表示使用默认像素格式
	 * @param outWidth    解码图片的目标宽度, 0表示使用默认宽度
	 * @param outHeight   解码图片的目标高度, 0表示使用默认高度
	 * @return
	 */
	public static Bitmap decodeFile(String path, Config pixelFormat, int outWidth, int outHeight) {
		try {
			return decodeStream(new FileInputStream(path), pixelFormat, outWidth, outHeight, 0, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Bitmap decodeStream(InputStream is, Config pixelFormat, int outWidth, int outHeight, int maxWidth, int maxHeight) {
		byte[] decode_buf = _local_buf.get();
		if (decode_buf == null) {
			decode_buf = new byte[64 * 1024];
			_local_buf.set(decode_buf);
		}

		try {
			if (!is.markSupported()) {
				XdBufferedInputStream xulBufferedInputStream = _localBufferedInputStream.get();
				if (xulBufferedInputStream == null) {
					xulBufferedInputStream = new XdBufferedInputStream(is, 64 * 1024);
				} else {
					xulBufferedInputStream.resetInputStream(is);
				}
				is = xulBufferedInputStream;
			}

			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inTempStorage = decode_buf;
			opts.inPreferredConfig = pixelFormat;

			opts.inJustDecodeBounds = true;
			is.mark(64 * 1024);
			BitmapFactory.decodeStream(is, null, opts);
			try {
				is.reset();
			} catch (IOException e) {
				e.printStackTrace();
				is.close();
				return null;
			}
			if ((outWidth > 0 && outHeight >= 0) || (outWidth >= 0 && outHeight > 0) ||
				(maxWidth > 0 && maxHeight >= 0) || (maxWidth >= 0 && maxHeight > 0)) {
				opts.inScaled = true;
				opts.inTargetDensity = DisplayMetrics.DENSITY_DEFAULT;
				int scale_x;
				int scale_y;
				if (maxWidth > 0 || maxHeight > 0) {
					if (outHeight == 0 && outWidth == 0) {
						outWidth = Math.min(opts.outWidth, maxWidth);
						outHeight = Math.min(opts.outHeight, maxHeight);
					} else {
						if (outWidth > 0 && maxWidth > 0) {
							outWidth = Math.min(outWidth, maxWidth);
						}
						if (outHeight > 0 && maxHeight > 0) {
							outHeight = Math.min(outHeight, maxHeight);
						}
					}
				}
				if (outWidth == 0) {
					scale_x = scale_y = opts.inTargetDensity * opts.outHeight / outHeight;
				} else if (outHeight == 0) {
					scale_x = scale_y = opts.inTargetDensity * opts.outWidth / outWidth;
				} else {
					scale_x = opts.inTargetDensity * opts.outWidth / outWidth;
					scale_y = opts.inTargetDensity * opts.outHeight / outHeight;
				}
				opts.inDensity = Math.min(scale_x, scale_y);
				if (opts.inDensity == opts.inTargetDensity) {
					opts.inScaled = false;
					outWidth = opts.outWidth;
					outHeight = opts.outHeight;
				} else {
					outWidth = XdUtils.roundToInt((float) opts.outWidth * opts.inTargetDensity / opts.inDensity);
					outHeight = XdUtils.roundToInt((float) opts.outHeight * opts.inTargetDensity / opts.inDensity);
				}
			} else {
				outWidth = opts.outWidth;
				outHeight = opts.outHeight;
			}

			if (ModuleDebug.DEBUG) {
				Log.i(TAG, " width " + opts.outWidth + " height " + opts.outHeight + "  ==> width " + outWidth + " height " + outHeight);
			}

			opts.inJustDecodeBounds = false;
			opts.inPurgeable = false;
			opts.inMutable = true;
			if (!opts.inScaled) {
				opts.inBitmap = createBitmapFromRecycledBitmaps(opts.outWidth, opts.outHeight);
			}
			opts.inSampleSize = 1;
			Bitmap bm = BitmapFactory.decodeStream(is, null, opts);
			return bm;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static Bitmap decodeStream(InputStream is, Config pixelFormat, int outWidth, int outHeight) {
		return decodeStream(is, pixelFormat, outWidth, outHeight, 0, 0);
	}
}
