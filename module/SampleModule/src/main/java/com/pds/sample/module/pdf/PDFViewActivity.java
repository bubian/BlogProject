/**
 * Copyright 2016 Bartosz Schiller
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pds.sample.module.pdf;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.pds.pdf.core.ExtPDFView;
import com.pds.pdf.utils.FileUtils;
import com.pds.router.module.BundleKey;
import com.pds.router.module.SampleGroupRouter;
import com.shockwave.pdfium.PdfDocument;

import java.util.List;

@Route(path = SampleGroupRouter.PDF_LOAD)
public class PDFViewActivity extends AppCompatActivity implements OnPageChangeListener, OnLoadCompleteListener,
        OnPageErrorListener {

    private static final String TAG = PDFViewActivity.class.getSimpleName();

    ExtPDFView pdfView;
    String mUrl;
    Integer pageNumber = 0;
    String pdfFileName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUrl = getIntent().getStringExtra(BundleKey.PARAM);
        displayFromUrl(mUrl);
        setTitle(pdfFileName);
    }

    private void displayFromUrl(String url) {
        pdfView = new ExtPDFView(this);
        setContentView(pdfView);
        pdfFileName = FileUtils.getFileNameByUrl(url);
        pdfView.fromUrl(url)
                .defaultPage(pageNumber)
                .onPageChange(this)
                .enableAnnotationRendering(true)
                .onLoad(this)
                .scrollHandle(new DefaultScrollHandle(this))
                .spacing(10) // in dp
                .onPageError(this);
        pdfView.enableCache(true).useDefaultProgressView().go();
    }

    @Override
    public void onPageChanged(int page, int pageCount) {
        pageNumber = page;
        setTitle(String.format("%s %s / %s", pdfFileName, page + 1, pageCount));
    }

    @Override
    public void loadComplete(int nbPages) {
        PdfDocument.Meta meta = pdfView.getPDFView().getDocumentMeta();
        printBookmarksTree(pdfView.getPDFView().getTableOfContents(), "-");

    }

    public void printBookmarksTree(List<PdfDocument.Bookmark> tree, String sep) {
        for (PdfDocument.Bookmark b : tree) {
            if (b.hasChildren()) {
                printBookmarksTree(b.getChildren(), sep + "-");
            }
        }
    }

    @Override
    public void onPageError(int page, Throwable t) {
        Log.e(TAG, "Cannot load page " + page);
    }
}
