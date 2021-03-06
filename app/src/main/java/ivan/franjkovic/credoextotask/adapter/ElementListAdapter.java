package ivan.franjkovic.credoextotask.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ivan.franjkovic.credoextotask.R;
import ivan.franjkovic.credoextotask.db.Element;

import static ivan.franjkovic.credoextotask.tools.Const.ITEM_HEIGHT;
import static ivan.franjkovic.credoextotask.tools.Const.PREFERENCE_FILE;

public class ElementListAdapter extends RecyclerView.Adapter<ElementListAdapter.ElementViewHolder> implements OnItemTouchHelperListener {

    private Context context;
    private List<Element> mElements;
    private OnElementListChangedListener listener;
    private OnItemClickListener clickListener;
    private SharedPreferences sharedPreferences;

    public class ElementViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.tv_name)
        TextView tvName;

        @BindView(R.id.tv_start)
        TextView tvStart;

        @BindView(R.id.tv_end)
        TextView tvEnd;

        @BindView(R.id.tv_tag)
        TextView tvTag;

        @BindView(R.id.itemContent)
        LinearLayout rvItem;


        public ElementViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            //rvItem.getLayoutParams().height = itemHeight();

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) {
                Element e = mElements.get(getAdapterPosition());
                clickListener.onItemClick(e.getId(), e.getName(), dateFormat(e.getStart())[1], dateFormat(e.getEnd())[0], e.getTag());
            }
        }
    }

    public ElementListAdapter(Context context, OnElementListChangedListener onElementListChangedListener, OnItemClickListener clickListener) {
        this.context = context;
        this.listener = onElementListChangedListener;
        this.clickListener = clickListener;
        mElements = new ArrayList<>();
    }

    @NonNull
    @Override
    public ElementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_item, parent, false);

        itemView.getLayoutParams().height = itemHeight();
        return new ElementViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ElementViewHolder holder, int position) {
        if (mElements != null) {
            Element current = mElements.get(position);
            holder.tvName.setText(current.getName());
            holder.tvStart.setText(dateFormat(current.getStart())[1]);
            holder.tvEnd.setText(dateFormat(current.getEnd())[0]);
            holder.tvTag.setText(current.getTag());
        }
    }

    @Override
    public int getItemCount() {
        if (mElements != null) {
            return mElements.size();
        } else {
            return 0;
        }
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        Collections.swap(mElements, fromPosition, toPosition);
        listener.onSwipeListChanged(sortedList(mElements), mElements);
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemDismiss(int position) {
        listener.onElementRemove(mElements.get(position), mElements);
        notifyItemRemoved(position);
    }

    public void setElements(List<Element> elements) {
        mElements = elements;
        notifyDataSetChanged();
    }

    private int itemHeight() {
        sharedPreferences = context.getSharedPreferences(PREFERENCE_FILE, Context.MODE_PRIVATE);
        int height = sharedPreferences.getInt(ITEM_HEIGHT, 0);
        if (height > 0) {
            return height;
        } else {
            return 256;
        }
    }

    private List<Integer> sortedList(List<Element> elementList) {
        List<Integer> idList = new ArrayList<>();
        for (Element e : elementList) {
            idList.add(e.getId());
        }
        return idList;
    }

    private String[] dateFormat(long currentTime) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy.");
        Date date = new Date(currentTime);
        return new String[]{dateFormat.format(date), timeFormat.format(date)};
    }

}
