package vn.toanlop3;
import android.content.Context;
import android.media.*;
/** Original offline music and effect resources. Music is suspended with the Activity. */
final class GameAudio {
 private final Context context;private final SoundPool pool;private final int[] effects=new int[5];private final boolean[] ready=new boolean[5];private MediaPlayer music;private int world=-1;private boolean enabled=true;
 GameAudio(Context c){context=c.getApplicationContext();pool=new SoundPool.Builder().setMaxStreams(5).setAudioAttributes(new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_GAME).setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build()).build();pool.setOnLoadCompleteListener((p,id,status)->{for(int i=0;i<effects.length;i++)if(effects[i]==id)ready[i]=status==0;});int[] r={R.raw.tap,R.raw.correct,R.raw.retry,R.raw.jump,R.raw.treasure};for(int i=0;i<r.length;i++)effects[i]=pool.load(context,r[i],1);}
 void setEnabled(boolean b){enabled=b;if(!b)pause();else if(music!=null){try{music.start();}catch(RuntimeException ignored){}}}
 void music(int theme){if(!enabled)return;theme=Math.max(0,Math.min(3,theme));if(world==theme&&music!=null){try{if(!music.isPlaying())music.start();}catch(RuntimeException ignored){}return;}if(music!=null){music.release();music=null;}int[] r={R.raw.music_0,R.raw.music_1,R.raw.music_2,R.raw.music_3};world=theme;try{music=MediaPlayer.create(context,r[theme]);if(music!=null){music.setLooping(true);music.setVolume(.23f,.23f);music.start();}}catch(RuntimeException ignored){}}
 void play(int i){if(enabled&&i>=0&&i<effects.length&&ready[i])pool.play(effects[i],.72f,.72f,1,0,1f);}
 void pause(){if(music!=null)try{if(music.isPlaying())music.pause();}catch(RuntimeException ignored){}}
 void close(){pause();if(music!=null)music.release();music=null;pool.release();}
}
