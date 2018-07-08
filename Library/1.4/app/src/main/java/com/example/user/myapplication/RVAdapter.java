package com.example.user.myapplication;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.ListIterator;
import java.util.Random;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.CardViewHolder> {

    private List<Card> cards;


    public static class CardViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        int currentCardPosition;
        Context mContext;

        CardViewHolder(CardView cv, Context context) {
            super(cv);
            cardView = cv;

            mContext = context;
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mContext instanceof ScrollingActivity) {
                        ((ScrollingActivity) mContext).showSnackbar(currentCardPosition);
                        ((ScrollingActivity) mContext).openCard(currentCardPosition);
                    }
                }
            });
        }
    }


    RVAdapter(List otherCards) {
        this.cards = otherCards;
    }

    void addCards(List cards) {
        this.cards = cards;
    }

    @Override
    public @NonNull
    CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView cv = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview, parent, false);
        return new CardViewHolder(cv, cv.getContext());
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder cardViewHolder, int position) {
        CardView cardView = cardViewHolder.cardView;
        TextView content = (TextView) cardView.findViewById(R.id.content);
        TextView title = (TextView) cardView.findViewById(R.id.title);
        ImageView picture = (ImageView) cardView.findViewById(R.id.person_photo);

        title.setText(cards.get(position).name);
        if (cards.get(position).getPhotoId() == 0) {
            cards.get(position).setPhotoId(setPicture());
        }
        picture.setImageResource(cards.get(position).getPhotoId());
        content.setText(reduceText(cards.get(position).description));
        cardViewHolder.currentCardPosition = position;

        //if(onLoadMoreListener != null && !isLoading && !noMore && cardViewHolder.getAdapterPosition() == getItemCount() - 1) {
        if (onLoadMoreListener != null && cardViewHolder.getAdapterPosition() == getItemCount() - 1) {
            isLoading = true;
            onLoadMoreListener.onLoadMore();
        }
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    private OnLoadMoreListener onLoadMoreListener;

    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        this.onLoadMoreListener = listener;
    }

    private boolean isLoading, noMore;

    public void endLoading() {
        this.isLoading = false;
    }

    public void setNoMore(boolean noMore) {
        this.noMore = noMore;
    }


    private String reduceText(String str) {
        return str.length() > 72 ? str.substring(0, 70) + " ..." : str;
    }

    private int setPicture() {
        int photoId[] = {R.drawable.book1, R.drawable.book2, R.drawable.book3};
        Random random = new Random();
        return photoId[random.nextInt(photoId.length)];
    }


    @Override
    public int getItemCount() {
        return cards.size();
    }
}