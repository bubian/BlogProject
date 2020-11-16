package com.pds.sample.module.html;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.BulletSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.pds.router.module.SampleGroupRouter;
import com.pds.sample.R;
import com.pds.util.unit.UnitConversionUtils;

import org.xml.sax.Attributes;
import org.xml.sax.XMLReader;

import blog.pds.com.three.htmlcompat.HtmlCompat;

// https://developer.android.google.cn/reference/androidx/core/text/HtmlCompat
@Route(path = SampleGroupRouter.TV_COMPAT_HTML)
public class HtmlCompatActivity extends AppCompatActivity {

    private static final String TAG = HtmlCompatActivity.class.getSimpleName();

    private TextView mTvHello;

    private int mBulletGapWidth;

    private boolean mUseNative;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_html_compat);
        mTvHello = findViewById(R.id.tv_hello);
        mBulletGapWidth = UnitConversionUtils.dip2px(this,8);
        update();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        mUseNative = menu.findItem(R.id.action_native).isChecked();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_native) {
            mUseNative = !item.isChecked();
            item.setChecked(mUseNative);
            update();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void update() {
        Spanned fromHtml;
        String source = getString(R.string.html);
        if (mUseNative) {
            Html.ImageGetter imageGetter = new Html.ImageGetter() {
                @Override
                public Drawable getDrawable(String source) {
                    return HtmlCompatActivity.this.getDrawable(source, null);
                }
            };
            Html.TagHandler tagHandler = new Html.TagHandler() {
                @Override
                public void handleTag(boolean opening, String tag, Editable output,
                                      XMLReader xmlReader) {
                    HtmlCompatActivity.this.handleTag(opening, tag, null, output, xmlReader);
                }
            };
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                fromHtml = Html.fromHtml(source, 0, imageGetter, tagHandler);
            } else {
                fromHtml = Html.fromHtml(source, imageGetter, tagHandler);
            }
        } else {
            HtmlCompat.ImageGetter imageGetter = new HtmlCompat.ImageGetter() {
                @Override
                public Drawable getDrawable(String source, Attributes attributes) {
                    return HtmlCompatActivity.this.getDrawable(source, attributes);
                }
            };
            HtmlCompat.TagHandler tagHandler = new HtmlCompat.TagHandler() {
                @Override
                public void handleTag(boolean opening, String tag, Attributes attributes,
                                      Editable output, XMLReader xmlReader) {
                    HtmlCompatActivity.this.handleTag(opening, tag, attributes, output, xmlReader);
                }
            };
            HtmlCompat.SpanCallback spanCallback = new HtmlCompat.SpanCallback() {
                @Override
                public Object onSpanCreated(String tag, Object span) {
                    Log.d(TAG, "New span for <" + tag + ">: " + span);
                    if (span instanceof BulletSpan) {
                        return new BulletSpan(mBulletGapWidth);
                    }
                    return span;
                }
            };
            fromHtml = HtmlCompat.fromHtml(this, source,
                    HtmlCompat.FROM_HTML_SEPARATOR_LINE_BREAK_LIST_ITEM,
                    imageGetter, tagHandler, spanCallback);
        }
        mTvHello.setMovementMethod(LinkMovementMethod.getInstance());
        mTvHello.setText(fromHtml);
    }

    private Drawable getDrawable(String source, Attributes attributes) {
        Resources resources = getResources();
        int drawableId = resources.getIdentifier(source, "drawable", getPackageName());
        Drawable drawable = ContextCompat.getDrawable(this, drawableId);
        if (drawable != null) {
            int width, height;
            if (attributes == null) {
                width = drawable.getIntrinsicWidth();
                height = drawable.getIntrinsicHeight();
            } else {
                width = Integer.parseInt(attributes.getValue("width"));
                width = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, width, resources.getDisplayMetrics()));
                float ratio = (float) width / (float) drawable.getIntrinsicWidth();
                height = (int) (drawable.getIntrinsicHeight() * ratio);
            }
            drawable.setBounds(0, 0, width, height);
        }
        return drawable;
    }

    private HtmlTagHandler mHtmlTagHandler = new HtmlTagHandler("myfont");
    public void handleTag(boolean opening, String tag, Attributes attributes, Editable output,
                          XMLReader xmlReader) {
        // Manipulate the output here for otherwise unsupported tags
        Log.d(TAG, "Unhandled tag <" + tag + ">");
        mHtmlTagHandler.handleTag(opening,tag,output,xmlReader);
    }

}
