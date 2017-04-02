package com.github.githubissues.adapter;

/**
 * Created by Ponns on 4/2/2017.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.githubissues.R;
import com.github.githubissues.models.Issue;
import com.github.githubissues.utils.Utility;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ItemHolder>{

    private List<Issue> issueList;
    private OnItemClickListener onItemClickListener;
    private LayoutInflater layoutInflater;
    Context mContext;
    public RecyclerViewAdapter(Context context, List<Issue> issueList){
        layoutInflater = LayoutInflater.from(context);
        this.issueList = issueList;
        this.mContext = context;
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.layout_item, parent, false);
        return new ItemHolder(itemView, this);
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {
        Issue issue = issueList.get(position);
        holder.setItem(issue,mContext);
    }

    @Override
    public int getItemCount() {
        return issueList.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        onItemClickListener = listener;
    }

    public OnItemClickListener getOnItemClickListener(){
        return onItemClickListener;
    }

    public interface OnItemClickListener{
        public void onItemClick(ItemHolder item, int position);
    }

    public static class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private RecyclerViewAdapter parent;
        ImageView imgUser;
        TextView textUser,textTitle,textDesc,textDate;

        public ItemHolder(View itemView, RecyclerViewAdapter parent) {
            super(itemView);
            itemView.setOnClickListener(this);
            this.parent = parent;
            imgUser = (ImageView) itemView.findViewById(R.id.image_user);
            textUser = (TextView) itemView.findViewById(R.id.txt_user);
            textTitle = (TextView) itemView.findViewById(R.id.txt_title);
            textDesc = (TextView) itemView.findViewById(R.id.txt_desc);
            textDate = (TextView) itemView.findViewById(R.id.txt_date);
        }

        public void setItem(Issue issue,Context context){
            Glide.with(context)
                    .load(issue.avatar)
                    .placeholder(R.mipmap.ic_launcher).dontAnimate()
                    .fitCenter()
                    .into(imgUser);

            textUser.setText(issue.user);
            textDesc.setText(issue.body);
            textTitle.setText(issue.title);
            textDate.setText(Utility.dateFormat(issue.updated_at));
        }

        /*public CharSequence getItemText(){
            //return textItemText.getText();
        }*/

        @Override
        public void onClick(View v) {
            final OnItemClickListener listener = parent.getOnItemClickListener();
            if(listener != null){
                listener.onItemClick(this, getAdapterPosition());
            }
        }
    }
}