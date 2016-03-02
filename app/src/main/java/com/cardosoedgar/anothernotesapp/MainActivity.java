package com.cardosoedgar.anothernotesapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.cardosoedgar.anothernotesapp.adapter.NoteAdapter;
import com.cardosoedgar.anothernotesapp.interfaces.BottomSheetInterface;
import com.cardosoedgar.anothernotesapp.model.Note;
import com.flipboard.bottomsheet.BottomSheetLayout;
import com.flipboard.bottomsheet.commons.MenuSheetView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;

public class MainActivity extends AppCompatActivity implements BottomSheetInterface {

    private final int NEW_NOTE = 1;

    @BindString(R.string.new_note) String stringNote;

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.recyclerView) RecyclerView recyclerView;
    @Bind(R.id.bottomsheet) BottomSheetLayout bottomSheet;

    @Inject Realm realm;

    NoteAdapter noteAdapter;
    List<Note> noteList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((CustomApplication) getApplication()).getAppComponent().inject(this);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        setupRecyclerView();
        fetchRealmForNotes();
    }

    private void fetchRealmForNotes() {
        List<Note> notes = realm.where(Note.class).findAll();
        if(notes == null) {
            return;
        }
        noteList.addAll(notes);
        noteAdapter.notifyItemRangeInserted(0, notes.size());
    }

    private void setupRecyclerView() {
        noteList = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        noteAdapter = new NoteAdapter(this, noteList);
        recyclerView.setAdapter(noteAdapter);
    }

    @OnClick(R.id.fab)
    public void addNewNote() {
        Intent intent = new Intent(this, NoteActivity.class);
        startActivityForResult(intent, NEW_NOTE);
    }

    private void deleteNote(int itemId) {
        Note note = noteList.get(itemId);
        noteList.remove(note);
        noteAdapter.notifyItemRemoved(itemId);
        removeFromRealm(note);
    }

    private void removeFromRealm(Note note) {
        realm.beginTransaction();
        note.removeFromRealm();
        realm.commitTransaction();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK) {
            int id = data.getIntExtra(stringNote, 0);
            Note newNote = realm.where(Note.class).equalTo("id",id).findFirst();
            if(requestCode != NEW_NOTE) {
                noteAdapter.notifyDataSetChanged();
                return;
            }
            noteList.add(newNote);
            noteAdapter.notifyItemInserted(noteList.size()-1);
        }
    }

    @Override
    public void showBottomSheet(int position) {
        bottomSheet.setPeekOnDismiss(true);
        MenuSheetView menuSheetView = new MenuSheetView(this, MenuSheetView.MenuType.LIST, "",
                item -> {
                    if (bottomSheet.isSheetShowing()) {
                        bottomSheet.dismissSheet();
                    }
                    deleteNote(position);
                    return true;
                });
        menuSheetView.inflateMenu(R.menu.bottom_sheet);
        bottomSheet.showWithSheetView(menuSheetView);
    }
}
