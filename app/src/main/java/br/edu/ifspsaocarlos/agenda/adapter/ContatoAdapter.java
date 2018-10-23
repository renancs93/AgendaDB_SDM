package br.edu.ifspsaocarlos.agenda.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import br.edu.ifspsaocarlos.agenda.model.Contato;
import br.edu.ifspsaocarlos.agenda.R;

import java.util.List;


public class ContatoAdapter extends RecyclerView.Adapter<ContatoAdapter.ContatoViewHolder> {

    private static List<Contato> contatos;
    private Context context;


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
        ImageView favoriteIcon;

        ContatoViewHolder(final View view) {
            super(view);

            nome = (TextView)view.findViewById(R.id.nome);
            favoriteIcon = (ImageView)view.findViewById(R.id.favorite_imageView);

            view.setOnClickListener(this);

            favoriteIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //Drawable current = v.getBackground();
                    Drawable imgFavorite = ContextCompat.getDrawable(context, R.drawable.ic_star_border_yellow);

                    if (!v.getBackground().equals(imgFavorite)){
                        favoriteIcon.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_star_yellow));
                        Toast.makeText(context, "ADD Favoritos", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        favoriteIcon.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_star_border_yellow));
                        Toast.makeText(context, "Removido dos Favoritos", Toast.LENGTH_SHORT).show();
                    }

                    //favoriteIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_star_yellow));
                    //favoriteIcon.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_star_yellow));

                    //Toast.makeText(context, "Adicionado nos Favoritos", Toast.LENGTH_SHORT).show();
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

}


