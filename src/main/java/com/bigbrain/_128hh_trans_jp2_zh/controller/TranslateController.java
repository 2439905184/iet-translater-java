package com.bigbrain._128hh_trans_jp2_zh.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.bigbrain._128hh_trans_jp2_zh.Launcher;
import com.bigbrain._128hh_trans_jp2_zh.util.Toast;
import com.bigbrain._128hh_trans_jp2_zh.util.TranslateApi_Jp2Zh;
import com.bigbrain._128hh_trans_jp2_zh.util.Utils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;

public class TranslateController {

	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private TextField src;

	@FXML
	private TextField dst;

	@FXML
	private TextArea console;

	/**
	 * 控制转换启停
	 */
	private volatile boolean stop = true;

	@FXML
	void openSrc(ActionEvent event) {

		if (src.getText() == null || src.getText().trim().length() == 0) {
			Toast.toast("请先设置一个有效的iet文件目录");
			return;
		}
		File srcDir = new File(src.getText().trim());
		if (!srcDir.exists()) {
			Toast.toast("你设置的源iet目录" + src.getText().trim() + "不存在或无效");
			return;
		}
		if (Utils.isWindows()) {
			Utils.openDir(new File(src.getText().trim()));
			return;
		}
	}

	@FXML
	void openDst(ActionEvent event) {

		if (dst.getText() == null || dst.getText().trim().length() == 0) {
			Toast.toast("请先设置一个有效的文件目录来存放转换后的iet的文件");
			return;
		}
		File dstDir = new File(dst.getText().trim());
		if (!dstDir.exists()) {
			Toast.toast("你设置的输出目录" + dst.getText().trim() + "不存在或无效");
			return;
		}
		if (Utils.isWindows()) {
			Utils.openDir(new File(dst.getText().trim()));
			return;
		}
	}

	@FXML
	void chooseDst(ActionEvent event) {
		DirectoryChooser fileChooser = new DirectoryChooser();
		if (Launcher.lastOpenDirDst != null) {
			fileChooser.setInitialDirectory(Launcher.lastOpenDirDst);
		}
		File dir = fileChooser.showDialog(null);
		if (dir != null) {
			dst.setText(dir.getAbsolutePath());
			Launcher.lastOpenDirDst = dir;
			Launcher.setProperty("last_open_dir_dst", dir.getAbsolutePath());
		}
	}

	@FXML
	void chooseSrc(ActionEvent event) {
		DirectoryChooser fileChooser = new DirectoryChooser();
		if (Launcher.lastOpenDirSrc != null) {
			fileChooser.setInitialDirectory(Launcher.lastOpenDirSrc);
		}
		File dir = fileChooser.showDialog(null);
		if (dir != null) {
			File[] files = dir.listFiles(new FileFilter() {

				@Override
				public boolean accept(File pathname) {

					return pathname.getName().endsWith(".iet") && !pathname.getName().startsWith("new");
				}
			});
			if (files == null || files.length == 0) {
				Toast.toast("您选择的文件夹下没有检测到iet文件");
				return;
			} else {
				Launcher.lastOpenDirSrc = dir;
				src.setText(dir.getAbsolutePath());
				dst.setText(dir.getAbsolutePath());// 默认保存目录为源目录
				Launcher.setProperty("last_open_dir_src", dir.getAbsolutePath());
				log("检测到：" + files.length + "个要转换的文件,分别是：");
				for (File iet : files) {
					log(iet.getAbsolutePath());
				}
			}
		}
	}

	/**
	 * 清除日志
	 * 
	 * @param event
	 */
	@FXML
	void clearLog(ActionEvent event) {
		console.clear();
		console.setText("\n");
	}

	/**
	 * 停止转换
	 * 
	 * @param event
	 */
	@FXML
	void doStop(ActionEvent event) {
		if (stop) {
			Toast.toast("转换工作还没开始");
			return;
		}
		stop = true;
		Toast.toast("正在停止转工作");
	}

