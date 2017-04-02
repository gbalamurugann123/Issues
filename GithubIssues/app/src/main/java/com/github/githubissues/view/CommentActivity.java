package com.github.githubissues.view;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.github.githubissues.R;
import com.github.githubissues.adapter.RecyclerViewAdapter;
import com.github.githubissues.constants.Constant;
import com.github.githubissues.models.Comment;
import com.github.githubissues.models.Issue;
import com.github.githubissues.utils.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CommentActivity extends AppCompatActivity implements RecyclerViewAdapter.OnItemClickListener {

    SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView myRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerViewAdapter myRecyclerViewAdapter;
    private ArrayList<Issue> commentList = new ArrayList<Issue>();

    long ISSUE_ID;
    String COMMENT_URI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ISSUE_ID = getIntent().getLongExtra("ISSUE_ID",0);
        COMMENT_URI = getIntent().getStringExtra("COMMENT_URI");

        getSupportActionBar().setTitle(getIntent().getStringExtra("ISSUE_TITLE"));  // provide compatibility to all the versions



        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swiperefreshlayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadView();
            }
        });

        myRecyclerView = (RecyclerView)findViewById(R.id.myrecyclerview);
        linearLayoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        myRecyclerViewAdapter = new RecyclerViewAdapter(this, commentList);
        myRecyclerViewAdapter.setOnItemClickListener(this);
        myRecyclerView.setAdapter(myRecyclerViewAdapter);
        myRecyclerView.setLayoutManager(linearLayoutManager);

        setAdapter();
        loadView();
    }

    public void loadView()
    {
        if(Utility.isNetworkAvailable(getApplicationContext()))
        {
            final ProgressDialog pDialog = new ProgressDialog(this);
            pDialog.setMessage("Loading...");
            pDialog.show();

            RequestQueue queue = Volley.newRequestQueue(CommentActivity.this);
            String url = COMMENT_URI;
            JsonArrayRequest jsObjRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    pDialog.dismiss();
                    new AsyncTaskRunner().execute(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    pDialog.dismiss();
                }
            });
            queue.add(jsObjRequest);
        }
        else
        {
            swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
        }
    }

    public void setAdapter()
    {
        List<Comment> ls = new Select().from(Comment.class).where("comment_id = ?",ISSUE_ID).execute();
        commentList.clear();
        for(int i = 0; i < ls.size(); i++ )
        {
            Issue issue = new Issue();
            issue.title = "";
            issue.user = ls.get(i).user;
            issue.avatar = ls.get(i).avatar;
            issue.body = ls.get(i).body;
            issue.updated_at = ls.get(i).updated_at;
            commentList.add(issue);
        }
        //issueList.addAll(ls);
        //Collections.reverse(commentList);
        myRecyclerViewAdapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onItemClick(RecyclerViewAdapter.ItemHolder item, int position) {

        //Toast.makeText(getApplicationContext(), issueList.get(position).comments +"",Toast.LENGTH_LONG).show();
    }


    private class AsyncTaskRunner extends AsyncTask<JSONArray, String, String> {

        private JSONArray resp;

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(JSONArray... params) {
            resp = params[0];
            //ActiveAndroid.beginTransaction();
            new Delete().from(Comment.class).where("comment_id = ?",ISSUE_ID).execute();
            try {
                for (int i = 0; i < resp.length(); i++) {
                    JSONObject objComment = (JSONObject) resp.get(i);
                    Comment comment = new Comment();
                    comment.comment_id = ISSUE_ID;
                    //comment.title = "";

                    JSONObject userObj = objComment.getJSONObject(Constant.USER_TEXT);
                    comment.user = userObj.getString(Constant.USER_LOGIN_TEXT);
                    comment.avatar = userObj.getString(Constant.USER_AVATAR_URL_TEXT);

                    //comment.comments = objIssue.getString(Constant.COMMENTS_URL_TEXT);
                    comment.body = objComment.getString(Constant.BODY_TEXT);
                    comment.updated_at = objComment.getString(Constant.UPDATED_AT_TEXT);
                    comment.save();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String result) {
            //ActiveAndroid.endTransaction();
            setAdapter();
        }
    }
}
