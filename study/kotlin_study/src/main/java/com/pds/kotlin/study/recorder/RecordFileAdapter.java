package com.pds.kotlin.study.recorder;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.text.format.DateUtils;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import com.pds.base.adapter.ListAdapter;
import com.pds.base.holder.BaseViewHolder;
import com.pds.kotlin.study.R;
import com.pds.kotlin.study.recorder.core.RecordDBHelper;
import com.pds.kotlin.study.recorder.frag.RecordPlayFragment;

import java.io.File;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;

public class RecordFileAdapter<T> extends ListAdapter<RecordEntity>
    implements DatabaseChangedListener {

    private static final String LOG_TAG = "FileViewerAdapter";

    private RecordDBHelper mDatabase;

    public RecordFileAdapter(Context context, int layoutId) {
        super(context, layoutId);
        mDatabase = new RecordDBHelper(context);
        RecordDBHelper.setOnDatabaseChangedListener(this);
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view, parent, false);
        return new BaseViewHolder(itemView);
    }

    @Override
    public void convert(BaseViewHolder holder, int position, RecordEntity item) {
        if (null == item){
            item = mDatabase.getItemAt(position);
        }
        long itemDuration = item.getLength();
        long minutes = TimeUnit.MILLISECONDS.toMinutes(itemDuration);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(itemDuration) - TimeUnit.MINUTES.toSeconds(minutes);
        String date = DateUtils.formatDateTime(
                getContext(),
                item.getTime(),
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NUMERIC_DATE | DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_YEAR
        );
        RecordEntity finalItem = item;
        holder.setText(R.id.file_name_text,item.getName())
                .setText(R.id.file_length_text,String.format("%02d:%02d", minutes, seconds))
                .setText(R.id.file_date_added_text,date)
                .setOnClickListener(R.id.card_view,(v -> {
                    try {
                        RecordPlayFragment playbackFragment =
                                new RecordPlayFragment().newInstance(finalItem);

                        FragmentTransaction transaction = ((FragmentActivity) getContext())
                                .getSupportFragmentManager()
                                .beginTransaction();

                        playbackFragment.show(transaction, "dialog_playback");

                    } catch (Exception e) {
                        Log.e(LOG_TAG, "exception", e);
                    }
                }))
                .setOnLongClickListener(R.id.card_view,(v -> {
                    ArrayList<String> entity = new ArrayList<>();
                    Context context = getContext();
                    entity.add(context.getString(R.string.dialog_file_share));
                    entity.add(context.getString(R.string.dialog_file_rename));
                    entity.add(context.getString(R.string.dialog_file_delete));

                    final CharSequence[] items = entity.toArray(new CharSequence[entity.size()]);

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(context.getString(R.string.dialog_title_options));
                    builder.setItems(items, (dialog, item1) -> {
                        if (item1 == 0) { shareFileDialog(holder.getPosition());
                        } else if (item1 == 1) { renameFileDialog(holder.getPosition());
                        } else if (item1 == 2) { deleteFileDialog(holder.getPosition());
                        }
                    });
                    builder.setCancelable(true);
                    builder.setNegativeButton(context.getString(R.string.dialog_action_cancel),
                            (dialog, id) -> dialog.cancel());

                    AlertDialog alert = builder.create();
                    alert.show();
                    return false;

                }));


    }

    @Override
    public int getItemCount() {
        return mDatabase.getCount();
    }

    public RecordEntity getItem(int position) {
        return mDatabase.getItemAt(position);
    }

    @Override
    public void onNewDatabaseEntryAdded() {
        notifyItemInserted(getItemCount() - 1);
    }

    @Override
    public void onDatabaseEntryRenamed() { }

    public void remove(int position) {
        File file = new File(getItem(position).getFilePath());
        file.delete();

        mDatabase.removeItemWithId(getItem(position).getId());
        notifyItemRemoved(position);
    }

    public void rename(int position, String name) {
        String mFilePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFilePath += "/SoundRecorder/" + name;
        File f = new File(mFilePath);

        if (f.exists() && !f.isDirectory()) {
        } else {
            File oldFilePath = new File(getItem(position).getFilePath());
            oldFilePath.renameTo(f);
            mDatabase.renameItem(getItem(position), name, mFilePath);
            notifyItemChanged(position);
        }
    }

    public void shareFileDialog(int position) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(getItem(position).getFilePath())));
        shareIntent.setType("audio/mp4");
        getContext().startActivity(Intent.createChooser(shareIntent, getContext().getText(R.string.send_to)));
    }

    public void renameFileDialog (final int position) {
        AlertDialog.Builder renameFileBuilder = new AlertDialog.Builder(getContext());

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.dialog_rename_file, null);

        final EditText input = view.findViewById(R.id.new_name);

        renameFileBuilder.setTitle(getContext().getString(R.string.dialog_title_rename));
        renameFileBuilder.setCancelable(true);
        renameFileBuilder.setPositiveButton(getContext().getString(R.string.dialog_action_ok),
                (dialog, id) -> {
                    try {
                        String value = input.getText().toString().trim() + ".mp4";
                        rename(position, value);

                    } catch (Exception e) {
                        Log.e(LOG_TAG, "exception", e);
                    }

                    dialog.cancel();
                });
        renameFileBuilder.setNegativeButton(getContext().getString(R.string.dialog_action_cancel),
                (dialog, id) -> dialog.cancel());

        renameFileBuilder.setView(view);
        AlertDialog alert = renameFileBuilder.create();
        alert.show();
    }

    public void deleteFileDialog (final int position) {
        AlertDialog.Builder confirmDelete = new AlertDialog.Builder(getContext());
        confirmDelete.setTitle(getContext().getString(R.string.dialog_title_delete));
        confirmDelete.setMessage(getContext().getString(R.string.dialog_text_delete));
        confirmDelete.setCancelable(true);
        confirmDelete.setPositiveButton(getContext().getString(R.string.dialog_action_yes),
                (dialog, id) -> {
                    try {
                        remove(position);

                    } catch (Exception e) {
                        Log.e(LOG_TAG, "exception", e);
                    }

                    dialog.cancel();
                });
        confirmDelete.setNegativeButton(getContext().getString(R.string.dialog_action_no),
                (dialog, id) -> dialog.cancel());

        AlertDialog alert = confirmDelete.create();
        alert.show();
    }
}
