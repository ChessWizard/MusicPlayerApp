package com.chess.musicplayerapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.media.MediaPlayer;
import android.media.TimedMetaData;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.SeekBar;

import com.chess.musicplayerapplication.databinding.ActivityMain2Binding;

import java.io.IOException;
import java.util.ArrayList;


public class MainActivity2 extends AppCompatActivity {

   // Adapter class'indan (MainActivity'den) gelen veriler aliniyor.

    String musicTitle,filePath;
    int position;// Adapter pozisyonu
    ArrayList<String> dataList;// Veri kumesi

    // Muziklerimiz ile ilgili islemlere ulasabilecegimiz class
    private MediaPlayer mediaPlayer;

    // Runnable ve Handler yapilari ile Seekbar hareketini kontrol edecegiz
    // Bu yapi sayesinde belirli kodlar belirli sure araliklarinda calistirilabilir.

    Runnable runnable;
    Handler handler;
    int totalTime;// Muzigin toplam suresini tutacak olan degisken.

    // Animasyon class'imiz tanimlaniyor
    private Animation animation;

    private ActivityMain2Binding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMain2Binding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        /* LinearLayout icerisinde Constraint islemi yapilmis halde gelir.
           Bundan dolayi uzaklik islemleri xml kodlari uzerinden yalnizca yapilabilir

           android:layout_marginStart="5dp" -> Kenardan 10dp uzakta olsun
           android:layout_gravity="center" -> Bulundugu yerin "merkezinde" olsun.(vertical constraint gibi)

           Ses acma/kapama islemini kontrol edebilmek icin SeekBar yapisini kullaniriz.

           android:max="100" -> En fazla 100 seviyesine kadar ses acilsin
           android:progress="50" -> Ilk konumu 50'de baslasin sesin
           android:layout_gravity="center" -> Bulundugu yerin "merkezinde" olsun.(vertical constraint gibi)
           android:layout_weight="1" -> Birden cok gorunumun responsive dagilmasi icin "oran saglamaya" yarar.
                                        1 verildiginde, geri kalan tum satiri bu nesne kaplar.
                                        NOT: Bu ozelligi kullanabilmek icin width veya height ozelliklerinden birisi0dp olmalidir!
         */

        /*
        // svg formattaki imageView'umuzun rengini siyah yapiyoruz.
        // setColorFilter() metodu sayesinde ImageView'larin renkleri degistirilebilir.
        binding.musicImage2.setColorFilter(getColor(R.color.black));
        binding.buttonPrevious.setColorFilter(getColor(R.color.black));
        binding.buttonPlayPause.setColorFilter(getColor(R.color.black));
        binding.buttonNext.setColorFilter(getColor(R.color.black));
*/

        // Animasyon islemleri

        // anim dosyasi icerisinde yarattigimiz animasyonumuz aktif hale getiriliyor.
        animation = AnimationUtils.loadAnimation(MainActivity2.this,R.anim.translate_animation);

        // Uzun muzik isimlerinin goruntulenebilmesi icin bu animasyonu entegre ediyoruz.
        binding.textMusicName.setAnimation(animation);

        // gelen veriler tanimlaniyor
        musicTitle = getIntent().getStringExtra("musicTitle");
        filePath = getIntent().getStringExtra("filePath");
        position = getIntent().getIntExtra("position",0);
        dataList = getIntent().getStringArrayListExtra("dataList");

        // Gelen muzik ismini goruntuluyoruz
        binding.textMusicName.setText(musicTitle);

        // muzik iceriklerimizi kontrol etmek icin MediaPlayer nesnemizi tanimliyoruz
        mediaPlayer = new MediaPlayer();


        // Veritabani,dosya islemleri,kaynaktan veri cekme gibi islemler try-catch icerisinde kontrol edilerek yapilir.
        try {
            // Hangi muzigin uzantisi adapter'dan aktiviteye geldiyse, nesnemizin icine onu koyariz.
            mediaPlayer.setDataSource(filePath);

            // Muzik uzantisi alindiktan sonra bu media'yi oynatmaya hazir hale getiririz.
            // Bu islem ozunde kullanici "muzigi oynatmaya karar verene kadar" baslamamasini saglar.
            mediaPlayer.prepare();

            // Son olarak media baslatilsin
            // Dogrudan baslatiyoruz.
            // Cunku muzige tiklandigi anda calmasini istiyoruz.
            mediaPlayer.start();

        } catch (IOException e) {
            e.printStackTrace();
        }



        // onceki sarkiya gecme butonu
        binding.buttonPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Asagidaki 2 durum icin de gecerli oldugundan dolayi bu islem
                // En disa yazarak ikisi icin de etkili kilabiliriz.
                // Yeni bir sarkiya gecmeden once sifirlamaliyiz.
                // Aksi taktirde program coker!
                mediaPlayer.reset();

