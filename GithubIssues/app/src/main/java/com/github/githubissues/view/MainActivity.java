package com.github.githubissues.view;

import android.app.ProgressDialog;
import android.content.Intent;
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
import com.github.githubissues.models.Issue;
import com.github.githubissues.utils.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements RecyclerViewAdapter.OnItemClickListener {

    SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView myRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerViewAdapter myRecyclerViewAdapter;
    private ArrayList<Issue> issueList = new ArrayList<Issue>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        myRecyclerViewAdapter = new RecyclerViewAdapter(this, issueList);
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

            RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
            String url = Constant.CRASHLYTIC_API;
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
        List<Issue> ls = new Select().from(Issue.class).execute();
        issueList.clear();
        issueList.addAll(ls);
        //Collections.reverse(issueList);
        myRecyclerViewAdapter.notifyDataSetChanged();

        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onItemClick(RecyclerViewAdapter.ItemHolder item, int position) {

        //Toast.makeText(getApplicationContext(), issueList.get(position).comments +"",Toast.LENGTH_LONG).show();
        Intent intent = new Intent(getApplicationContext(), CommentActivity.class);
        intent.putExtra("ISSUE_TITLE", issueList.get(position).title);
        intent.putExtra("ISSUE_ID", issueList.get(position).issue_id);
        intent.putExtra("COMMENT_URI", issueList.get(position).comments);
        startActivity(intent);
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
            new Delete().from(Issue.class).execute();
            try {
                for (int i = 0; i < resp.length(); i++) {
                    JSONObject objIssue = (JSONObject) resp.get(i);
                    Issue issue = new Issue();
                    issue.issue_id = objIssue.getInt(Constant.ID_TEXT);
                    issue.title = objIssue.getString(Constant.TITLE_TEXT);

                    JSONObject userObj = objIssue.getJSONObject(Constant.USER_TEXT);
                    issue.user = userObj.getString(Constant.USER_LOGIN_TEXT);
                    issue.avatar = userObj.getString(Constant.USER_AVATAR_URL_TEXT);

                    issue.comments = objIssue.getString(Constant.COMMENTS_URL_TEXT);
                    issue.body = objIssue.getString(Constant.BODY_TEXT);
                    issue.updated_at = objIssue.getString(Constant.UPDATED_AT_TEXT);
                    issue.save();
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
