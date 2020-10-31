package com.pds.ui.act;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pds.ui.R;
import com.pds.ui.dialog.DialogFragmentHelper;

/**
 * @author: pengdaosong
 * CreateTime:  2020-05-11 17:47
 * Email：pengdaosong@medlinker.com
 * Description:
 */
public class DialogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_activity);
    }

    public void dialogShow(View view){
        shareDialog(view);
    }

    /**
     * 分享弹窗 UI请自行填充
     * @param view
     */
    private void shareDialog(View view){
        DialogFragmentHelper dialogFragmentHelper = DialogFragmentHelper.newInstance(R.layout.dialog_share)
                .setListener((view1, dialogFragmentHelper1) -> {
                    RecyclerView recyclerView = (RecyclerView) view;
                    recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false));
                });
        dialogFragmentHelper.show(getSupportFragmentManager(),"share");
    }
}
