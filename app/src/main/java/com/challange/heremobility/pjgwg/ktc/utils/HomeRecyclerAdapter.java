package com.challange.heremobility.pjgwg.ktc.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.challange.heremobility.pjgwg.ktc.R;

/**
 * Created by pranjul on 25/2/18.
 */

public class HomeRecyclerAdapter extends RecyclerView.Adapter<HomeRecyclerAdapter.MyViewHolder> {

    private final String[] TAGS = {"Location", "Hotel", "Hospital", "Local Shops"};
    private final int[] IMGS = {R.drawable.location, R.drawable.hotel, R.drawable.hospital, R.drawable.market};
    RecyclerOnClickListener listener;
    private Context context;


    public HomeRecyclerAdapter(Context context, RecyclerOnClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.layout_card, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        ImageView visibleImg;
        TextView visibleText;
        if (position % 2 == 0) {
            holder.imageView2.setVisibility(View.INVISIBLE);
            holder.frameLayout1.setVisibility(View.INVISIBLE);
            holder.textView1.setVisibility(View.INVISIBLE);

            visibleImg = holder.imageView1;
            visibleText = holder.textView2;

        } else {
            holder.imageView1.setVisibility(View.INVISIBLE);
            holder.frameLayout2.setVisibility(View.INVISIBLE);
            holder.textView2.setVisibility(View.INVISIBLE);

            visibleImg = holder.imageView2;
            visibleText = holder.textView1;

        }

        holder.setData(position, visibleImg, visibleText);
    }

    @Override
    public int getItemCount() {
        return TAGS.length;
    }

    public interface RecyclerOnClickListener {
        public void onItemClick(int position);
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imageView1, imageView2;
        TextView textView1, textView2;
        CardView cardView;
        FrameLayout frameLayout1, frameLayout2;

        public MyViewHolder(View itemView) {
            super(itemView);

            imageView1 = (ImageView) itemView.findViewById(R.id.image_1);
            imageView2 = (ImageView) itemView.findViewById(R.id.image_2);
            textView1 = (TextView) itemView.findViewById(R.id.textview_1);
            textView2 = (TextView) itemView.findViewById(R.id.textview_2);
            cardView = (CardView) itemView.findViewById(R.id.card_home);
            frameLayout1 = (FrameLayout) itemView.findViewById(R.id.layout_1);
            frameLayout2 = (FrameLayout) itemView.findViewById(R.id.layout_2);

            cardView.setOnClickListener(this);

            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();

            int height = displayMetrics.heightPixels - 40;

            height = height / 4;
            imageView1.getLayoutParams().height = height;
            imageView1.requestLayout();

            imageView2.getLayoutParams().height = height;
            imageView2.requestLayout();


        }

        @SuppressLint("ResourceAsColor")
        void setData(int pos, ImageView imageView, TextView textView) {
            textView.setText(TAGS[pos]);
            imageView.setImageResource(IMGS[pos]);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            listener.onItemClick(position);
        }
    }
}
