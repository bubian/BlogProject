package com.pds.ui.view;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.graphics.*;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import com.pds.ui.R;
import com.pds.util.StringUtils;
import com.pds.util.unit.UnitConversionUtils;

/**
 * CreateTime:  2018/12/5 7:06 PM
 * Email：pengdaosong@medlinker.com.
 * Description:
 * @author pengdaosong
 */
public class UiTableLayoutView extends View {
  private final int mTableContentTextSize;
  //从右至左，分别表示有无left，top，right，bottom边框线（0 - 无，1 - 有）
  private int mFrameLine = 0x0101;
  private int mFrameLineColor;
  private int mFrameLineWidth = 2;
  //从右至左，分别表示有无横向分割线，垂直分割线
  private byte mContentLine = 0x11;

  private Path mHorizontalFrameLinePath = new Path();
  private Path mVerticalFrameLinePath = new Path();

  private int mVerticalGap;
  private int mHorizontalGap;

  private int cellWidth;
  private int cellHeight;

  private int width;
  private int height;

  private Paint mFrameLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
  private Paint mTableHeadPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
  private Paint mTableContentPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
  private Paint mTableImagePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

  private int mTableHeadColor = Color.RED;
  private int mTableContentColor;

  private String[] mHorizontalTitle;
  private String[] mVerticalTitle;

  //是否绘制类似excel第一个单元格分类表格
  private boolean mIsClassifyTitle = false;
  private boolean mIsPlaceholderOneCell = false;
  private boolean mIsOpenAutoUpdateEncoderData = true;
  private String[] mContentStrData;
  private int[] mContentIntData;

  private static final int MODE_STRING_DATA = 1;
  private static final int MODE_ENCODER_DATA = 2;
  private int mMode = MODE_ENCODER_DATA;

  private int mZeroImageId =-1;
  private int mOneImageId = -1;

  private int mTableHeadTextSize;
  private int mTableHeadTextHeight;

  private int mStartRowIndex;
  private int mStartColumnIndex;

  private Bitmap zeroBitmap;
  private Bitmap oneBitmap;

  private String zeroStr;
  private String oneStr;

  private Context mContext;

  private static final byte[] ENCODER = {4,2,1};
  private boolean mIsInterceptTouchEvent = true;

  private Matrix mMatrix;

  public UiTableLayoutView(Context context) {
    this(context,null);
  }

  public UiTableLayoutView(Context context, @Nullable AttributeSet attrs) {
    this(context, attrs,0);
  }

