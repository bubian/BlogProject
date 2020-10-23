package com.pds.pdf.core;

import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.github.jorgecastillo.FillableLoader;
import com.github.jorgecastillo.FillableLoaderBuilder;
import com.github.jorgecastillo.clippingtransforms.PlainClippingTransform;
import com.pds.pdf.R;
import com.pds.pdf.path.PDFPath;
import com.pds.pdf.path.Paths;
import com.pds.pdf.process.ExtFillAbleLoader;
import com.pds.pdf.process.ProgressView;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/10/23 10:56 AM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
public class ViewHelper {

    public static void addProcessView(ViewGroup parent, View process, int direction) {
        parent.addView(process, buildProcessLayoutParams(process, direction));
    }

    public static View buildDefaultProcessView(ViewGroup root, int type) {
        Context context = root.getContext();
        View progressView;
        if (Constants.DEFAULT_TYPE_ONE == type) {
            ExtFillAbleLoader loader = new ExtFillAbleLoader(context);

            FillableLoaderBuilder loaderBuilder = new FillableLoaderBuilder();
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(dip2px(context, 100), dip2px(context, 100));
            FillableLoader fillableLoader = loaderBuilder
                    .parentView(loader)
                    .layoutParams(params)
                    .svgPath(PDFPath.PDF_PATH_ONE)
                    .originalDimensions(970, 970)
                    .strokeWidth(dip2px(context, 1))
                    .strokeColor(Color.parseColor("#1c9ade"))
                    .fillColor(Color.parseColor("#1c9ade"))
                    .strokeDrawingDuration(2000)
                    .fillDuration(5000)
                    .clippingTransform(new PlainClippingTransform())
                    .build();

            loader.svgPath(PDFPath.PDF_PATH_ONE);
            loader.setFillAbleLoader(fillableLoader);
            loader.setVisibility(View.GONE);
            progressView = loader;
        } else {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dip2px(context, 3));
            progressView = new ProgressView(context);
            progressView.setLayoutParams(params);
        }
        return progressView;
    }

    public static RelativeLayout.LayoutParams buildProcessLayoutParams(View process, int direction) {
        RelativeLayout.LayoutParams params;
        if (process.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
            params = (RelativeLayout.LayoutParams) process.getLayoutParams();
        } else {
            params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        if (Constants.DIRECTION_TOP == direction) {
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        } else if (Constants.DIRECTION_BOTTOM == direction) {
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        } else {
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
        }
        return params;
    }

    public static ViewGroup.LayoutParams buildPDFViewLayoutParams(View process, View pdfView, int direction) {
        if (null == process) {
            return pdfView.getLayoutParams();
        }

        RelativeLayout.LayoutParams params;
        if (pdfView.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
            params = (RelativeLayout.LayoutParams) pdfView.getLayoutParams();
        } else {
            params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
        int processId = process.getId();
        if (processId == View.NO_ID) {
            processId = R.id.pdf_process_view_id;
            process.setId(processId);
        }
        if (Constants.DIRECTION_TOP == direction) {
            params.addRule(RelativeLayout.BELOW, processId);
        } else if (Constants.DIRECTION_BOTTOM == direction) {
            params.addRule(RelativeLayout.ABOVE, processId);
        }
        return params;
    }


    public static DisplayMetrics getDisplayMetrics(Context context) {
        return context.getResources().getDisplayMetrics();
    }

    public static int dip2px(Context context, float dpValue) {
        return (int) (dpValue * getDisplayMetrics(context).density + 0.5f);
    }
}
