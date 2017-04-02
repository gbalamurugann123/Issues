package com.github.githubissues.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;


@Table(name = "Comment")
public class Comment extends Model {

    @Column(name = "comment_id")
    public long comment_id;

    @Column(name = "user")
    public String user;

    @Column(name = "avatar")
    public String avatar;

    @Column(name = "body")
    public String body;

    @Column(name = "updated_at")
    public String updated_at;
}
