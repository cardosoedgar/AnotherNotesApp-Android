package com.cardosoedgar.anothernotesapp.dagger;

import com.cardosoedgar.anothernotesapp.MainActivity;
import com.cardosoedgar.anothernotesapp.NoteActivity;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by edgarcardoso on 3/1/16.
 */
@Singleton
@Component(modules={
        AppModule.class,
        DataModule.class
})
public interface AppComponent {
    void inject(MainActivity activity);
    void inject(NoteActivity activity);
}
