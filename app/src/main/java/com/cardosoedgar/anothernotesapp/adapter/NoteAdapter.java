package com.cardosoedgar.anothernotesapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cardosoedgar.anothernotesapp.interfaces.BottomSheetInterface;
import com.cardosoedgar.anothernotesapp.NoteActivity;
import com.cardosoedgar.anothernotesapp.R;
import com.cardosoedgar.anothernotesapp.model.Note;

import java.util.List;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;

/**
 * Created by edgarcardoso on 3/1/16.
 */
public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    private final int EDIT_NOTE = 2;

    List<Note> noteList;
    Context context;

    public NoteAdapter(Context context, List<Note> noteList) {
        this.context = context;
        this.noteList = noteList;
    }

    @Override
    public NoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_layout,parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NoteViewHolder holder, int position) {
        Note note = noteList.get(position);
        holder.setNote(note);
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        @Bind(R.id.title)
        TextView title;
        @Bind(R.id.content) TextView content;

        @BindString(R.string.new_note) String stringNote;
        @BindString(R.string.no_title) String noTitle;

        Note note;

        public NoteViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(v.getContext(), NoteActivity.class);
            intent.putExtra(stringNote, note.getId());
            ((Activity)context).startActivityForResult(intent, EDIT_NOTE);
        }

        public void setNote(Note note) {
            this.note = note;
            setViews();
        }

        private void setViews() {
            String stringTitle = note.getTitle();
            title.setText(stringTitle.isEmpty() ? noTitle : stringTitle);
            content.setText(note.getContent());
        }

        @Override
        public boolean onLongClick(View v) {
            BottomSheetInterface bottomSheet = (BottomSheetInterface) context;
            int position = noteList.indexOf(note);
            bottomSheet.showBottomSheet(position);
            return true;
        }
    }
}