	@FXML
	void doParseAndTranslate(ActionEvent event) {

		if (!stop) {
			Toast.toast("转换工作正在进行，请先中断再转换");
			return;
		}
		if (src.getText() == null || src.getText().isBlank()) {
			Toast.toast("请先选择iet源文件夹");
			src.requestFocus();
			return;
		}
		if (dst.getText() == null || dst.getText().isBlank()) {
			Toast.toast("请设置一个输出文件夹用来保存转换后的文件");
			dst.requestFocus();
			return;
		}
		File srcDir = new File(src.getText().trim());
		if (!srcDir.exists()) {
			Toast.toast("文件夹：" + src.getText() + "不存在");
			return;
		}
		File dstDir = new File(dst.getText().trim());
		if (!dstDir.exists()) {
			dstDir.mkdirs();
		}
		File[] files = srcDir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File pathname) {

				return pathname.getName().endsWith(".iet") && !pathname.getName().startsWith("new");
			}
		});
		if (files == null || files.length == 0) {
			Toast.toast("您选择的文件夹下没有检测到iet文件");
			return;
		}
		stop = false;
		new Thread(() -> {
			for (File iet : files) {
				if (stop) {
					Toast.toast("转换工作被手动停止");
					log("转换工作被手动停止");
					return;
				} else {
					log("正在解析" + iet.getAbsolutePath());
					parseAndTranslateOneIet(iet);
				}
			}
			log("转换工作完成,请打开输出目录" + dst.getText().trim() + "进行查看");
			stop = true;
		}).start();
	}

	/**
	 * 对单个文件进行解析转换
	 * 
	 * @param iet
	 */
	void parseAndTranslateOneIet(File iet) {
		BufferedReader br = null;
		String line = null;
		long lineNumber = 0;
		File newIet = new File(new File(dst.getText().trim()), "new_" + iet.getName());
		StringBuffer newContent = new StringBuffer();
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(iet), "UTF-8"));
			while ((line = br.readLine()) != null) {
				lineNumber++;
				Pattern pb = Pattern.compile("(\\[).*?(\\])");
				Matcher mb = pb.matcher(line.trim());
				while (mb.find()) {
					String str1 = mb.group().trim();
					Pattern inner = Pattern.compile("text=[\\s\\S]*\"");
					Matcher t = inner.matcher(str1);
					while (t.find()) {
						if (stop) {
							log("收到停止指令，停止当前文件：" + iet.getAbsolutePath() + "的转换任务");
							return;
						}
						try {
							String str = t.group();
							String text = str.split("=")[1].replaceAll("\"", "");
							log("原始行：" + line);
							log("检测到第：" + lineNumber + " 行出现目标文本：[" + text + "]，准备翻译");
							// 翻译工具（VIP付费接口）
							TranslateApi_Jp2Zh tool = new TranslateApi_Jp2Zh("_128hh_trans_jp2_zh", text);
							String result = tool.getTransResult();
							JsonObject jobj = JsonParser.parseString(result).getAsJsonObject();
							if(jobj.get("code") != null) {
								int code = jobj.get("code").getAsInt();
								if(code == -10) {
									int wordCnt = jobj.get("wordCnt").getAsInt();
									int reqCnt = jobj.get("requestCnt").getAsInt();
									log("==========================");
									log("请求翻译句子数或者翻译字数超过了单次结算限制。需要进行结算才能继续翻译。");
									log("结算费用 100句/元或者5000字/元；本次结算，总共翻译："+reqCnt+"条句子，"+wordCnt+"个字。按句子费用大概："+reqCnt*0.01+"元。按字数费用大概："+wordCnt/5000.0+"元；两者取小。");
									log("联系18588224003完成微信转账，结算清0");
									log("==========================");
									Toast.toast("请求翻译句子数或者翻译字数超过了单次结算限制。需要进行结算才能继续翻译。具体细节请看日志界面");
									return;
								}
								if(code <= 0) {
									Toast.toast("参数校验失败，请联系：18588224003");
									return;
								}
							}
							JsonArray jarr = jobj.get("trans_result").getAsJsonArray();
							String src = jarr.get(0).getAsJsonObject().get("src").getAsString();
							String trans = jarr.get(0).getAsJsonObject().get("dst").getAsString();
							src = URLDecoder.decode(src, "utf-8");
							trans = URLDecoder.decode(trans, "utf-8");
							log("翻译后：" + trans);
							line = line.replace(src, trans);
							log("替换后：" + line.replace(src, trans));
						} catch (Exception e) {
							log("翻译：" + iet.getAbsolutePath() + "的第" + lineNumber + "行出现问题：" + e.getMessage() + ",忽略异常继续运行");
							continue;
						}
					}
				}
				newContent.append(line).append("\n");
			}
			log(iet.getAbsolutePath() + "转换成功-SUCCESS，输出文件为：" + newIet.getAbsolutePath());
			log("=================================================");
			Utils.outputText(newContent.toString(), newIet);
		} catch (Exception e) {
			e.printStackTrace();
			log(e.getMessage());
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private synchronized void log(String message) {

		Platform.runLater(() -> {
			try {
				console.appendText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "\t" + message);
				console.appendText("\n");
			} catch (Exception e) {
			}
		});
	}

	@FXML
	void initialize() {
		assert src != null : "fx:id=\"src\" was not injected: check your FXML file 'main.fxml'.";
		assert dst != null : "fx:id=\"dst\" was not injected: check your FXML file 'main.fxml'.";
		assert console != null : "fx:id=\"console\" was not injected: check your FXML file 'main.fxml'.";
		console.setText("\n");
		String last_open_dir_src = Launcher.getProperty("last_open_dir_src");
		if (last_open_dir_src != null && new File(last_open_dir_src).exists()) {
			src.setText(last_open_dir_src.trim());
		}
		String last_open_dir_dst = Launcher.getProperty("last_open_dir_dst");
		if (last_open_dir_dst != null && new File(last_open_dir_dst).exists()) {
			dst.setText(last_open_dir_dst.trim());
		}
	}
}
