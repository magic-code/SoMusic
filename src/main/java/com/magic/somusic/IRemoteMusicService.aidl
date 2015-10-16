// IRemoteMusicService.aidl
package com.magic.somusic;

// Declare any non-default types here with import statements

interface IRemoteMusicService {

     /**入播放音乐*/
      void play();
      /**下一首音乐*/
      pvoid next();
      /**上一首音乐*/
      void previous();
      /**暂停播放*/
      void pause();
      /**停止播放*/
      void stop();


}
