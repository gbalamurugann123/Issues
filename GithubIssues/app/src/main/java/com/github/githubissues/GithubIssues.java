package com.github.githubissues;


import android.app.Application;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Configuration;
import com.github.githubissues.models.Comment;
import com.github.githubissues.models.Issue;

/**
 * Created by Ponns on 4/2/2017.
 */

public class GithubIssues extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Configuration dbConfiguration = new Configuration.Builder(this)
                .setDatabaseName("GithubIssues.db")
                .addModelClass(Issue.class)
                .addModelClass(Comment.class)
                .create();

        ActiveAndroid.initialize(dbConfiguration);
    }
}
