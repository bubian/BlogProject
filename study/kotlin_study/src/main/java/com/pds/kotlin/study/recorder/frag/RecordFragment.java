package com.pds.kotlin.study.recorder.frag;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.melnykov.fab.FloatingActionButton;
import com.pds.kotlin.study.R;
import com.pds.kotlin.study.recorder.core.RecordService;

import java.io.File;

public class RecordFragment extends Fragment {
    private static final String ARG_POSITION = "position";

    private FloatingActionButton mRecordButton = null;
    private Button mPauseButton = null;

    private TextView mRecordingPrompt;
    private int mRecordPromptCount = 0;

    private boolean mStartRecording = true;
    private boolean mPauseRecording = true;

    private Chronometer mChronometer = null;
    private long timeWhenPaused = 0;

    public static RecordFragment newInstance(int position) {
        RecordFragment f = new RecordFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View recordView = inflater.inflate(R.layout.fragment_record, container, false);
        mChronometer = recordView.findViewById(R.id.chronometer);
        mRecordingPrompt = recordView.findViewById(R.id.recording_status_text);
        mRecordButton = recordView.findViewById(R.id.btnRecord);
//        mRecordButton.setColorNormal(getResources().getColor(R.color.background_tab_pressed));
        mRecordButton.setColorPressed(getResources().getColor(R.color.colorAccent));
        mRecordButton.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[] { Manifest.permission.RECORD_AUDIO }, 10);
            } else {
                onRecord(mStartRecording);
            }
            mStartRecording = !mStartRecording;
        });
        mPauseButton = recordView.findViewById(R.id.btnPause);
        mPauseButton.setVisibility(View.GONE);
        mPauseButton.setOnClickListener(v -> {
            onPauseRecord(mPauseRecording);
            mPauseRecording = !mPauseRecording;
        });
        return recordView;
    }

    @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                                     @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 10) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onRecord(mStartRecording);
            } else {
                //User denied Permission.
            }
        }
    }

    private void onRecord(boolean start){
        Intent intent = new Intent(getActivity(), RecordService.class);
        if (start) {
            mRecordButton.setImageResource(R.drawable.ic_media_stop);
            //mPauseButton.setVisibility(View.VISIBLE);
            File folder = new File(Environment.getExternalStorageDirectory() + "/SoundRecorder");
            if (!folder.exists()) {
                folder.mkdir();
            }

            mChronometer.setBase(SystemClock.elapsedRealtime());
            mChronometer.start();
            mChronometer.setOnChronometerTickListener(chronometer -> {
                if (mRecordPromptCount == 0) { mRecordingPrompt.setText(getString(R.string.record_in_progress) + ".");
                } else if (mRecordPromptCount == 1) { mRecordingPrompt.setText(getString(R.string.record_in_progress) + "..");
                } else if (mRecordPromptCount == 2) { mRecordingPrompt.setText(getString(R.string.record_in_progress) + "...");
                    mRecordPromptCount = -1;
                }
                mRecordPromptCount++;
            });

            getActivity().startService(intent);
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            mRecordingPrompt.setText(getString(R.string.record_in_progress) + ".");
            mRecordPromptCount++;

        } else {
            mRecordButton.setImageResource(R.drawable.ic_mic_white_36dp);
            //mPauseButton.setVisibility(View.GONE);
            mChronometer.stop();
            mChronometer.setBase(SystemClock.elapsedRealtime());
            timeWhenPaused = 0;
            mRecordingPrompt.setText(getString(R.string.record_prompt));

            getActivity().stopService(intent);
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    private void onPauseRecord(boolean pause) {
        if (pause) {
            mPauseButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_media_play ,0 ,0 ,0);
            mRecordingPrompt.setText(getString(R.string.resume_recording_button).toUpperCase());
            timeWhenPaused = mChronometer.getBase() - SystemClock.elapsedRealtime();
            mChronometer.stop();
        } else {
            mPauseButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_media_pause ,0 ,0 ,0);
            mRecordingPrompt.setText(getString(R.string.pause_recording_button).toUpperCase());
            mChronometer.setBase(SystemClock.elapsedRealtime() + timeWhenPaused);
            mChronometer.start();
        }
    }
}