package com.pds.ui.jsonviewer.view;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.pds.ui.R;
import com.pds.ui.dialog.BaseDialogFragment;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/12/10 1:15 PM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
public class JsonDialog extends BaseDialogFragment implements View.OnClickListener {

    private String mKeyValue;
    private ClipboardManager mClipboard;
    private String mKey;
    private String mValue;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Dialog_NoActionBar);
        mKeyValue = getArguments().getString("json_key_value");
        mClipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        String[] ts = mKeyValue.split(":");
        if (ts.length > 1){
            mKey = ts[0];
            mValue = ts[1];
        }else {
            mKey = ts[0];
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_json, container);
        view.findViewById(R.id.all).setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.all) {
            copyToClipboard(mKeyValue);
        } else if (id == R.id.key) {
            copyToClipboard(mKey);
        } else if (id == R.id.value) {
            copyToClipboard(mValue);
        }
        dismiss();
    }

    private void copyToClipboard(String s){
        ClipData clip = ClipData.newPlainText(s, s);
        mClipboard.setPrimaryClip(clip);
        Toast.makeText(getContext(),"复制成功", Toast.LENGTH_SHORT);
    }
}
