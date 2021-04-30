package com.example.adsdemo;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdView;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int MENU_ITEM_VIEW_TYPE = 0;
    private static final int BANNER_AD_VIEW_TYPE = 1;
    Context context;
    List<Object> recyclerViewItems;
    public OnItemClickListener onItemClickListener;

    public RecyclerViewAdapter(Context context, List<Object> recyclerViewItems,OnItemClickListener onItemClickListener) {
        this.context = context;
        this.recyclerViewItems = recyclerViewItems;
        this.onItemClickListener = onItemClickListener;
    }

    public class MenuItemViewHolder extends RecyclerView.ViewHolder {
        private TextView menuItemName;
        LinearLayout ll_Main;

        MenuItemViewHolder(View view) {
            super(view);

            menuItemName = view.findViewById(R.id.menu_item_name);
            ll_Main = view.findViewById(R.id.ll_Main);

            ll_Main.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(getAdapterPosition(), v);
                }
            });
        }
    }

    public class AdViewHolder extends RecyclerView.ViewHolder {

        AdViewHolder(View view) {
            super(view);
        }
    }

    @Override
    public int getItemCount() {
        return recyclerViewItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position % MainActivity.ITEMS_PER_AD == 0) ? BANNER_AD_VIEW_TYPE : MENU_ITEM_VIEW_TYPE;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Log.e("TAG", "onCreateViewHolder: viewType = "+viewType);
        switch (viewType) {
            case MENU_ITEM_VIEW_TYPE:
                Log.e("TAG", "onCreateViewHolder: MENU_ITEM_VIEW_TYPE viewType = "+viewType);
                View menuItemLayoutView = LayoutInflater.from(viewGroup.getContext()).inflate(
                        R.layout.menu_item_container, viewGroup, false);
                return new MenuItemViewHolder(menuItemLayoutView);
            case BANNER_AD_VIEW_TYPE:
                Log.e("TAG", "onCreateViewHolder: BANNER_AD_VIEW_TYPE viewType = "+viewType);
                // fall through
            default:
                Log.e("TAG", "onCreateViewHolder: default viewType = "+viewType);
                View bannerLayoutView = LayoutInflater.from(
                        viewGroup.getContext()).inflate(R.layout.banner_ad_container,
                        viewGroup, false);
                return new AdViewHolder(bannerLayoutView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Log.e("TAG", "onBindViewHolder: position = " + position);

        int viewType = getItemViewType(position);
        switch (viewType) {
            case MENU_ITEM_VIEW_TYPE:
                MenuItemViewHolder menuItemHolder = (MenuItemViewHolder) holder;
                ModelClass modelClass = (ModelClass) recyclerViewItems.get(position);

                menuItemHolder.menuItemName.setText(modelClass.getName());

                break;
            case BANNER_AD_VIEW_TYPE:
                AdViewHolder bannerHolder = (AdViewHolder) holder;
                AdView adView = (AdView) recyclerViewItems.get(position);
                ViewGroup adCardView = (ViewGroup) bannerHolder.itemView;

                if (adCardView.getChildCount() > 0) {
                    adCardView.removeAllViews();
                }
                if (adView.getParent() != null) {
                    ((ViewGroup) adView.getParent()).removeView(adView);
                }

                adCardView.addView(adView);
                // fall through
            default:

        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int pos, View v);
    }
}