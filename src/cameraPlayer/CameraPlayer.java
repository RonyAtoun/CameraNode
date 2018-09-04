package cameraPlayer;


import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.MediaPlayer;

public class CameraPlayer {
   private final JFrame frame;
   private final EmbeddedMediaPlayerComponent mediaPlayerComponent;
   MediaPlayer mediaPlayer;
   String bannerText;
   int xPos, yPos;
   String mrl[] = {"SampleVideo_1080x720_5mb.mp4", "SampleVideo_720x480_5mb.mp4", "SampleVideo_640x360_5mb.mp4", "SampleVideo_360x240_5mb.mp4"};

   public CameraPlayer(String banner) {
	   int value = Integer.parseInt(banner.replaceAll("[^0-9]", ""));
       bannerText = banner;
       switch (value-1) {
       case 0: 
           xPos = 550;
           yPos = 100;
           break;
       case 1:
           xPos = 1180;
           yPos = 100;
           break;
       case 2:
           xPos = 550;
           yPos = 550;
           break;
       case 3:
           xPos = 1180;
           yPos = 550;
       }
       frame = new JFrame(banner);
       frame.setBounds(xPos, yPos, 600, 400);
       frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
       frame.addWindowListener(new WindowAdapter() {
           @Override
           public void windowClosing(WindowEvent e) {
               mediaPlayerComponent.release();
               System.exit(0);
           }
       });
       mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
       mediaPlayer = mediaPlayerComponent.getMediaPlayer();
      frame.setContentPane(mediaPlayerComponent);
       frame.setVisible(true);
       mediaPlayerComponent.getMediaPlayer().playMedia(mrl[3]);
       mediaPlayer.setRepeat(true);
       
   }
   synchronized public void updateBannerWithBW(String update){
       String title = bannerText+" "+update;
       frame.setTitle(title);
   }
   synchronized public void switchCameraBW (int bwLevel) {
       mediaPlayer.prepareMedia(mrl[bwLevel]);
       mediaPlayer.play();     
   }

}