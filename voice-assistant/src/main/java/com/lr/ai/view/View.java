package com.lr.ai.view;

import com.lr.ai.api.VoiceAI;
import com.lr.ai.tool.AudioConvert;
import com.lr.ai.tool.MP3Player;
import com.lr.ai.util.StringUtil;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ran on 2018/11/15.
 */
public class View extends JFrame implements ActionListener {

	//录制音频的变量和类
	private static final long serialVersionUID = 1L;
	private AudioFormat audioFormat;
	private TargetDataLine targetDataLine;

	//保存文件路径地址
	private String workspace = System.getProperty("user.home");
	private String audio2textPath = workspace+"\\audio2text\\"+"VOICE"+new Date().getTime()/1000+".wav";
	private String text2audioPath;

	//定义所需要的组件
	private JLabel jlableTitle;
	private JLabel jlableQuestion;
	private JLabel jlableAnswer;
	private JButton jbtnCapture;
	private JPanel mMainJpanel;
	private JPanel mContentPanel;

	public static void main(String[] args) {
		//创造一个实例
		View view = new View();
	}

	//构造函数
	public View() {
		// 取消窗体标题栏和无边框效果
		setUndecorated(true);

		// 设置背景图片
		ImageIcon background = new ImageIcon("res/index_bg.png");
		JLabel label = new JLabel(background);
		label.setBounds(0, 0, background.getIconWidth(), background.getIconHeight());
		getLayeredPane().add(label, new Integer(Integer.MIN_VALUE));

		// 设置界面大小
		int frameWidth = background.getIconWidth();
		int frameHeight = background.getIconHeight();
		setSize(frameWidth, frameHeight);

		// 用户不可改变窗体大小
		setResizable(false);

		//用户点击叉号时关闭程序
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//设置按钮的图片和位置
		ImageIcon imgRecognize = new ImageIcon("res/btn_recognize.png");
		jbtnCapture = this.createImageButton(imgRecognize);
		jbtnCapture.setBounds(10, 150, imgRecognize.getIconWidth(), imgRecognize.getIconHeight());

		//设置字体位置和属性
		Font myFont = new Font("华文新魏", Font.BOLD, 24);
		jlableTitle = new JLabel("Windows 语音助手",JLabel.CENTER);
		jlableTitle.setFont(myFont);

		jlableQuestion = new JLabel("",JLabel.RIGHT);
		jlableQuestion.setFont(myFont);

		jlableAnswer = new JLabel("",JLabel.LEFT);
		jlableAnswer.setFont(myFont);

		//创建具有指定行数和列数的网格布局。Rows为行数，cols为列数。
		GridLayout gridlayout = new GridLayout(4, 0);
		gridlayout.setHgap(10);// 设置组件的水平间距
		mMainJpanel = new JPanel(gridlayout);
		mMainJpanel.setOpaque(false);//设置透明

		//将不同的空间放在Jpanel上
		mMainJpanel.add(jlableTitle);
		mMainJpanel.add(jlableQuestion);
		mMainJpanel.add(jlableAnswer);
		mMainJpanel.add(jbtnCapture);

		//对开始录音按钮进行注册监听
		jbtnCapture.setEnabled(true);
		jbtnCapture.addActionListener(this);
		jbtnCapture.setActionCommand("jbtnCapture");

		//将Jpanel放在ContentPanel上
		mContentPanel = new JPanel(new BorderLayout());
		mContentPanel.setOpaque(false);//设置透明
		mContentPanel.add(mMainJpanel, BorderLayout.CENTER);

		//设置窗口的属性
		setLocationRelativeTo(null);//设置窗体居中显示
		setContentPane(mContentPanel);//把该控件作为jForm的控制面板
		setVisible(true);//控件可以显示出来,


		//窗体可鼠标移动窗体
		final int[] x = new int[1];
		final int[] y = new int[1];
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				x[0] = e.getX();
				y[0] = e.getY();
			}
		});
		addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent e) {
				int left = getLocation().x;
				int top = getLocation().y;
				setLocation(left + e.getX() - x[0], top + e.getY() - y[0]);
			}
		});

	}

	//创建图片按钮
	public JButton createImageButton(ImageIcon img) {
		JButton button = new JButton("");
		button.setIcon(img);
		button.setSize(img.getIconWidth(), img.getIconHeight());
		button.setBackground(null);

		button.setBorder(null);
		button.setContentAreaFilled(false);
		button.setFocusPainted(false);

		return button;
	}

	//窗体事件
	public void actionPerformed(ActionEvent e) {
		//点击开始录音按钮后的动作
		if (e.getActionCommand().equals("jbtnCapture")) {
			//停止按钮可以启动
			jbtnCapture.setEnabled(false);
			//循环语音交互
			new CyclicSpeechThread().start();
			//调用录音的方法
			captureAudio();
		}
	}

	/**
	 * 播放音频交互
	 */
	public class PlayMp3Thread extends Thread{
		public void run(){
			MP3Player mp3 = new MP3Player(text2audioPath);
			mp3.play();
		}
	}

	/**
	 * 计时结束录制
	 */
	public class CyclicSpeechThread extends Thread {
		public void run() {
			int time = 4;// 结束时间.
			long t1 = System.currentTimeMillis();
			while (time >= 0) {
				if (System.currentTimeMillis() - t1 == 1000) {
					jlableTitle.setText("..请说我在听.. ");
					jlableAnswer.setText("<html><body><br>" + "提示:请在 "+ time+" 秒内完成语音交互" + "<body></html>");
					t1 = System.currentTimeMillis();
					time--;// 减一秒
				}
			}

			//调用停止录音的方法
			targetDataLine.stop();
			targetDataLine.close();
			String voicePath = audio2textPath;
			jlableTitle.setText("..我正在思考..");
			jlableQuestion.setText("");
			jlableAnswer.setText("");

			//调用语音助手
			VoiceAI voiceAI = new VoiceAI();
			Map<String, String> map = new HashMap<String, String>();
			try {
				map = voiceAI.hci(voicePath);
			} catch (Exception e1) {
				e1.printStackTrace();
			}

			//讲语音内容返回到窗体上
			jlableTitle.setText("");
			if(!StringUtil.isNullOrEmpty(map.get("question"))){
				jlableQuestion.setText(map.get("question"));
			}
			if(!StringUtil.isNullOrEmpty(map.get("answer"))){
				jlableAnswer.setText("<html><body><br><br>" + map.get("answer") + "<body></html>");
			}
			if(!StringUtil.isNullOrEmpty(map.get("url"))){
				openWebSite(map.get("url"));
			}

			//创建并启动线程（播放MP3）
			text2audioPath = map.get("text2audioPath");
			jlableTitle.setText("");
			new PlayMp3Thread().start();

			//获取语音播放时长
			int sleepTime = 0;
			AudioConvert audioConvert = new AudioConvert();
			try {
				sleepTime = audioConvert.getMp3TrackLength(new File(text2audioPath));
			} catch (TagException e) {
				e.printStackTrace();
			} catch (ReadOnlyFileException e) {
				e.printStackTrace();
			} catch (CannotReadException e) {
				e.printStackTrace();
			} catch (InvalidAudioFrameException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			//暂停语音播放时长，然后再开始计时
			time = sleepTime;// 结束时间.
			t1 = System.currentTimeMillis();
			while (time >= 0) {
				if (System.currentTimeMillis() - t1 == 1000) {
					t1 = System.currentTimeMillis();
					time--;// 减一秒
				}
			}

			//清楚界面文字内容
			jlableQuestion.setText("");
			jlableAnswer.setText("");

			//循环语音交互
			new CyclicSpeechThread().start();

			//调用录音的方法
			captureAudio();
		}

	}

	//region 录音
	public void captureAudio(){
		try {
			audioFormat = getAudioFormat();//构造具有线性 PCM 编码和给定参数的 AudioFormat。
			DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
			//根据指定信息构造数据行的信息对象，这些信息包括单个音频格式。此构造方法通常由应用程序用于描述所需的行。
			//lineClass - 该信息对象所描述的数据行的类
			//format - 所需的格式
			targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
			//如果请求 DataLine，且 info 是 DataLine.Info 的实例（至少指定一种完全限定的音频格式），
			//上一个数据行将用作返回的 DataLine 的默认格式。
			new CaptureThread().start();
			//开启线程
		} catch (Exception e){
			e.printStackTrace();
			System.exit(0);
		}
	}

	private AudioFormat getAudioFormat() {
		float sampleRate = 8000F;
		// 8000,11025,16000,22050,44100 采样率
		int sampleSizeInBits = 16;
		// 8,16 每个样本中的位数
		int channels = 2;
		// 1,2 信道数（单声道为 1，立体声为 2，等等）
		boolean signed = true;
		// true,false
		boolean bigEndian = false;
		// true,false 指示是以 big-endian 顺序还是以 little-endian 顺序存储音频数据。
		return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed,
				bigEndian);//构造具有线性 PCM 编码和给定参数的 AudioFormat。
	}

	class CaptureThread extends Thread {
		public void run() {
			AudioFileFormat.Type fileType = null;
			//指定的文件类型
			File audioFile = null;
			//设置文件类型和文件扩展名
			//根据选择的单选按钮。
			fileType = AudioFileFormat.Type.WAVE;
			audioFile = new File(audio2textPath);
			try {
				targetDataLine.open(audioFormat);
				//format - 所需音频格式
				targetDataLine.start();
				//当开始音频捕获或回放时，生成 START 事件。
				AudioSystem.write(new AudioInputStream(targetDataLine),fileType, audioFile);
				//new AudioInputStream(TargetDataLine line):构造从指示的目标数据行读取数据的音频输入流。该流的格式与目标数据行的格式相同,line - 此流从中获得数据的目标数据行。
				//stream - 包含要写入文件的音频数据的音频输入流
				//fileType - 要写入的音频文件的种类
				//out - 应将文件数据写入其中的外部文件

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	//endregion

	/**
	 * 打开web界面
	 * @param url
	 */
	public void openWebSite(String url){
		try {
			java.net.URI uri = java.net.URI.create(url);
			// 获取当前系统桌面扩展
			Desktop dp = Desktop.getDesktop();
			// 判断系统桌面是否支持要执行的功能
			if (dp.isSupported(Desktop.Action.BROWSE)) {
				//File file = new File("D:\\aa.txt");
				//dp.edit(file);// 　编辑文件
				dp.browse(uri);// 获取系统默认浏览器打开链接
				// dp.open(file);// 用默认方式打开文件
				// dp.print(file);// 用打印机打印文件
			}
		} catch (NullPointerException e) {
			// 此为uri为空时抛出异常
			e.printStackTrace();
		} catch (IOException e) {
			// 此为无法获取系统默认浏览器
			e.printStackTrace();
		}
	}

}