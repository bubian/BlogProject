package com.pds.ui.act;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import com.pds.ui.R;
import com.pds.ui.view.graph.SuitLines;
import com.pds.ui.view.graph.Unit;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    SuitLines suitLines;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_main);
        suitLines =  findViewById(R.id.suitlines);
        onBtnClick2(null);
        onBtnClick101(null);
    }

    public void onBtnClick(View view) {
        suitLines.anim();
    }

    private boolean enable;
    public void onBtnClick1(View view) {
        suitLines.setCoverLine(enable = !enable);
    }

    public void onBtnClick13(View view) {
        int[] colors = new int[2];
        colors[0] = color[new SecureRandom().nextInt(7)];
        colors[1] = Color.WHITE;
        suitLines.setDefaultOneLineColor(colors);
    }

    private int curCount = 1;

    public void onBtnClick2(View view) {
        suitLines.setXySize(textSize = 8);
        init(curCount = 1);
    }

    public void onBtnClick3(View view) {
        init(++curCount);
    }

    public void onBtnClick4(View view) {
        if (curCount <= 1) {
            curCount = 1;
        }
        init(--curCount);
    }

    public void onBtnClick5(View view) {
        suitLines.setLineForm(!suitLines.isLineFill());
    }


    public void onBtnClick6(View view) {
        suitLines.setLineStyle(suitLines.isLineDashed()?SuitLines.SOLID:SuitLines.DASHED);
    }

    public void onBtnClick7(View view) {
        suitLines.setLineType(suitLines.getLineType() == SuitLines.CURVE ? SuitLines.SEGMENT : SuitLines.CURVE);
    }

    public void onBtnClick8(View view) {
        suitLines.disableEdgeEffect();
    }

    public void onBtnClick9(View view) {
        suitLines.setEdgeEffectColor(color[new SecureRandom().nextInt(7)]);
    }

    public void onBtnClick10(View view) {
        suitLines.setXyColor(color[new SecureRandom().nextInt(7)]);
    }

    private float textSize = 8;

    public void onBtnClick11(View view) {
        suitLines.setXySize(++textSize);
    }

    public void onBtnClick12(View view) {
        if (textSize < 6) {
            textSize = 6;
        }
        suitLines.setXySize(--textSize);
    }
    public void onBtnClick14(View view) {
        suitLines.disableClickHint();
    }
    public void onBtnClick15(View view) {
        suitLines.setHintColor(color[new SecureRandom().nextInt(7)]);
    }

    private int[] color = {Color.RED, Color.GRAY, Color.BLACK, Color.BLUE, 0xFFF76055, 0xFF9B3655, 0xFFF7A055};

    public void init(int count) {
        if (count <= 0) {
            count = 0;
        }
        if (count == 1) {
            List<Unit> lines = new ArrayList<>();
            lines.add(new Unit(0, "dd"));
            lines.add(new Unit(30, "dd"));
            lines.add(new Unit(42, "dd"));
            lines.add(new Unit(10, "dd"));
            lines.add(new Unit(0, "dd"));
            lines.add(new Unit(-10, "dd"));
            lines.add(new Unit(35, "dd"));
            lines.add(new Unit(15, "dd"));
            lines.add(new Unit(0, "dd"));
            lines.add(new Unit(-6, "dd"));
            lines.add(new Unit(6, "dd"));
            lines.add(new Unit(2, "dd"));
            lines.add(new Unit(12, "dd"));
            suitLines.feedWithAnim(lines);
            return;
        }

        SuitLines.LineBuilder builder = new SuitLines.LineBuilder();
        for (int j = 0; j < count; j++) {
            List<Unit> lines = new ArrayList<>();
            for (int i = 0; i < 50; i++) {
                lines.add(new Unit(new SecureRandom().nextInt(128), "" + i));
            }
            builder.add(lines, new int[]{color[new SecureRandom().nextInt(7)], Color.WHITE});
        }
        builder.build(suitLines, true);

    }

    private boolean setShowYGrid;
    public void onBtnClick101(View view) {
        suitLines.setShowYGrid(setShowYGrid = !setShowYGrid);
    }
}
