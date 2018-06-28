package com.example.user.myapplication;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.ListIterator;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.CardViewHolder> {

    private List<Card> cards;


    public static class CardViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        int currentCardPosition;
        Context mContext;

        CardViewHolder(CardView cv, Context context) {
            super(cv);
            cardView = cv;

            mContext=context;
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    if(mContext instanceof ScrollingActivity){
                        ((ScrollingActivity)mContext).showSnackbar(currentCardPosition);
                        ((ScrollingActivity)mContext).openCard(currentCardPosition);
                    }
                }
            });
        }
    }



    RVAdapter(List otherCards){
        this.cards = otherCards;
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cv = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview, parent, false);
        return new CardViewHolder(cv, cv.getContext());
    }

    @Override
    public void onBindViewHolder(CardViewHolder cardViewHolder, int position) {
        CardView cardView = cardViewHolder.cardView;
        TextView content = (TextView)cardView.findViewById(R.id.content);
        TextView title = (TextView)cardView.findViewById(R.id.title);
        ImageView picture = (ImageView)cardView.findViewById(R.id.person_photo);

        title.setText(cards.get(position).name);
        picture.setImageResource(cards.get(position).photoId);
        content.setText(cards.get(position).description);
        cardViewHolder.currentCardPosition = position;

    }

    @Override
    public int getItemCount() {
        return cards.size();
    }
}