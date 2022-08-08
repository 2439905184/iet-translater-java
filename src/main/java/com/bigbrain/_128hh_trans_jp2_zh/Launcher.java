package com.bigbrain._128hh_trans_jp2_zh;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Launcher extends Application {

	/**
	 * 记录上一次打开目录
	 */
	public static File lastOpenDirSrc = null;
	/**
	 * 记录上一次打开目录
	 */
	public static File lastOpenDirDst = null;
	private static File config = null;
	public static Properties properties = new Properties();
	static {
		config = new File(System.getProperty("user.home"),"._128hh_trans_jp2_zh.properties");
		if(config.exists()) {
			try {
				properties.load(new FileInputStream(config));
				if(getProperty("last_open_dir_src") != null && new File(getProperty("last_open_dir_src").trim()).exists()) {
					lastOpenDirSrc = new File(getProperty("last_open_dir_src").trim());
				}
				if(getProperty("last_open_dir_dst") != null && new File(getProperty("last_open_dir_dst").trim()).exists()) {
					lastOpenDirDst = new File(getProperty("last_open_dir_dst").trim());
				}
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * 获取属性值
	 * 
	 * @param key
	 * @return
	 */
	public static String getProperty(String key) {

		return properties.getProperty(key, "");
	}

	/**
	 * 设置属性值并保存到磁盘
	 * 
	 * @param key
	 * @return
	 */
	public static void setProperty(String key, String value) {

		try {
			properties.setProperty(key, value);
			OutputStream out = new FileOutputStream(config);
			properties.store(out, "");
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	@Override
	public void start(Stage primaryStage) {
		try {

			Parent root = FXMLLoader.load(getClass().getResource("/main.fxml"));
			Scene scene = new Scene(root);
			scene.getStylesheets().add("bootstrapfx.css");
			primaryStage.setTitle("iet文件汉化处理工具");
			primaryStage.setResizable(false);
			primaryStage.setScene(scene);
			primaryStage.show();
			primaryStage.getIcons().add(new Image(Launcher.class.getResourceAsStream("/icon.png")));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}