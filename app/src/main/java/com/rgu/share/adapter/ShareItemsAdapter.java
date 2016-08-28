package com.rgu.share.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.rgu.share.R;
import com.rgu.share.ShareItem;

import java.util.ArrayList;

/**
 * Created by Raghu on 27/06/16.
 */
public class ShareItemsAdapter extends RecyclerView.Adapter<ShareItemsAdapter.ViewHolder> {

    private final LayoutInflater mInflater;
    private ArrayList<ShareItem> mItemsList = new ArrayList<>();
    private Context mContext;

    private boolean mIsDeleteModeActivated;
    private OnItemSelectListener mOnItemSelectListener;
    private static final String SELECTED_ITEM = "selected_item";

    public ShareItemsAdapter(Context context, ArrayList<Uri> itemsList) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = context;
        mOnItemSelectListener = (OnItemSelectListener) context;

        addAll(itemsList);
    }

    private void addAll(ArrayList<Uri> itemsList) {
        for (Uri uri : itemsList) {
            mItemsList.add(new ShareItem(uri));
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = mInflater.inflate(R.layout.list_share_item_layout, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Glide.with(mContext)
                .load(mItemsList.get(position).uri)
                .centerCrop()
                .override(150, 150)
                .placeholder(R.drawable.ic_loading_placeholder)
                .into(holder.itemIv);

        holder.shareItem = mItemsList.get(position);
        if (!mIsDeleteModeActivated) {
            holder.shareItem.isSelected = false;
        }

        Log.d("rgu", "pos= " + position + " isSelected= " + holder.shareItem.isSelected);
        holder.itemIv.setSelected(mItemsList.get(position).isSelected);
//        holder.shareItem = mItemsList.get(position);
        holder.view.setTag(holder);
    }

    @Override
    public int getItemCount() {
        return mItemsList.size();
    }

    public boolean getIsDeleteModeActivated() {
        return mIsDeleteModeActivated;
    }

    public void setIsDeleteModeActivated(boolean deleteMode) {
        mIsDeleteModeActivated = deleteMode;
    }

    public void clearAllItems() {
        mItemsList.clear();
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private ImageView itemIv;
        View view;
        ShareItem shareItem;

        public ViewHolder(View itemView) {
            super(itemView);
            itemIv = (ImageView) itemView.findViewById(R.id.share_item_iv);
            view = itemView;
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);

        }

        @Override
        public void onClick(View v) {
            if (mIsDeleteModeActivated) {
                ViewHolder holder = (ViewHolder) v.getTag();

                boolean status = !holder.shareItem.isSelected;
                holder.itemIv.setSelected(status);
                holder.shareItem.isSelected = status;
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (!mIsDeleteModeActivated) {
                ViewHolder holder = (ViewHolder) v.getTag();

                holder.itemIv.setSelected(!v.isSelected());
                holder.shareItem.isSelected = true;

                mIsDeleteModeActivated = true;
                mOnItemSelectListener.onDeleteModeActivated();
            }
            return true;
        }
    }

    public interface OnItemSelectListener {
        void onDeleteModeActivated();
    }
}
