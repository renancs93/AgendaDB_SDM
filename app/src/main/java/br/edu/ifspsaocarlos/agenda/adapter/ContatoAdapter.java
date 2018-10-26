package br.edu.ifspsaocarlos.agenda.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import br.edu.ifspsaocarlos.agenda.data.ContatoDAO;
import br.edu.ifspsaocarlos.agenda.model.Contato;
import br.edu.ifspsaocarlos.agenda.R;

import java.util.List;


public class ContatoAdapter extends RecyclerView.Adapter<ContatoAdapter.ContatoViewHolder> {

    private static List<Contato> contatos;
    private Context context;
    private ContatoDAO cDAO;

    private static ItemClickListener clickListener;

    public ContatoAdapter(List<Contato> contatos, Context context) {
        this.contatos = contatos;
        this.context = context;
    }

    @Override
    public ContatoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.contato_celula, parent, false);
        return new ContatoViewHolder(view);
    }


    @Override
    public void onBindViewHolder(ContatoViewHolder holder, int position) {
       holder.nome.setText(contatos.get(position).getNome());

       //Informação de contato favorito
       if (contatos.get(position).getFavorite() == 1){
           holder.favoriteIcon.setChecked(true);
       }
       else {
           holder.favoriteIcon.setChecked(false);
       }

    }

    @Override
    public int getItemCount() {
        return contatos.size();
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        clickListener = itemClickListener;
    }


    public  class ContatoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        final TextView nome;
        CheckBox favoriteIcon;

        ContatoViewHolder(final View view) {
            super(view);

            nome = (TextView)view.findViewById(R.id.nome);
            favoriteIcon = (CheckBox)view.findViewById(R.id.favorite_imageView);

            view.setOnClickListener(this);

            favoriteIcon.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if (isChecked){
                        //add favorito
                        favoriteIcon.setButtonDrawable(R.drawable.ic_star_yellow);
                        Toast.makeText(context, "Adicionado aos Favoritos", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        //remove favorito
                        favoriteIcon.setButtonDrawable(R.drawable.ic_star_border_yellow);
                        Toast.makeText(context, "Removido dos Favoritos", Toast.LENGTH_SHORT).show();
                    }

                    //Chama função que irá atualizar no banco de dados o status de Favorito
                    atualizaFavoritos(getAdapterPosition(), isChecked==true?1:0);

                }
            });
        }

        @Override
        public void onClick(View view) {

            if (clickListener != null)
                clickListener.onItemClick(getAdapterPosition());
        }
    }


    public interface ItemClickListener {
        void onItemClick(int position);
    }

    public interface ItemFavoriteChange {
        void onFavoriteChange(int position,int favorito);
    }

    void atualizaFavoritos(int position, int favorito){

        Contato c = new Contato();
        c.setId(contatos.get(position).getId());
        c.setFavorite(favorito);

        cDAO = new ContatoDAO(context);
        cDAO.addOurRemoveFavorite(c);
    }

}
