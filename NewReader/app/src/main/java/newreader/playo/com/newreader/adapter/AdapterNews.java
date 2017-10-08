package newreader.playo.com.newreader.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;

import android.widget.LinearLayout;


import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import newreader.playo.com.newreader.R;
import newreader.playo.com.newreader.model.Hits;

public class AdapterNews extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private List<Hits> items = new ArrayList<>();
    private Context mContext;


    private OnItemClickListener mOnItemClickListener;

    //    private static final int[] mdrawableId = {R.drawable.orange,R.drawable.chat_box_blue,R.drawable.dark_blue,R.drawable.dark_purple,R.drawable.red,R.drawable.green};
    public interface OnItemClickListener {
        void onItemClick(Hits obj);

    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }


    public AdapterNews(Context context, List<Hits> items) {
        this.items = items;
        mContext = context;

    }

    private class OriginalViewHolder extends RecyclerView.ViewHolder {

        LinearLayout lyt_parent;
        TextView mDescription;

        public OriginalViewHolder(View v) {
            super(v);
            lyt_parent = (LinearLayout) v.findViewById(R.id.lyt_parent);
            mDescription = (TextView) v.findViewById(R.id.description);
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.lsv_item_post, parent, false);
        vh = new OriginalViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof OriginalViewHolder) {
            final Hits p = items.get(position);
            OriginalViewHolder vItem = (OriginalViewHolder) holder;
            if (p.title != null)
                vItem.mDescription.setText(p.title);
            else
                vItem.mDescription.setText(mContext.getResources().getString(R.string.no_info_found));

            vItem.lyt_parent.setTag(items.get(position));


            vItem.lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickListener != null) {
                        Hits channel = (Hits) view.getTag();
                        mOnItemClickListener.onItemClick(channel);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    public void insertData(List<Hits> items) {
        setLoaded();
        int positionStart = getItemCount();
        int itemCount = items.size();
        this.items.addAll(items);
        notifyItemRangeInserted(positionStart, itemCount);
    }

    private void setLoaded() {

        for (int i = 0; i < getItemCount(); i++) {
            if (items.get(i) == null) {
                items.remove(i);
                notifyItemRemoved(i);
            }
        }
    }


}