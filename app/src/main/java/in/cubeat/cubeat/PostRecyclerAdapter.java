package in.cubeat.cubeat;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by SAYAN on 09-07-2016.
 */
public class PostRecyclerAdapter extends RecyclerView.Adapter<PostRecyclerAdapter.Holder> {
    Context context;
    LayoutInflater inflater;
    ArrayList<PostPreview> data;

    public PostRecyclerAdapter(Context context, ArrayList<PostPreview> data){
        this.context=context;
        this.data=data;
        this.inflater=LayoutInflater.from(context);
    }

    @Override
    public PostRecyclerAdapter.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(inflater.inflate(R.layout.post_list_item,parent,false));
    }

    @Override
    public void onBindViewHolder(PostRecyclerAdapter.Holder holder, int position) {
        holder.title.setText(data.get(position).title);
        holder.author.setText(data.get(position).author);
        holder.date.setText(data.get(position).date);
        String url = data.get(position).imageURL;
        holder.excerpt.setText(Html.fromHtml(data.get(position).excerpt));
        if(!url.equals(""))
            Glide.with(context)
                    .load(url)
                    .fitCenter()
                    .into(holder.image);
        else
            Glide.with(context)
                    .load(R.drawable.thumbnail_default)
                    .into(holder.image);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title;
        ImageView image;
        TextView author;
        TextView date;
        TextView excerpt;
        public Holder(View itemView) {
            super(itemView);
            title= (TextView) itemView.findViewById(R.id.list_title);
            image= (ImageView) itemView.findViewById(R.id.list_picture);
            author=(TextView) itemView.findViewById(R.id.list_author);
            date=(TextView) itemView.findViewById(R.id.list_date);
            excerpt=(TextView) itemView.findViewById(R.id.list_excerpt);

            itemView.findViewById(R.id.list_card).setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.list_card:
                    Intent i1=new Intent(context,ViewPostActivity.class);
                    i1.putExtra(BackendHelper.POST_ID_KEY_INTENT,data.get(getAdapterPosition()).ID);
                    context.startActivity(i1);
                    break;
            }
        }
    }
}
