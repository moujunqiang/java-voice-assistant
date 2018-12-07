package com.lr.ai.tool;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

import javazoom.spi.mpeg.sampled.file.MpegAudioFileReader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * Created by ran on 2018/11/15.
 */
public class ConvertMP32PCM {

    /**
     * MP3转换PCM文件方法
     *
     * @param mp3filepath
     *            原始文件路径
     * @param pcmfilepath
     *            转换文件的保存路径
     * @throws Exception
     */
    public static void convertMP32PCM(String mp3filepath, String pcmfilepath) throws Exception {
        AudioInputStream audioInputStream = getPcmAudioInputStream(mp3filepath);
        AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, new File(pcmfilepath));
    }

    private static AudioInputStream getPcmAudioInputStream(String mp3filepath) {
        File mp3 = new File(mp3filepath);
        AudioInputStream audioInputStream = null;
        AudioFormat targetFormat = null;
        try {
            AudioInputStream in = null;
             MpegAudioFileReader mp = new MpegAudioFileReader();
            in = mp.getAudioInputStream(mp3);
            AudioFormat baseFormat = in.getFormat();
            targetFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, baseFormat.getSampleRate(), 16,
                    baseFormat.getChannels(), baseFormat.getChannels() * 2, baseFormat.getSampleRate(), false);
            audioInputStream = AudioSystem.getAudioInputStream(targetFormat, in);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return audioInputStream;
    }


    public static void main(String[] args) {
        String path = "C:\\Users\\ran\\audio2text\\";
        String mp3filepath = path + "VOICE1542286983.mp3";
        String pcmfilepath = path + "VOICE1542286983.pcm";

        try {
            ConvertMP32PCM.convertMP32PCM(mp3filepath, pcmfilepath);
            //      MP3ToPcm.playMP3(mp3filepath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}