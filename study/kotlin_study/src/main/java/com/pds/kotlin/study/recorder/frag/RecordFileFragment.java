package com.pds.kotlin.study.recorder.frag;

import android.os.Bundle;
import android.os.FileObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pds.kotlin.study.R;
import com.pds.kotlin.study.recorder.RecordFileAdapter;

public class RecordFileFragment extends Fragment {
    private static final String ARG_POSITION = "position";

    private RecordFileAdapter mFileViewerAdapter;

    public static RecordFileFragment newInstance(int position) {
        RecordFileFragment f = new RecordFileFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        observer.startWatching();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_file_viewer, container, false);

        RecyclerView mRecyclerView = v.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        llm.setReverseLayout(true);
        llm.setStackFromEnd(true);

        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mFileViewerAdapter = new RecordFileAdapter(getActivity(), R.layout.card_view);
        mRecyclerView.setAdapter(mFileViewerAdapter);

        return v;
    }

    private FileObserver observer = new FileObserver(android.os.Environment.getExternalStorageDirectory().toString() + "/SoundRecorder") {
        @Override
        public void onEvent(int event, String file) {
            if(event == FileObserver.DELETE){
                String filePath = android.os.Environment.getExternalStorageDirectory().toString() + "/SoundRecorder" + file + "]";
            }
        }
    };
}