  public UiTableLayoutView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    mContext = context;
    mFrameLineColor = context.getResources().getColor(R.color.color_e2e7f1);
    mFrameLinePaint.setColor(mFrameLineColor);
    mFrameLinePaint.setStrokeWidth(mFrameLineWidth);
    mFrameLinePaint.setStyle(Style.STROKE);
    mTableHeadTextSize = UnitConversionUtils.sp2Px(mContext,13);
    mTableContentTextSize = UnitConversionUtils.sp2Px(mContext,12);
    setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    mTableContentColor = context.getResources().getColor(R.color.color_9a9aa4);
  }

  public UiTableLayoutView setFrameLine(int frameLine){
    mFrameLine = frameLine;
    return this;
  }

  private int mInitialDownY;
  private int mInitialDownX;
  private int mActivePointerId;
  private int mPointerIndex;
  private boolean mIsMoveEvent;

  public UiTableLayoutView  isInterceptTouchEvent(boolean isInterceptTouchEvent){
    mIsInterceptTouchEvent = isInterceptTouchEvent;
    return this;
  }
  @Override
  public boolean onTouchEvent(MotionEvent ev) {

    if (!mIsInterceptTouchEvent){
      return super.onTouchEvent(ev);
    }

    if (cellWidth <=0 || cellHeight <= 0){
      return false;
    }

    final int action = ev.getActionMasked();
    switch (action) {
      case MotionEvent.ACTION_DOWN:
        mActivePointerId = ev.getPointerId(0);
        mPointerIndex = ev.findPointerIndex(mActivePointerId);
        if (mPointerIndex < 0) {
          return false;
        }
        mInitialDownY = (int) ev.getY(mPointerIndex);
        mInitialDownX = (int) ev.getX(mPointerIndex);
        return true;
      case MotionEvent.ACTION_MOVE:
        return false;
      case MotionEvent.ACTION_UP:
        mPointerIndex = ev.findPointerIndex(mActivePointerId);
        if (mPointerIndex < 0 || mIsMoveEvent) {
          return false;
        }

        int x = (int) ev.getY(mPointerIndex);
        int y = (int) ev.getX(mPointerIndex);
        int l;
        int t;
        int r;
        int b;

//        if(x >= mInitialDownX){
//          l = mInitialDownX;
//          r = x;
//        }else {
//          l = x;
//          r = mInitialDownX;
//        }
//
//        if(y >= mInitialDownY){
//          t = mInitialDownY;
//          b = y;
//        }else{
//          t = y;
//          b = mInitialDownY;
//        }

        l = mInitialDownX;
        t = mInitialDownY;


        int top = getTop();
//
//        int hS = (l - getLeft()) / cellWidth;
//        int vS = (t - top) / cellHeight;

        int hS = l  / cellWidth;
        int vS = t / cellHeight;

//        int hE = (r - getLeft()) / cellWidth;
//        int vE = (b - getTop()) / cellHeight;

        if(vS < mStartRowIndex && mStartRowIndex > 0){
          return false;
        }

        if(hS < mStartColumnIndex && mStartColumnIndex >0){
          return false;
        }

//        if(hS != hE || vS != vE){
//          return false;
//        }

//        int yS = vS*cellHeight + getTop();
//        int xS = hS*cellWidth + getLeft();

        int yS = vS*cellHeight;
        int xS = hS*cellWidth;

        if(mInitialDownX - xS < 8 || mInitialDownY - yS < 8){
          return false;
        }

        return doCellClick(hS,vS);
    }

    return super.onTouchEvent(ev);
  }

  private boolean doCellClick(int hS, int vS) {
    if (mIsOpenAutoUpdateEncoderData){
      int d = vS - mStartRowIndex;
      int q = hS - mStartColumnIndex;



      if (d < 0 || q >= ENCODER.length){
        return false;
      }

      int t = mContentIntData[d];
      int encoder = ENCODER[q];
      if ((t & encoder) == 0){
        mContentIntData[d] += encoder;
      }else {
        mContentIntData[d] -= encoder;
      }
      invalidate();
    }
    return true;
  }


  public UiTableLayoutView bindTableTitleData(String[] horizontalTitle,String[] verticalTitle){
    return bindTableTitleData(horizontalTitle,verticalTitle,false);
  }

  private boolean checkEmpty(String[] d){
    return null == d || d.length < 1;
  }

  /**
   *
   * @param horizontalTitle 水平分类数据
   * @param verticalTitle 垂直分类数据
   * @param isClassifyTitle 当存在水平和垂直分类时设置为true，模仿excel第一个单元格分类样式
   * @return 当前View对象 - 链式调度
   */
  public UiTableLayoutView bindTableTitleData(String[] horizontalTitle,String[] verticalTitle,boolean isClassifyTitle){

    if (null == horizontalTitle && verticalTitle == null){
      return this;
    }

    if(!checkEmpty(horizontalTitle)){
      mStartRowIndex = 1;
    }

    if(!checkEmpty(verticalTitle)){
      mStartColumnIndex = 1;
    }

    mIsPlaceholderOneCell = mStartRowIndex == 1 && mStartColumnIndex == 1;
    this.mIsClassifyTitle = isClassifyTitle && mIsPlaceholderOneCell;

    mHorizontalGap = horizontalTitle.length;
    mHorizontalTitle = horizontalTitle;
    mVerticalGap = verticalTitle.length;
    mVerticalTitle = verticalTitle;


    if(!mIsClassifyTitle && mIsPlaceholderOneCell){
      mHorizontalGap += 1;
      mVerticalGap += 1;
    }

    mTableHeadPaint.setTextSize(mTableHeadTextSize);
    mTableHeadPaint.setColor(mTableHeadColor);
    mTableHeadPaint.setTextAlign(Align.CENTER);

    mTableContentPaint.setTextSize(mTableContentTextSize);
    mTableContentPaint.setColor(mTableContentColor);
    mTableContentPaint.setTextAlign(Align.CENTER);
    mTableHeadTextHeight = getTableHeadTextHeight(horizontalTitle[0]);

    if (null == mContentIntData){
      mContentIntData = new int[mVerticalGap - mStartRowIndex];
    }
    return this;
  }

  private int getTableHeadTextHeight(String title){
    Paint paint = new Paint();
    paint.setTextSize(mTableHeadTextSize);
    Rect rect = new Rect();
    paint.getTextBounds(title, 0, title.length(), rect);
    return rect.height();
  }

  public UiTableLayoutView bindTableContentData(String[] contentStrData){
    mMode = MODE_STRING_DATA;
    mIsOpenAutoUpdateEncoderData = false;
    mContentStrData = contentStrData;
    invalidate();
    return this;
  }

  public String getEncoderData(){

    String data = "";
    for ( int i = 0;i < mContentIntData.length ; i++){
      data += ""+mContentIntData[i];
    }
    return data;
  }

  public void updateData(String encoderStr){
    bindTableContentEncoderData(encoderStr,mZeroImageId,mOneImageId);
  }

  public void updateEncoderData(int[] contentIntData){
    bindTableContentEncoderData(contentIntData,mZeroImageId,mOneImageId);
  }

  public void bindTableContentEncoderData(int[] contentIntData, int zeroImageId, int oneImageId){
    mMode = MODE_ENCODER_DATA;
    mContentIntData = contentIntData;
    mZeroImageId = zeroImageId;
    mOneImageId = oneImageId;

    if (null == mMatrix){
      mMatrix = new Matrix();
    }

    if (!mIsPlaceholderOneCell){
      throw new IllegalStateException("必须指定横向和垂直标题");
    }
    createImageCache(zeroImageId,oneImageId);
    invalidate();
  }

  private void createImageCache(int zeroImageId, int oneImageId){
    try {
      if (zeroImageId > 0 && null == zeroBitmap){
        Drawable drawable = mContext.getResources().getDrawable(zeroImageId);
        zeroBitmap = drawableToBitmap(drawable);
      }
    }catch (NotFoundException e){
      try {
        zeroStr = mContext.getResources().getString(zeroImageId);
      }catch (NotFoundException ex){
        ex.printStackTrace();
      }
    }

    try {
      if (oneImageId > 0 && null == oneBitmap){
        Drawable drawable = mContext.getResources().getDrawable(oneImageId);
        oneBitmap = drawableToBitmap(drawable);
      }
    }catch (NotFoundException e){
      try {
        oneStr = mContext.getResources().getString(oneImageId);
      }catch (NotFoundException ex){
        ex.printStackTrace();
      }
    }
  }


  private Bitmap drawableToBitmap(Drawable drawable) {
    // 取 drawable 的长宽
    int w = drawable.getIntrinsicWidth();
    int h = drawable.getIntrinsicHeight();
    // 取 drawable 的颜色格式
    Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
            : Bitmap.Config.RGB_565;
    // 建立对应 bitmap
    Bitmap bitmap = Bitmap.createBitmap(w, h, config);
    // 建立对应 bitmap 的画布
    Canvas canvas = new Canvas(bitmap);
    drawable.setBounds(0, 0, w, h);
    // 把 drawable 内容画到画布中
    drawable.draw(canvas);
    return bitmap;
  }

  /**
   *
   * @param encoderStr "76543210" - 7 = 4+2+1（0x111） - 依此类推，所以有一定限制
   * @param zeroImageId 表示改位是0显示的类容
   * @param oneImageId 表示改位是1显示的类容
   * @return
   */
  public void bindTableContentEncoderData(String encoderStr, int zeroImageId,int oneImageId){
    if (StringUtils.isStrictEmpty(encoderStr) && mVerticalGap > 0){
      mContentIntData = new int[mVerticalGap - mStartRowIndex];
      createImageCache(zeroImageId,oneImageId);
      invalidate();
      return;
    }
    int len = encoderStr.length();
    int[] l = new int[len];
    for (int i = 0;i < encoderStr.length() ; i++){
      char c = encoderStr.charAt(i);
      if (!Character.isDigit(c)){
        l[i] = 0;
        continue;
      }
      l[i] = c - '0';
    }
    bindTableContentEncoderData(l,zeroImageId,oneImageId);
  }

  private boolean isDrawData(){
    return width < 1 || height < 1
            || mHorizontalGap < 1;
  }

  private int getBackGroundColor(){
    Drawable drawable = getBackground();
    if(null == drawable){
      return Color.TRANSPARENT;
    }else {
      return ((ColorDrawable)drawable).getColor();
    }
  }

  @Override
  protected void onDraw(Canvas canvas) {
    canvas.drawColor(getBackGroundColor());
    width = getWidth();
    height = getHeight();

    if (mHorizontalGap<= 0 || mVerticalGap <= 0){
      return;
    }

    cellWidth = width / mHorizontalGap;
    cellHeight = height / mVerticalGap;

    if (isDrawData()){
      return;
    }
    //绘制边线
    drawFrameLine(canvas);
    //绘制单元格类容
    drawTableContent(canvas);
  }

  private void drawFrameLine(Canvas canvas){
    if (!mHorizontalFrameLinePath.isEmpty() || !mVerticalFrameLinePath.isEmpty()){
      canvas.drawPath(mVerticalFrameLinePath,mFrameLinePaint);
      canvas.drawPath(mHorizontalFrameLinePath,mFrameLinePaint);
      return;
    }
    drawHorizontalFrameLine(canvas);
    drawVerticalFrameLine(canvas);
  }

  private void drawHorizontalFrameLine(Canvas canvas){
    //记录水平横线路径
    if ((0x0010 & mFrameLine) != 0 ){
      mHorizontalFrameLinePath.moveTo(0,0);
      mHorizontalFrameLinePath.lineTo(width,0);
    }

    if ((mContentLine & 0x01) != 0){
      for(int i = 1 ; i < mVerticalGap; i++){
        int y = cellHeight * i;
        mHorizontalFrameLinePath.moveTo(0,y);
        mHorizontalFrameLinePath.lineTo(width,y);
      }
    }
    if ((0x1000 & mFrameLine) != 0 ){
      mHorizontalFrameLinePath.moveTo(0,height);
      mHorizontalFrameLinePath.lineTo(width,height);
    }
    canvas.drawPath(mHorizontalFrameLinePath,mFrameLinePaint);
  }

  private void drawVerticalFrameLine(Canvas canvas){
    //记录垂直横线路径
    if ((0x0001 & mFrameLine) != 0 ){
      mVerticalFrameLinePath.moveTo(0,0);
      mVerticalFrameLinePath.lineTo(0,height);
    }

    if ((mContentLine & 0x10) != 0){
      for(int i = 1 ; i < mHorizontalGap; i++){
        int x = cellWidth * i;
        mVerticalFrameLinePath.moveTo(x,0);
        mVerticalFrameLinePath.lineTo(x,height);
      }
    }

    if ((0x0100 & mFrameLine) != 0 ){
      mVerticalFrameLinePath.moveTo(width,0);
      mVerticalFrameLinePath.lineTo(width,height);
    }
    canvas.drawPath(mVerticalFrameLinePath,mFrameLinePaint);
  }

  private void drawTableContent(Canvas canvas) {
    if (mIsClassifyTitle){
      drawOneCell(canvas);
      drawText(canvas);
    }else {
      drawText(canvas);
    }
  }

  private void drawTableTitleLine(Canvas canvas) {
    if (!mIsClassifyTitle){
      return;
    }
    canvas.drawLine(0,0,cellWidth,cellHeight,mFrameLinePaint);
  }

  private void drawOneCell(Canvas canvas) {
    //绘制第一个单元格分类
    drawTableTitleLine(canvas);

    String topTitle = mHorizontalTitle[0];
    String leftTitle = mVerticalTitle[0];

    int t = 0;
    int b = mTableHeadTextHeight + 8;
    int l = cellWidth / cellHeight * b;
    int r = cellWidth;


    int hX = l + (r - l) / 2;
    int hY = b;
    canvas.drawText(topTitle,hX,hY,mTableHeadPaint);

    int vX = (cellWidth - l) / 2;
    int vY = (int) (1.5*hY + mTableHeadTextHeight);
    canvas.drawText(leftTitle,vX,vY,mTableHeadPaint);
  }

  private void drawText(Canvas canvas) {
    drawTableHeadText(canvas);
    if ((null == mContentIntData || mContentIntData.length < 1)
            && (null == mContentStrData || mContentStrData.length < 1)){
      return;
    }
    drawContentText(canvas);
  }

  private void drawTableHeadText(Canvas canvas) {
    if (checkEmpty(mHorizontalTitle) && checkEmpty(mVerticalTitle)){
      return;
    }

    int startIndex = mIsPlaceholderOneCell ? 1 : 0;
    int cap = mIsClassifyTitle ? 1 : 0;
    if (!checkEmpty(mHorizontalTitle)){
      for (int h = startIndex ; h < mHorizontalGap; h++){
        drawCell(canvas,h,mHorizontalTitle[h - startIndex + cap]);
      }
    }

    if (!checkEmpty(mVerticalTitle)){
      for (int v = startIndex;v < mVerticalGap; v++){
        drawCell(canvas,v*mHorizontalGap,mVerticalTitle[v - startIndex + cap]);
      }
    }
  }

  private void drawContentText(Canvas canvas) {
    int startIndex = mStartRowIndex * mHorizontalGap + mStartColumnIndex -1;
    startIndex = startIndex < 0 ? 0 : startIndex;

    int num = mHorizontalGap * mVerticalGap;

    for (int i = startIndex ; i < num; i++){
      int hIndex = i % mHorizontalGap;
      int vIndex = i / mHorizontalGap;

      if (mStartRowIndex -1 >= hIndex){
        continue;
      }

      if (hIndex >= mHorizontalGap || vIndex >= mVerticalGap){
        return;
      }

      if (mMode == MODE_STRING_DATA){
        int index = (hIndex - mStartColumnIndex)*(vIndex - mStartRowIndex);
        if(index < 0 || index >= mContentStrData.length){
          continue;
        }
        String str = mContentStrData[index];
        drawCell(canvas,i,str);

      }else if (mMode == MODE_ENCODER_DATA){

        int encoderStart = hIndex - mStartColumnIndex;
        encoderStart = encoderStart < 0 ? 0 : encoderStart;

        if (encoderStart >= ENCODER.length){
          return;
        }
        int encoder = ENCODER[encoderStart];

        int vIndexStart = vIndex - mStartRowIndex;
        vIndexStart = vIndexStart < 0 ? 0 : vIndexStart;
        if(vIndexStart >= mContentIntData.length ){
          return;
        }
        if ((encoder & mContentIntData[vIndexStart]) != 0){
          if (null != oneBitmap){
            drawCell(canvas,i,oneBitmap);
          }else {
            drawCell(canvas,i,oneStr);
          }
        }else {
          if (null != zeroBitmap){
            drawCell(canvas,i,zeroBitmap);
          }else {
            drawCell(canvas,i,zeroStr);
          }
        }
      }
    }
  }
  private void drawCell(Canvas canvas, int position, String str){
    if (TextUtils.isEmpty(str)){
      return;
    }
    int l = getCellX(position);
    int t = getCellY(position);

    float x = l + cellWidth / 2;
    Paint.FontMetrics fontMetrics = mTableContentPaint.getFontMetrics();
    float y = t +  cellHeight / 2 + (Math.abs(fontMetrics.ascent) - fontMetrics.descent) / 2;

    canvas.drawText(str,x ,y,mTableContentPaint);
  }

  private void drawCell(Canvas canvas, int position, Bitmap bitmap){
    if (null == bitmap || bitmap.isRecycled()){
      return;
    }

    int l = getCellX(position);
    int t = getCellY(position);

    int w = bitmap.getWidth();
    int h = bitmap.getHeight();
    if (null == mMatrix){
      mMatrix = new Matrix();
    }
    mMatrix.setTranslate(l + (cellWidth - w) / 2,t + (cellHeight - h) / 2);
    canvas.drawBitmap(bitmap,mMatrix,mTableImagePaint);
  }

  private int getCellX(int position){
    int hIndex = position % mHorizontalGap;
    return hIndex * cellWidth;
  }

  private int getCellY(int position){
    int vIndex = position / mHorizontalGap;
    return vIndex * cellHeight;
  }
}
