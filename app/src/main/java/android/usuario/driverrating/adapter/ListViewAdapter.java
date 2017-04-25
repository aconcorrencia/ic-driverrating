package android.usuario.driverrating.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.usuario.driverrating.R;
import android.usuario.driverrating.domain.Veiculo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.joooonho.SelectableRoundedImageView;

import java.util.ArrayList;

public class ListViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private ArrayList<Veiculo> mList;
    private LayoutInflater mLayoutInflater;
    private RecyclerViewOnClickListenerHack mRecyclerViewOnClickListenerHack;
    private RecyclerViewOnClickListenerHack mRecyclerViewOnLongClickListenerHack;
    private long idPadrao;
    private final int VIEW_ITEM = 0;


    public ListViewAdapter(Context c, ArrayList<Veiculo> mList, long idPadrao) {
        mContext = c;
        this.mList = mList;
        this.idPadrao=idPadrao;
        mLayoutInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /*public ListViewAdapter(ExibirPerfilComportamental c, ArrayList<DadosSensores> ds) {

    }*/


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = mLayoutInflater.inflate(R.layout.item_listview, viewGroup, false);
        MyViewHolderPerfil mvh = new MyViewHolderPerfil(v);
        return mvh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder myViewHolder, int position) {
        ((MyViewHolderPerfil) myViewHolder).txtNomePerfil.setText(Html.fromHtml("<b>Nome do Perfil:</b> " + mList.get(position).getNomeUsuario()));
        ((MyViewHolderPerfil) myViewHolder).txtMarca.setText(Html.fromHtml("<b>Marca:</b> " + mList.get(position).getMarca()));
        ((MyViewHolderPerfil) myViewHolder).txtModelo.setText(Html.fromHtml("<b>Modelo:</b> " + mList.get(position).getModelo()));
        ((MyViewHolderPerfil) myViewHolder).txtMotor.setText(Html.fromHtml("<b>Motor:</b> " + mList.get(position).getMotor()));
        ((MyViewHolderPerfil) myViewHolder).txtVersao.setText(Html.fromHtml("<b>Versão:</b> " + mList.get(position).getVersao()));

        if (mList.get(position).getFoto() == null)
            ((MyViewHolderPerfil) myViewHolder).img.setImageResource(R.drawable.img_car2);
        else
            ((MyViewHolderPerfil) myViewHolder).img.setImageBitmap(mList.get(position).getFoto());
        if(idPadrao == mList.get(position).getId()){
            ((MyViewHolderPerfil) myViewHolder).imgPadrao.setVisibility(ImageView.VISIBLE);
        }else{
            ((MyViewHolderPerfil) myViewHolder).imgPadrao.setVisibility(ImageView.GONE);
        }
    }

    public void swap(ArrayList<Veiculo> c){
        if (mList != null) {
            mList.clear();
            mList.addAll(c);
        }
        else {
            mList = c;
        }
        notifyDataSetChanged();
    }

    public void setIdPadrao(long idPadrao) {
        this.idPadrao = idPadrao;
    }

    @Override
    public int getItemCount() {
        return mList.size();

    }

    @Override
    public int getItemViewType(int position) {

        return VIEW_ITEM;

    }

    public void setRecyclerViewOnClickListenerHack(RecyclerViewOnClickListenerHack r) {
        mRecyclerViewOnClickListenerHack = r;
    }

    public void setRecyclerViewOnLongClickListenerHack(RecyclerViewOnClickListenerHack r) {
        mRecyclerViewOnLongClickListenerHack = r;
    }

    public void addListItem(Veiculo c, int position) {
        mList.add(c);
        notifyItemInserted(position);
    }

    public void addList(ArrayList<Veiculo> c) {
        for (Veiculo q : c) {
            mList.add(q);
            notifyItemInserted(mList.size());
        }
    }


    public void removeListItem(int position) {
        mList.remove(position);
        notifyItemRemoved(position);
    }


    public class MyViewHolderPerfil extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        public SelectableRoundedImageView img;
        public ImageView imgPadrao;
        public TextView txtNomePerfil;
        public TextView txtMarca;
        public TextView txtModelo;
        public TextView txtMotor;
        public TextView txtVersao;

        public MyViewHolderPerfil(View itemView) {
            super(itemView);
            //img = (ImageView) itemView.findViewById(R.id.img);
            img = (SelectableRoundedImageView) itemView.findViewById(R.id.img);
            imgPadrao = (ImageView) itemView.findViewById(R.id.imgPadrao);
            txtNomePerfil = (TextView) itemView.findViewById(R.id.txtNomePerfil);
            txtMarca = (TextView) itemView.findViewById(R.id.txtMarca);
            txtModelo = (TextView) itemView.findViewById(R.id.txtModelo);
            txtMotor = (TextView) itemView.findViewById(R.id.txtMotor);
            txtVersao = (TextView) itemView.findViewById(R.id.txtVersao);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            itemView.setId(R.id.list_item);

        }

        @Override
        public void onClick(View v) {
            if (mRecyclerViewOnClickListenerHack != null) {
                mRecyclerViewOnClickListenerHack.onClickListener(v, getPosition());
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (mRecyclerViewOnLongClickListenerHack != null) {
                mRecyclerViewOnLongClickListenerHack.onLongPressClickListener(v, getPosition());
                return true;
            }
            return false;
        }
    }

}
