package com.bigbrain._128hh_trans_jp2_zh.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Utils {

	public static final String OS_WINDOWS = "windows";
	public static final String OS_LINUX = "linux";
	public static final String OS_MAC = "mac";
	public static final String OS_IOS = "ios";
	public static final String OS_ANDROID = "android";
	public static final String OS = System.getProperty("os.name").toLowerCase();
	private static ExecutorService pool = Executors.newCachedThreadPool();

	public static boolean isBlank(String str) {

		return str == null || str.trim().length() == 0;
	}
	
	/**
	 * 	将字节数组转换为字节数组
	 * @param content
	 * @return
	 */
	public static byte[] intArray2ByteArray(int [] content) {
		
		//long s1 = System.currentTimeMillis();
		byte [] con = new byte[content.length*4];
		try {
			for(int i = 0 ; i < content.length ; i++) {
				byte[] oneBytes = Utils.int2Bytes(content[i]);
				int k = i*4;
				con[k] = oneBytes[0]; 
				con[k+1] = oneBytes[1];
				con[k+2] = oneBytes[2];
				con[k+3] = oneBytes[3]; 
			}
			//long s2 = System.currentTimeMillis();
			//System.out.println("intArray2ByteArray数组转换耗时："+(s2-s1));
			return con;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static int[] byteArray2IntArray(byte[] decompressedData) {
		
		int len = decompressedData.length/4;
		int [] c = new int[len];
		for(int i = 0; i< len; i++) {
			byte[] t = {decompressedData[i*4],decompressedData[i*4+1],decompressedData[i*4+2],decompressedData[i*4+3]};
			c[i] = Utils.bytes2Int(t);
		}
		return null;
	}
	
	/**
	 * 	打印最后n个字节
	 * @param content
	 * @param n
	 */
	public static void printLastNBytes(byte [] content,int n) {
		
		int last = content.length-1;
		for(int i = 0 ; i < n ; i++) {
			System.out.println((n-i)+"->"+content[last-i]);
		}
	}

	/**
	 *	 计算一个最佳的send_buff(返回字节数)
	 * @param bandwidth	带宽，单位Mb/s
	 * @param latency	延迟，单位ms
	 * @return
	 */
	public static int calcSendBuff(double bandwidth,double latency) {
		
		double realLatency = latency/1000;//换算成秒
		double realBandwidth = bandwidth*1024*1024/8;//换算成字节
		return (int)(realBandwidth*realLatency);
	}
	
	/**
	 * 通过线程池执行线程
	 * 
	 * @param r
	 */
	public static void executeRunnable(Runnable r) {
		pool.execute(r);
	}

	public static String date2str(Date date, String fmt) {
		if (date == null || fmt == null)
			return "";
		SimpleDateFormat sdf = new SimpleDateFormat(fmt);
		return sdf.format(date);
	}

	public static void openDir(File userDir) {
		if (Utils.isWindows()) {
			try {
				Runtime.getRuntime().exec("explorer.exe /select," + userDir.getAbsolutePath());
			} catch (IOException e) {
				Toast.toast("工作目录为：" + userDir.getAbsolutePath(), 10000);
			}
			return;
		}
	}

	/**
	 * 获取类Unix系统的IP
	 * 
	 * @return
	 * @throws Exception
	 */
	public static String getIp() {

		try {
			Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
			if (null == netInterfaces) {
				throw new Exception("获取类Unix系统的IP失败");
			}
			InetAddress ip = null;
			while (netInterfaces.hasMoreElements()) {
				NetworkInterface ni = netInterfaces.nextElement();
				if (ni.isUp()) {
					Enumeration<InetAddress> addressEnumeration = ni.getInetAddresses();
					while (addressEnumeration.hasMoreElements()) {
						ip = addressEnumeration.nextElement();
						if (ip.isSiteLocalAddress() && !ip.isLoopbackAddress() && ip.getHostAddress().indexOf(":") == -1) {
							return ip.getHostAddress();
						}
					}
				}
			}
			return "127.0.0.1";
		} catch (Exception e) {
			return "127.0.0.1";
		}
	}

	/**
	 * 将可序列化对象转换成字节码数组
	 * 
	 * @param <T>
	 * @param t
	 * @return
	 */
	public static <T extends Serializable> byte[] serialize(T t) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream o;
		try {
			o = new ObjectOutputStream(out);
			o.writeObject(t);
			return out.toByteArray();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 序列化输出到磁盘
	 * 
	 * @param <T>
	 * @param t
	 * @return
	 */
	public static <T extends Serializable> void serializeToFile(T t, String path) {

		byte[] bytes = serialize(t);
		FileOutputStream out;
		try {
			// KitLogUtil.info("持久化："+t.getClass().getCanonicalName()+"到路径："+path);
			out = new FileOutputStream(path);
			out.write(bytes);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 序列化输出到磁盘
	 * 
	 * @param <T>
	 * @param t
	 * @return
	 */
	public static <T extends Serializable> void serializeToFile(T t, File file) {

		serializeToFile(t, file.getAbsolutePath());
	}

	/**
	 * 将可序列化对象转换成字节码数组
	 * 
	 * @param <T>
	 * @param t
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Serializable> T deserialize(byte[] content) {

		ObjectInputStream objIn = null;
		try {
			objIn = new ObjectInputStream(new ByteArrayInputStream(content));
			return (T) objIn.readObject();
		} catch (Exception e) {
			return null;
		}
	}

	public static byte[] getBytes(File file) {

		// Log.info("开始获取文件："+file.getAbsolutePath());
		try {
			InputStream is = new FileInputStream(file);
			byte[] content = is.readAllBytes();
			is.close();
			// Log.info("结束获取文件："+file.getAbsolutePath());
			return content;
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 *      * byte数组转int类型的对象      * @param bytes      * @return     
	 */
	public static int bytes2Int(byte[] bytes) {

		if (bytes == null || bytes.length != 4) {

			throw new RuntimeException("参数非法");
		}
		return (bytes[0] & 0xff) << 24 | (bytes[1] & 0xff) << 16 | (bytes[2] & 0xff) << 8 | (bytes[3] & 0xff);
	}

	public static byte[] int2Bytes(int num) {
		byte[] bytes = new byte[4];
		// 通过移位运算，截取低8位的方式，将int保存到byte数组
		bytes[0] = (byte) (num >>> 24);
		bytes[1] = (byte) (num >>> 16);
		bytes[2] = (byte) (num >>> 8);
		bytes[3] = (byte) num;
		return bytes;
	}

	public static void sleep(int mills) {
		try {
			Thread.sleep(mills);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 从zip提取文件并输出到目标路径(按后缀匹配第一个文件)
	 * 
	 * @param src
	 * @param suffix
	 * @param dst
	 */
	public static void extractFileFromZip(String src, String suffix, String dst) {
		ZipInputStream zis = null;
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(dst);
			zis = new ZipInputStream(new FileInputStream(src));
			ZipEntry ze = null;
			while ((ze = zis.getNextEntry()) != null) {
				String name = ze.getName();
				if (name.endsWith(suffix)) {
					break;
				}
			}

			if (ze == null) {
				throw new RuntimeException("压缩包里找不到文件：" + suffix);
			}
			byte[] buff = new byte[8192];
			int len = -1;
			while ((len = zis.read(buff, 0, 8192)) != -1) {
				out.write(buff, 0, len);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			try {
				if (zis != null) {
					zis.close();
				}
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 版本是否更新
	 * 
	 * @param currVer
	 * @param hisVer
	 * @return
	 */
	public static boolean isVersionNewer(String currVer, String hisVer) {
		String[] currs = currVer.split("\\.");
		int curMaj = Integer.parseInt(currs[0]);
		int curMid = Integer.parseInt(currs[1]);
		int curMin = Integer.parseInt(currs[2]);

		String[] hiss = hisVer.split("\\.");
		int hisMaj = Integer.parseInt(hiss[0]);
		int hisMid = Integer.parseInt(hiss[1]);
		int hisMin = Integer.parseInt(hiss[2]);

		if (curMaj > hisMaj) {
			return true;
		}
		if (curMaj == hisMaj && curMid > hisMid) {
			return true;
		}
		if (curMaj == hisMaj && curMid == hisMid && curMin > hisMin) {
			return true;
		}
		return false;
	}

	/**
	 * @return
	 */
	public static String os() {

		if (OS.indexOf("windows") != -1) {
			return OS_WINDOWS;
		}
		if (OS.indexOf("linux") != -1) {
			return OS_LINUX;
		}
		if (OS.indexOf("mac") != -1) {
			return OS_MAC;
		}
		return null;

	}

	public static boolean isLinux() {

		return OS.indexOf("linux") != -1;
	}

	public static boolean isMac() {

		return OS.indexOf("mac") != -1;
	}

	public static boolean isWindows() {

		return OS.indexOf("windows") != -1;
	}

	public static void outputText(String string, File newIet)throws Exception {
		
		if(string == null) {
			return;
		}
		FileWriter fw = new FileWriter(newIet);
		fw.write(string);
		fw.close();
	}
}
