package com.cardosoedgar.anothernotesapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.EditText;

import com.cardosoedgar.anothernotesapp.model.Note;
import com.jakewharton.rxbinding.widget.RxTextView;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import io.realm.Realm;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

public class NoteActivity extends AppCompatActivity {

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.editTextTitle) EditText editTextTitle;
    @Bind(R.id.editTextContent) EditText editTextContent;
    @BindString(R.string.new_note) String stringNote;

    @Inject Realm realm;

    Note note;
    CompositeSubscription subscriptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        ((CustomApplication) getApplication()).getAppComponent().inject(this);
        ButterKnife.bind(this);
        setupToolbar();

        checkIntentForNote();
        addSubscriptions();
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void checkIntentForNote() {
        int id = getIntent().getIntExtra(stringNote, 0);
        if(id == 0) {
            createNewNoteFromRealm();
            return;
        }
        setNote(id);
        setCursorToEnd();
    }

    private void setNote(int id) {
        note = realm.where(Note.class).equalTo("id", id).findFirst();
        editTextTitle.setText(note.getTitle());
        editTextContent.setText(note.getContent());
    }

    private void setCursorToEnd() {
        editTextTitle.setSelection(editTextTitle.getText().length());
        editTextContent.setSelection(editTextContent.getText().length());
    }

    private void createNewNoteFromRealm() {
        realm.beginTransaction();
        note = realm.createObject(Note.class);
        realm.commitTransaction();
    }

    private void addSubscriptions() {
        subscriptions = new CompositeSubscription();
        subscriptions.add(RxTextView.textChanges(editTextTitle)
                .debounce(5, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .distinctUntilChanged()
                .filter((text) -> text.length() > 0)
                .subscribe((text) -> {
                    save();
                }));

        subscriptions.add(RxTextView.textChanges(editTextContent)
                .debounce(5, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .distinctUntilChanged()
                .filter((text) -> text.length() > 0)
                .subscribe((text) -> {
                    save();
                }));
    }

    private void save() {
        realm.beginTransaction();
        if(!(note.getId() > 0)) {
            Number maxId = realm.where(Note.class).max("id");
            int id = maxId == null ? 1 : maxId.intValue() + 1;
            note.setId(id);
        }

        note.setTitle(editTextTitle.getText().toString());
        note.setContent(editTextContent.getText().toString());
        realm.commitTransaction();
    }

    private void finishEditing() {
        updateNoteBeforeFinish();
        if(!(note.getId() > 0)) {
            removeFromRealm();
            finish();
            return;
        }

        Intent intent = getIntent();
        intent.putExtra(stringNote, note.getId());
        setResult(RESULT_OK, intent);
        finish();
    }

    private void removeFromRealm() {
        realm.beginTransaction();
        note.removeFromRealm();
        realm.commitTransaction();
    }

    private void updateNoteBeforeFinish() {
        String title = editTextTitle.getText().toString();
        String content = editTextContent.getText().toString();
        if(!title.isEmpty() || !content.isEmpty()) {
            save();
        }
    }

    @Override
    public void onBackPressed() {
        finishEditing();
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home) {
            finishEditing();
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        subscriptions.unsubscribe();
        super.onDestroy();
    }
}
