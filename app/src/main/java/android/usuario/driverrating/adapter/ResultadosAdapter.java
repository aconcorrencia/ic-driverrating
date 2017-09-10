package android.usuario.driverrating.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.usuario.driverrating.R;
import android.usuario.driverrating.domain.DadosLogClassificacao;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.util.ArrayList;


public class ResultadosAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private RecyclerViewOnClickListenerHack mRecyclerViewOnClickListenerHack;
    private float scale;
    private int width;
    private int height;
    private DisplayImageOptions options;
    private ArrayList<DadosLogClassificacao> mList;

    public ResultadosAdapter(Context c, ArrayList<DadosLogClassificacao> mList) {
        mContext = c;
        this.mList = mList;
        mLayoutInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        scale = mContext.getResources().getDisplayMetrics().density;
        width = mContext.getResources().getDisplayMetrics().widthPixels - (int) (14 * scale + 0.5f);
        height = (width / 16) * 9;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = mLayoutInflater.inflate(R.layout.item_resultados, viewGroup, false);
        MyViewHolder mvh = new MyViewHolder(v);
        return mvh;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder myViewHolder, final int position) {
        if (mList.size() > 0 && position < mList.size()) {
            ((MyViewHolder) myViewHolder).tvTitle.setText("Resultado nº "+mList.get(position).getId());
            ((MyViewHolder) myViewHolder).tvDate.setText(mList.get(position).getData());
            ((MyViewHolder) myViewHolder).tvHour.setText(mList.get(position).getHora());
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();

    }

    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    public void setRecyclerViewOnClickListenerHack(RecyclerViewOnClickListenerHack r) {
        mRecyclerViewOnClickListenerHack = r;
    }


    public void addListItem(DadosLogClassificacao c, int position) {
        mList.add(c);
        notifyItemInserted(position);
    }

    public void addList(ArrayList<DadosLogClassificacao> c) {
        for (DadosLogClassificacao q : c) {
            mList.add(q);
            notifyItemInserted(mList.size());
        }
    }

    public void swap(ArrayList<DadosLogClassificacao> c) {
        if (mList != null) {
            mList.clear();
            mList.addAll(c);
        } else {
            mList = c;
        }
        notifyDataSetChanged();
    }


    public void removeListItem(int position) {
        mList.remove(position);
        notifyItemRemoved(position);
    }

    public void clear() {
        mList.clear();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView tvTitle;
        public TextView tvDate;
        public TextView tvHour;
        public Button btnVer;
        public Button btnApagar;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            tvDate = (TextView) itemView.findViewById(R.id.tvDate);
            tvHour = (TextView) itemView.findViewById(R.id.tvHour);
            btnVer = (Button) itemView.findViewById(R.id.btnVer);
            btnApagar = (Button) itemView.findViewById(R.id.btnApagar);
            itemView.setId(R.id.list_item);
            btnVer.setOnClickListener(this);
            btnApagar.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mRecyclerViewOnClickListenerHack != null) {
                mRecyclerViewOnClickListenerHack.onClickListener(v, getPosition());
            }
        }
    }
}