                // En az 1. indisteysek geri alinabilsin
                // 0. indiste geri basamayiz cunku - li bir indis ifadesi yoktur!
                if (position > 0) {
                    // Verimizin pozisyon indisini 1 azaltiyoruz ki onceki veri(sarki) gelsin.
                    position--;
                }

                // 0. indiste "geri" basilirsa da en sondaki sarkiya gidilsin
                else if (position == 0) {
                    position = dataList.size() - 1;// Son sarkinin indisi yapildi pozisyonumuz.
                }

                // Yeni gelinen indis degeri alindi ki sarkiya gecebilelim.
                String newFilePath = dataList.get(position);

                // Muzigimiz oynatilmaya baslanir.
                // Veri kaynagi degistigi icin tekrardan try-catch icerisinde baslattik!
                try {

                    mediaPlayer.setDataSource(newFilePath);
                    mediaPlayer.prepare();
                    mediaPlayer.start();

                    // Yeni muzigin baglantisini alip geci yaptik ama
                    // ismini de tekrar goruntulemeliyiz
                    String newMusicName = newFilePath.substring(newFilePath.lastIndexOf("/")+1);
                    binding.textMusicName.setText(newMusicName);

                    // Onceki text animasyonu sonlansin
                    binding.textMusicName.clearAnimation();
                    // Yeni gelen text'e animasyon eklenir
                    binding.textMusicName.startAnimation(animation);


                } catch (IOException e) {
                    e.printStackTrace();
                }
                binding.buttonPlayPause.setImageDrawable(ContextCompat.getDrawable(MainActivity2.this,R.drawable.music_pause));// durdurma tusu resmi goruntulensin.



            }
        });


        // Durdurma/tekrardan baslatma butonu
        binding.buttonPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Eger muzik caliyorsa
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.pause();// muzik durdurulsun

                    // NOT: Butonun arkaplan resmi yalnizca setImageDrawable(context,drawable uzantisi) ile degisti
                    binding.buttonPlayPause.setImageDrawable(ContextCompat.getDrawable(MainActivity2.this,R.drawable.music_play));// oynatma tusu resmi goruntulensin
                }

                // muzik durdurulmus haldeyse (calmiyorsa)
                else{
                    mediaPlayer.start();// muzik tekrardan baslatilsin(kaldigi yerden devam eder.)
                    binding.buttonPlayPause.setImageDrawable(ContextCompat.getDrawable(MainActivity2.this,R.drawable.music_pause));// durdurma tusu resmi goruntulensin.
                }

            }
        });

        // sonraki sarkiya gecme butonu
        binding.buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.reset();


                // son indiste ileri basamayiz cunku daha fazla tanimli indis ifadesi yoktur!
                if (position == dataList.size() - 1) {

                    position = 0;// en basa donsun
                }

                // sonuncu indiste "ileri" basilirsa da en sondaki sarkiya gidilsin
                else {
                    position++;// en basa donsun
                }


                // Yeni gelinen indis degeri alindi ki sarkiya gecebilelim.
                String newFilePath = dataList.get(position);

                // Muzigimiz oynatilmaya baslanir.
                // Veri kaynagi degistigi icin tekrardan try-catch icerisinde baslattik!
                try {

                    mediaPlayer.setDataSource(newFilePath);
                    mediaPlayer.prepare();
                    mediaPlayer.start();

                    // Yeni muzigin baglantisini alip geci yaptik ama
                    // ismini de tekrar goruntulemeliyiz
                    String newMusicName = newFilePath.substring(newFilePath.lastIndexOf("/")+1);
                    binding.textMusicName.setText(newMusicName);

                    // Onceki text animasyonu sonlansin
                    binding.textMusicName.clearAnimation();
                    // Yeni gelen text'e animasyon eklenir
                    binding.textMusicName.startAnimation(animation);


                } catch (IOException e) {
                    e.printStackTrace();
                }
                binding.buttonPlayPause.setImageDrawable(ContextCompat.getDrawable(MainActivity2.this,R.drawable.music_pause));// durdurma tusu resmi goruntulensin.
            }
        });

        // Seekbar ayarlari yapiliyor -> Ses acma/kapama ve sarki ileri/geri alma ayarlari

        // 1) ses olaylari
        binding.seekBarVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {


            // Seekbar uzerindeki deger degisimlerini kontrol eden metot
            // onProgressChanged(seekbar nesnesi, seekbar'in pozisyonu, kullanicinin seekbarda yaptigi degisimler icin)
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                // Eger kullanici seekbar uzerinde degisiklik yaptiysa
                if(fromUser){
                    // Degisiklik yapildiysa, seekbar'in konumu da bu degisiklige bagli degissin.
                    binding.seekBarVolume.setProgress(progress);

                    // muzik ses duzeyimize ulasiyoruz
                    float volumeLevel = progress / 100f;

                    // Muzik sesimiz ile seekbar'i bagliyoruz
                    mediaPlayer.setVolume(volumeLevel,volumeLevel);// Soldan ve sagdan gelen sesler ayni olsun.
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // 2) muzik suresi kontrolu
        binding.seekBarMusicTicking.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                // Eger seekbarUzerinde degisiklik olduysa
                if(fromUser){
                    // muzigimiz de seekbar dogrultusunda ilerlesin
                    mediaPlayer.seekTo(progress);

                    // kullanici dogrultusunda muzik ilerlesin(kullanici ileri/geri sarma durumlari)
                    binding.seekBarMusicTicking.setProgress(progress);
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // Runnable ve Handler yapilari tanimlandi

        handler = new Handler();// Zaman islemlerini kontrol eder
        runnable = new Runnable() {// Handler ile belirlenecek zaman araligi icerisinde gerceklesecek kodlari(olaylari) kontrol eder.
            @Override
            public void run() {

                // gelen muzigin toplam suresi alindi.
                totalTime = mediaPlayer.getDuration();

                // Muzik ilerlemesinin en max degerini toplam sure olarak ayarladik.
                binding.seekBarMusicTicking.setMax(totalTime);

                // Muzigin bulundugu konum alinir
                int currentPosition = mediaPlayer.getCurrentPosition();

                // Bu konum seekbar'a verilerek ilerlemesi saglanmis olur.
                binding.seekBarMusicTicking.setProgress(currentPosition);

                // Handler yapisi ile bu olaylar icin bir zaman araligi veriliyor.
                handler.postDelayed(runnable,1000);// Her 1 saniyede 1 bu olaylar gerceklessin.

                // Bu zaman icerisinde surelerimizi de es zamanli sekilde goruntuluyoruz.

                // gecen zaman
                String elapsedTime = createTimeLabel(currentPosition);

                // toplam zaman
                String lastTime = createTimeLabel(totalTime);

                // Son olarak Runnable calistikca bu degerler de goruntulensin es zamanli sekilde
                binding.textMusicTicking.setText(elapsedTime);
                binding.textMusicTotalTime.setText(lastTime);

                // Muzik bittigindeki olacaklari da kontrol etmemiz gerek
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                    // Muzik sonra erdigindeki durumlari kontrol eden metot.
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        mediaPlayer.reset();


                        // son indiste ileri basamayiz cunku daha fazla tanimli indis ifadesi yoktur!
                        if (position == dataList.size() - 1) {

                            position = 0;// en basa donsun
                        }

                        // sonuncu indiste "ileri" basilirsa da en sondaki sarkiya gidilsin
                        else {
                            position++;// en basa donsun
                        }


                        // Yeni gelinen indis degeri alindi ki sarkiya gecebilelim.
                        String newFilePath = dataList.get(position);

                        // Muzigimiz oynatilmaya baslanir.
                        // Veri kaynagi degistigi icin tekrardan try-catch icerisinde baslattik!
                        try {

                            mediaPlayer.setDataSource(newFilePath);
                            mediaPlayer.prepare();
                            mediaPlayer.start();

                            // Yeni muzigin baglantisini alip geci yaptik ama
                            // ismini de tekrar goruntulemeliyiz
                            String newMusicName = newFilePath.substring(newFilePath.lastIndexOf("/")+1);
                            binding.textMusicName.setText(newMusicName);

                            // Onceki text animasyonu sonlansin
                            binding.textMusicName.clearAnimation();
                            // Yeni gelen text'e animasyon eklenir
                            binding.textMusicName.startAnimation(animation);


                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        binding.buttonPlayPause.setImageDrawable(ContextCompat.getDrawable(MainActivity2.this,R.drawable.music_pause));// durdurma tusu resmi goruntulensin.
                    }
                });


            }
        };
        // Runnable iel ilgili islemlerin hepsi son bulduktan sonra handler ile kullanilabilmesi icin
        // post(runnable nesnesi) koyarak handler'i etkinlestirmis oluyoruz.
        handler.post(runnable);



    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        mediaPlayer.pause();
    }

    // Muzik suresini gostermeye yarayacak metot

    public String createTimeLabel(int currentPosition){

        // zaman etiketi,tum zaman
        String timeLabel;

        // dakika ve saniyeler
        int minute,second;

        // dakika = bulunulan konum / 1000 / 60
        // 1 dakika = 60 saniye , 1 saniye 1000 milisaniye

        minute = currentPosition / 1000 / 60;
        second = currentPosition / 1000 % 60;

        // Eger saniye degeri 0-10 arasi ise
        if(second < 10){
            // Sure gorunumu ayarlandi.
            // Bu tip sure gorunumlerini rahatca yapabilmek icin metodumuz String doner.
            timeLabel = minute + ":0" + second;
        }

        // Eger 10 saniye ve ustuyse
        // Zaten dogrudan kalan zamani gosterelim.
        // NOT: Oncesinde sadece 6,7... yerine 06,07 gozukmeli gibi ozel bir durum yoktur
        else{
            timeLabel = minute + ":" + second;
        }

        return timeLabel;
    }




}
