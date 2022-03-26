package com.chess.musicplayerapplication;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

// RecyclerView.Adapter<MusicAdapter.MusicViewHolder> ->  Veri kumemizi tuttugumuz class'i Adapter Generic listesinin icerisine koyuyoruz.
public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MusicViewHolder>{

    Context mContext;// Adapter'i kullanabilmek icin android parcasi almaliyiz.
    ArrayList<String> dataList;// Adapter'a eklenecek veri kumesi

    // Constructor

    public MusicAdapter(Context mContext, ArrayList<String> dataList) {
        this.mContext = mContext;
        this.dataList = dataList;
    }

    // Muzik sayfasi gorunumlermizi (veri kumemiz) tutan class
    public class MusicViewHolder extends RecyclerView.ViewHolder{

        // itemView nesnesi ile RecyclerView uzerinde durma uzere yarattigimiz
        // tasarim gorunumlerine ulasabiliyoruz.(card_view_design.xml)

        private TextView text_music;
        private CardView card_music;

        public MusicViewHolder(@NonNull View itemView) {
            super(itemView);

            // Kullanilacak Card nesnelerimiz Constructor icerisinde tanimlandi.
            text_music = itemView.findViewById(R.id.text_music);
            card_music = itemView.findViewById(R.id.card_music);
        }
    }


    // RecyclerView uzerinde goruntulenecek tasarimin inflate edilecegi metot.
    @NonNull
    @Override
    public MusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // from(parent.getContext()) -> RecyclerView'un parent'indan (aktivite icerisinden) context alinir.

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_design,parent,false);

        // inflate edilen gorunum return edilir.
        return new MusicViewHolder(v);
    }

    // "Card gorunumlerinin birlestirildigi" metot
    // Verilerin davranisi burada islenir.

    @Override
    public void onBindViewHolder(@NonNull MusicViewHolder holder, int position) {

        // Veri kumemiz alindi.
        String filePath = dataList.get(position);

        // Verilerimizle ilgili islemler yapiliyor

        // Muzik basligi(ismi) aliniyor
        // muzik uzantisindaki son / karakterinin bulundugu indise gidilir
        // bu indisten bir sonraki (+1. indis) muzik isminin baslangicidir.
        // "buradan baslayacak sekilde" string bitene kadar gideriz ve muzik ismimiz alinmis olur.
        String musicTitle = filePath.substring(filePath.lastIndexOf("/")+1);

        //Aldigimiz muzik ismini CardView uzerinde holder nesnesi ile ularasak goruntuluyoruz.
        holder.text_music.setText(musicTitle);

        // Herhangi bir card'imiza(muzige) tiklandiginda olacaklar

        holder.card_music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Muzige tiklandiginda daha detayli gorunumu olan sayfasina ulasilsin
                // oynatma ayarlari,kac saniye gecti,kac saniye kaldi...
                Intent intent = new Intent(mContext,MainActivity2.class);

                // Gecis yaparken muzik ismi, muzik yolu gibi ozelliklerini de aktiviteye gonderiyoruz.
                intent.putExtra("musicTitle",musicTitle);
                intent.putExtra("filePath",filePath);
                intent.putExtra("position", holder.getAdapterPosition());
                intent.putExtra("dataList",dataList);

                // Adapter class'i bir aktivite olmadigi icin startActivity() metodu dogrudan alinamaz
                // Intent'in yazildigi context(android,aktivite parcasi) alinarak bu gecis yapilabilir.
                mContext.startActivity(intent);

            }
        });


    }

    // Card'imizin kac defa RecyclerView uzerine eklenecegini yoneten metot
    // Veri kumemizdeki eleman sayisi kadar eklensin diyerek tum elemanlari eklemis oluruz.
    @Override
    public int getItemCount() {
        return dataList.size();
    }

}
