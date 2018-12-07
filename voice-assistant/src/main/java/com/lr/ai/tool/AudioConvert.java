package com.lr.ai.tool;

/**
 * Created by ran on 2018/11/16.
 */

import org.bytedeco.javacpp.avcodec;
import org.bytedeco.javacv.*;
import org.bytedeco.javacv.FrameRecorder.Exception;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.mp3.MP3AudioHeader;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.TagException;

import java.beans.Encoder;
import java.io.File;
import java.io.IOException;


/**
 * 音频参数转换(包含采样率、编码,位数,通道数)
 *
 * @author eguid
 *
 */
public class AudioConvert {

    // 测试
    public static void main(String[] args) throws ReadOnlyFileException, IOException, TagException, InvalidAudioFrameException, CannotReadException {
        int i = getMp3TrackLength(new File("C:\\Users\\ran\\text2audio\\VOICE1542450027.mp3"));
        System.out.println(i);
    }

    /**
     * 获取音频文件时长
     * @param mp3File
     * @return
     * @throws TagException
     * @throws ReadOnlyFileException
     * @throws CannotReadException
     * @throws InvalidAudioFrameException
     * @throws IOException
     */
    public static int getMp3TrackLength(File mp3File) throws TagException, ReadOnlyFileException, CannotReadException, InvalidAudioFrameException, IOException {
        try {
            MP3File f = (MP3File) AudioFileIO.read(mp3File);
            MP3AudioHeader audioHeader = (MP3AudioHeader)f.getAudioHeader();
            return audioHeader.getTrackLength();
        } catch(Exception e) {
            return -1;
        }
    }

    /**
     * 通用音频格式参数转换
     *
     * @param inputFile
     * -导入音频文件
     * @param outputFile
     * -导出音频文件
     * @param audioCodec
     * -音频编码
     * @param sampleRate
     * -音频采样率
     * @param audioBitrate
     * -音频比特率
     */
    public static void convert(String inputFile, String outputFile, int audioCodec, int sampleRate, int audioBitrate,
                               int audioChannels) {
        Frame audioSamples = null;
        // 音频录制(输出地址,音频通道)
        FFmpegFrameRecorder recorder = null;
        //抓取器
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputFile);
        // 开启抓取器
        if (start(grabber)) {
            recorder = new FFmpegFrameRecorder(outputFile, audioChannels);
            recorder.setAudioOption("crf", "0");
            recorder.setAudioCodec(audioCodec);
            recorder.setAudioBitrate(audioBitrate);
            recorder.setAudioChannels(audioChannels);
            recorder.setSampleRate(sampleRate);
            recorder.setAudioQuality(0);
            recorder.setAudioOption("aq", "10");
            // 开启录制器
            if (start(recorder)) {
                try {
            // 抓取音频
                    while ((audioSamples = grabber.grab()) != null) {
                        recorder.setTimestamp(grabber.getTimestamp());
                        recorder.record(audioSamples);
                    }
                } catch (FrameGrabber.Exception e1) {
                    System.err.println("抓取失败");
                } catch (Exception e) {
                    System.err.println("录制失败");
                }
                stop(grabber);
                stop(recorder);
            }
        }
    }
    public static boolean start(FrameGrabber grabber) {
        try {
            grabber.start();
            return true;
        } catch (FrameGrabber.Exception e2) {
            try {
                System.err.println("首次打开抓取器失败,准备重启抓取器...");
                grabber.restart();
                return true;
            } catch (FrameGrabber.Exception e) {
                try {
                    System.err.println("重启抓取器失败,正在关闭抓取器...");
                    grabber.stop();
                } catch (FrameGrabber.Exception e1) {
                    System.err.println("停止抓取器失败!");
                }
            }
        }
        return false;
    }
    public static boolean start(FrameRecorder recorder) {
        try {
            recorder.start();
            return true;
        } catch (Exception e2) {
            try {
                System.err.println("首次打开录制器失败!准备重启录制器...");
                recorder.stop();
                recorder.start();
                return true;
            } catch (Exception e) {
                try {
                    System.err.println("重启录制器失败!正在停止录制器...");
                    recorder.stop();
                } catch (Exception e1) {
                    System.err.println("关闭录制器失败!");
                }
            }
        }
        return false;
    }
    public static boolean stop(FrameGrabber grabber) {
        try {
            grabber.flush();
            grabber.stop();
            return true;
        } catch (FrameGrabber.Exception e) {
            return false;
        } finally {
            try {
                grabber.stop();
            } catch (FrameGrabber.Exception e) {
                System.err.println("关闭抓取器失败");
            }
        }
    }
    public static boolean stop(FrameRecorder recorder) {
        try {
            recorder.stop();
            recorder.release();
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            try {
                recorder.stop();
            } catch (Exception e) {
            }
        }
    }
}