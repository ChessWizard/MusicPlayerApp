package com.chess.musicplayerapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.chess.musicplayerapplication.databinding.ActivityMainBinding;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    // EXTERNAL_STORAGE'in dosya yolu alinmistir -> /storage/emulated/0
    // +"/" da ekleniyor ki main folders(ana dosyalar) kismina cikabilsin.
    private final static String MEDIA_PATH = Environment.getExternalStorageDirectory().getPath()+"/";
            //Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getPath()+"/";

    //.mp3 uzantili sarkilari tutacak liste.
    private ArrayList<String> songList = new ArrayList<String>();

    // RecyclerView icin Adapter class;
    private MusicAdapter adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        /*  android:maxLines="1" -> Uzun yazilarda, sadece tek bir satira sigacak kadarini gostersin.
                                    NOT: Sigmayanlari "animasyon yaratarak" goruntuleyecegiz.
            android:ellipsize="end" -> Normal sartlarda sigamaycak cok uzun yazilar icin yazinin sonunda olacak sekilde uzunlugunu belirtmek icin "..." goruntulenir.

            background_card_view adli bir drawable yaratarak card'larimiz icin arka plan hazirladik.
            "1dp kalinliginda siyah bir cerceve" 'yi stroke tag'i ile ekledik.

            LinearLayout(horizontal) ile belirli buyuklukte bir muzik notasi resmi olmasini sagladik.
            android:scaleType="fitCenter" -> Resmin merkezde kalacak sekilde ayarlanmasi saglandi.
            android:padding="20dp" -> Oncesinde resmi merkezde sabitledigimiz icin kenarlarindan bosluk birakarak boyutunu kucultebildik konumunu bozmadan.

            Resmin bulundugu LinearLayout'un altina RecyclerView'umuzu koyduk. Muziklerimiz burada goruntulenecek.
            tools:listitem="@layout/card_view_design" -> Yarattigimiz tasarimi da RecyclerView uzerinde nasil gorunecegini gormek icin ekledik.
                                                         Bu tasarim sonrasinda Adapter yapisi ile kontrol edilecek.

        */

        // RecyclerView "listeleme sekli" ayarlaniyor

        // Kapsayici LayoutManager class'i nesnesi, child class'i olan LinearLayoutManager'dan orneklendi ve tipi belirlenmis oldu.
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        binding.rv.setLayoutManager(layoutManager);// Kisaca, new LinearLayoutManager(this)'da yazilabilirdi.

       /* // svg formattaki imageView'umuzun rengini siyah yapiyoruz.

        binding.musicImage.setColorFilter(getColor(R.color.black));
*/
        // Kullanicidan dosyalarina erisim icin izin aliniyor.
        // ContextCompat sinifindaki static metot olan checkSelfPermission(context,izin tipi); ile izin aliyoruz.

        // kullanici izin vermediyse
        // ilk durumda kullanici izin vermemistir(default)
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){

            // kullanicidan ilk defa izin istiyoruz
            //requestPermission(context,izin icerigini goruntulemek icin String dizi icinde izin tanimi,kendi sececgimiz bir talep kodu sayisi);
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);

            // Rational(mantikli) bir soru da sorarak bunu yapabiliriz.


        }

        // Kullanici izin verdiyse
        else{
            // Ses dosyalari alinsin.
            getAllAudioFiles();
        }



    }

    // Kullanicinin depolama merkezinden ses dosyalari almaya yarayacak metot
    public void getAllAudioFiles(){
        /* Dogrudan intent ile de gecis yapabiliriz

        Intent intentToAudios = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        startActivity(intentToAudios);
        */

        // Neden ici bos degilse diye kontrol yapiliyor?
        if(MEDIA_PATH != null){// Eger uzanti ici bos degilse(dosyalar bulunduysa)


            // Dosya alim islemleri try-catch icerisinde yapilmalidir!
            // Database islemleri gibi NullPointerException hatasi vermeden duzgun calissin diye!
            try{

                // File(dosya) class'i icerisinde harici dosyalara gidis yolunu belirtiyoruz.
            File mainFile = new File(MEDIA_PATH);

            // Harici dosyalarin hepsine liste seklinde ulasiyoruz ve filesList adli nesnemize atiyoruz.
            File[] filesList = mainFile.listFiles();

            for (File file:filesList) {

                if(file.isDirectory()){
                    scanDirectory(file);
                }

                else{
                    // Dosya yolu getAbsolutePath() metodu ile String (yazili uzantisi) seklinde alindi
                    // Bu sayede .mp3 ile biten dosyalari almak icin kontrol yapabilecegiz. Cunku uzantimiz string sekilde.
                    String path = file.getAbsolutePath();
                    if(path.endsWith(".mp3")){
                        // Bu bulunan tum .mp3 uzantilari bir liste icerisine aktarilsin.
                        songList.add(path);
                        Log.e("songList", String.valueOf(songList));

                        // Adapter icerisine veri kumesi olarak songList koymustuk
                        // Kullanici cihazina yeni muzik indirirse,silerse bu listede eklenme/cikarma durumu olusur
                        // notifyDataSetChanged() ile de bu gibi "veri kumesi degisimleri" yakalanir.s

                        adapter.notifyDataSetChanged();
                    }
                }

            }

        }catch (NullPointerException e){
                e.printStackTrace();
            }
        }

        // adapter class'imizi ses dosyalarinin alinacagi metotta tanimlamaliyiz.
        // Cunku ses verilerine bu metot sayesinde erisiyoruz

        // Telefon icerisindeki tum .mp3 ler songList ile alinmisti.
        adapter = new MusicAdapter(MainActivity.this,songList);
        binding.rv.setAdapter(adapter);// adapter'imizi recyclerView'umuz(rv) icin veriyoruz.


    }

    // Dosya dizinini goruntulemeye yarayan metot

    public void scanDirectory(File directory){

        // Eger dizin verisinin ici bos degilse
        if(directory != null){
            try {
                File[] filesList = directory.listFiles();

                for (File file : filesList) {

                    if (file.isDirectory()) {
                        scanDirectory(file);
                    } else {
                        // Dosya yolu getAbsolutePath() metodu ile String (yazili uzantisi) seklinde alindi
                        // Bu sayede .mp3 ile biten dosyalari almak icin kontrol yapabilecegiz. Cunku uzantimiz string sekilde.
                        String path = file.getAbsolutePath();
                        if (path.endsWith(".mp3")) {
                            // Bu bulunan tum .mp3 uzantilari bir liste icerisine aktarilsin.
                            songList.add(path);
                        }
                    }

                }
            }catch (NullPointerException e){
                e.printStackTrace();
            }
            }
    }

    // Izin verilip/verilmeme sonucundaki durumlar burada yazilir

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Eger sectigimiz requestCode alindiysa -> requestCode == 1 ve "Onay verildi" secildiyse -> PackageManager.PERMISSION_GRANTED
        if(requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

            // Tum ses dosyalari alinsin eger 1 koduna sahip izine onay verildiyse.
            getAllAudioFiles();
        }

        /*else{
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }
*/
    }

}