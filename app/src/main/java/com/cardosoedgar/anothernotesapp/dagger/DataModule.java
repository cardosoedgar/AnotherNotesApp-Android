package com.cardosoedgar.anothernotesapp.dagger;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by edgarcardoso on 3/1/16.
 */
@Module
public class DataModule {

    String database;

    public DataModule(String database) {
        this.database = database;
    }

//    @Provides
//    @Singleton
//    RealmConfiguration providesRealmConfiguration(Application application) {
//        return new RealmConfiguration.Builder(application)
//                .name(database)
//                .build();
//    }

    @Provides
    @Singleton
    Realm providesRealm(Application application) {
//        return Realm.getInstance(configuration);
        RealmConfiguration realmConfig = new RealmConfiguration.Builder(application).build();
        Realm.setDefaultConfiguration(realmConfig);
        return Realm.getDefaultInstance();
    }
}
