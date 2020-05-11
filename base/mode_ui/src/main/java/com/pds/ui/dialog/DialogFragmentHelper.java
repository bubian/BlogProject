package com.pds.ui.dialog;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import androidx.fragment.app.DialogFragment;

import com.heaven7.core.util.ViewHelper;
import com.pds.ui.R;


/**
 * @author <a href="mailto:tql@medlinker.net">tqlmorepassion</a>
 * @version 5.0
 * @description
 * @time 2017/6/16
 */
public class DialogFragmentHelper extends BaseDialogFragment {

    private int mResLayoutId;
    private InitViewListener mListener;
    private DialogInterface.OnDismissListener mDismissListener;

    public DialogFragmentHelper() {
    }

    /**
     * @param layoutId
     * @return
     */
    public static DialogFragmentHelper newInstance(int layoutId) {
        DialogFragmentHelper dialogFragment = new DialogFragmentHelper();
        Bundle bundle = new Bundle();
        bundle.putInt("layout", layoutId);
        dialogFragment.setArguments(bundle);
        return dialogFragment;
    }

    /**
     * @param listener
     * @return
     */
    public DialogFragmentHelper setListener(InitViewListener listener) {
        this.mListener = listener;
        return this;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() == null) {
            return;
        }
        final DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final WindowManager.LayoutParams layoutParams = getDialog().getWindow().getAttributes();
        layoutParams.windowAnimations = R.style.dialog_animation;
        layoutParams.width = dm.widthPixels;
        layoutParams.gravity = Gravity.BOTTOM;
        getDialog().getWindow().setAttributes(layoutParams);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        unpackBundle();
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Dialog_NoActionBar);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(mResLayoutId, container);
        if (mListener != null) {
            mListener.setupView(new ViewHelper(view), this);
        }
        return view;
    }

    @Override
    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance()) {
            getDialog().setDismissMessage(null);
        }
        super.onDestroyView();
    }

    private void unpackBundle() {
        Bundle args = getArguments();
        if (args == null) {
            return;
        }
        mResLayoutId = args.getInt("layout");
    }

    public void setDismissListener(DialogInterface.OnDismissListener dismissListener) {
        this.mDismissListener = dismissListener;
    }

    /**
     *
     */
    public interface InitViewListener {
        /**
         * @param view
         * @param dialogFragmentHelper
         */
        void setupView(ViewHelper view, DialogFragmentHelper dialogFragmentHelper);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mDismissListener != null) {
            mDismissListener.onDismiss(dialog);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        if (getDialog() == null) {  // Returns mDialog
            // Tells DialogFragment to not use the fragment as a dialog, and so won't try to use mDialog
            setShowsDialog(false);
        }
        super.onActivityCreated(savedInstanceState);
    }
}
