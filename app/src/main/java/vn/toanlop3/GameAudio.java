package vn.toanlop3;
import android.media.*;
final class GameAudio {
 private static final int RATE=22050;
 private final AudioTrack[] tracks=new AudioTrack[3];
 GameAudio(){
  tracks[0]=make(new double[]{880},.075,.16);tracks[1]=make(new double[]{523.25,659.25,783.99,1046.50},.10,.21);tracks[2]=make(new double[]{440,392},.12,.13);
 }
 private AudioTrack make(double[] notes,double duration,double volume){
  int n=(int)(RATE*duration);short[] pcm=new short[notes.length*n];
  for(int j=0;j<notes.length;j++)for(int i=0;i<n;i++){double t=(double)i/RATE,e=Math.min(1,i/(RATE*.006))*Math.max(0,1-(double)i/n),a=2*Math.PI*notes[j]*t;pcm[j*n+i]=(short)(32767*volume*e*(Math.sin(a)+.16*Math.sin(a*2)));}
  AudioTrack tr=new AudioTrack.Builder().setAudioAttributes(new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_GAME).setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build()).setAudioFormat(new AudioFormat.Builder().setEncoding(AudioFormat.ENCODING_PCM_16BIT).setSampleRate(RATE).setChannelMask(AudioFormat.CHANNEL_OUT_MONO).build()).setTransferMode(AudioTrack.MODE_STATIC).setBufferSizeInBytes(pcm.length*2).build();tr.write(pcm,0,pcm.length);return tr;
 }
 void play(int i){AudioTrack t=tracks[i];if(t==null||t.getState()!=AudioTrack.STATE_INITIALIZED)return;if(t.getPlayState()==AudioTrack.PLAYSTATE_PLAYING)t.stop();t.reloadStaticData();t.play();}
 void close(){for(AudioTrack t:tracks)if(t!=null){try{t.release();}catch(RuntimeException ignored){}}}
}
