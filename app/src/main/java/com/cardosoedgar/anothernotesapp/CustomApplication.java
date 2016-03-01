package com.cardosoedgar.anothernotesapp;

import android.app.Application;

import com.cardosoedgar.anothernotesapp.dagger.AppComponent;
import com.cardosoedgar.anothernotesapp.dagger.AppModule;
import com.cardosoedgar.anothernotesapp.dagger.DaggerAppComponent;
import com.cardosoedgar.anothernotesapp.dagger.DataModule;

/**
 * Created by edgarcardoso on 3/1/16.
 */
public class CustomApplication extends Application{

    private AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        String database = getString(R.string.database);

        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .dataModule(new DataModule(database))
                .build();
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }
}
