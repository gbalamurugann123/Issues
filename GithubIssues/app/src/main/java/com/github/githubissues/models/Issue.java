package com.github.githubissues.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;


@Table(name = "Issue")
public class Issue extends Model {

    @Column(name = "issue_id")
    public long issue_id;

    @Column(name = "title")
    public String title;

    @Column(name = "user")
    public String user;

    @Column(name = "avatar")
    public String avatar;

    @Column(name = "comments")
    public String comments;

    @Column(name = "body")
    public String body;

    @Column(name = "updated_at")
    public String updated_at;
}
