package org.belichenko.a.timetodrink;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.belichenko.a.timetodrink.data_structure.Results;

import java.util.ArrayList;


public class PointsRecyclerViewAdapter extends RecyclerView.Adapter<PointsRecyclerViewAdapter.ViewHolder> {

    private final ArrayList<Results> mValues;
    private final ListFragment.OnListFragmentInteractionListener mListener;

    public PointsRecyclerViewAdapter(ArrayList<Results> items
            , ListFragment.OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).name);
        holder.mContentView.setText(mValues.get(position).vicinity);
        Picasso.with(App.getAppContext())
                .load(mValues.get(position).icon)
                .error(R.mipmap.ic_launcher)
                .into(holder.mImageView);

                holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public Results mItem;
        public final ImageView mImageView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.bar_name);
            mContentView = (TextView) view.findViewById(R.id.bar_adress);
            mImageView = (ImageView) view.findViewById(R.id.imageView);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
