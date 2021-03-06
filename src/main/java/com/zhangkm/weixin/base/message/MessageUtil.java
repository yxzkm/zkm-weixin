package com.zhangkm.weixin.base.message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.core.util.QuickWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.XppDriver;

import com.zhangkm.weixin.base.message.res.Article;
import com.zhangkm.weixin.base.message.res.MusicMessage;
import com.zhangkm.weixin.base.message.res.NewsMessage;
import com.zhangkm.weixin.base.message.res.TextMessage;


public class MessageUtil {

	/**
	 * 返回消息类型：文本
	 */
	public static final String RESP_MESSAGE_TYPE_TEXT = "text";

	/**
	 * 返回消息类型：音乐
	 */
	public static final String RESP_MESSAGE_TYPE_MUSIC = "music";

	/**
	 * 返回消息类型：图文
	 */
	public static final String RESP_MESSAGE_TYPE_NEWS = "news";

	/**
	 * 请求消息类型：文本
	 */
	public static final String REQ_MESSAGE_TYPE_TEXT = "text";

	/**
	 * 请求消息类型：图片
	 */
	public static final String REQ_MESSAGE_TYPE_IMAGE = "image";

	/**
	 * 请求消息类型：链接
	 */
	public static final String REQ_MESSAGE_TYPE_LINK = "link";

	/**
	 * 请求消息类型：地理位置
	 */
	public static final String REQ_MESSAGE_TYPE_LOCATION = "location";

	/**
	 * 请求消息类型：音频
	 */
	public static final String REQ_MESSAGE_TYPE_VOICE = "voice";

	/**
	 * 请求消息类型：推送
	 */
	public static final String REQ_MESSAGE_TYPE_EVENT = "event";

	/**
	 * 事件类型：subscribe(订阅)
	 */
	public static final String EVENT_TYPE_SUBSCRIBE = "subscribe";

	/**
	 * 事件类型：unsubscribe(取消订阅)
	 */
	public static final String EVENT_TYPE_UNSUBSCRIBE = "unsubscribe";

	/**
	 * 事件类型：CLICK(自定义菜单点击事件)
	 */
	public static final String EVENT_TYPE_CLICK = "CLICK";
	
	public static final String EVENT_TYPE_VIEW = "VIEW";

	public static final String EVENT_TYPE_LOCATION = "LOCATION";

	/**
	 * 解析微信发来的请求（XML）
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, String> parseXml(HttpServletRequest request)  {
		
		System.out.println("以下是来自微信服务器发送过来的用户事件消息：");
		System.out.println("===================================================");

		Enumeration<String> e0 = request.getHeaderNames();
		System.out.println(String.format("%25s", "header"));
		while (e0.hasMoreElements()) {
			String key = (String) e0.nextElement();
			String value = request.getHeader(key);
			System.out.println(String.format("%20s", key) + " : " + value);
		}
		System.out.println();
		System.out.println(String.format("%25s", "request"));

		// 从request中取得输入流
		InputStream inputStream = null;
		try {
			inputStream = request.getInputStream();
		} catch (IOException e1) {
			e1.printStackTrace();
			return new HashMap<String, String>();
		}
		// 读取输入流
		SAXReader reader = new SAXReader();
		Document document = null;
		try {
			document = reader.read(inputStream);
		} catch (DocumentException e1) {
			e1.printStackTrace();
		}finally{
			try {
				inputStream.close();
			} catch (IOException e2) {
				e2.printStackTrace();
			}
		}
		
		// 得到xml根元素
		Element root = null;
		try {
			root = document.getRootElement();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		// 得到根元素的所有子节点
		List<Element> elementList = new ArrayList<Element>();
		if(root!=null){
			elementList = root.elements();
		}

		// 遍历所有子节点
		Map<String, String> map = new HashMap<String, String>();
		if(elementList!=null){
			for (Element e : elementList){
				map.put(e.getName(), e.getText());
				System.out.println(String.format("%20s", e.getName()) + " : " + e.getText());
			}
		}

		System.out.println("===================================================");

		return map;
	}

	/**
	 * 解析微信发来的请求（XML）
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public static String getXmlFromPostData(HttpServletRequest request) {

		InputStream inputStream = null;
		ByteArrayOutputStream baos = null;
		try {
			inputStream = request.getInputStream();
			baos = new ByteArrayOutputStream();
			int i = -1;
			while ((i = inputStream.read()) != -1) {
				baos.write(i);
			}
			return baos.toString();
		} catch (IOException e1) {
			e1.printStackTrace();
			return "";
		} finally {
			try {
				inputStream.close();
			} catch (IOException e2) {
				e2.printStackTrace();
			}
			try {
				baos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static Map<String, String> xmlString2Map(String xml)  {
        Document document = null;
		try {
			document = DocumentHelper.parseText(xml);
		} catch (DocumentException e2) {
			e2.printStackTrace();
		}  

		// 得到xml根元素
		Element root = null;
		try {
			root = document.getRootElement();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		// 得到根元素的所有子节点
		List<Element> elementList = new ArrayList<Element>();
		if(root!=null){
			elementList = root.elements();
		}

		// 遍历所有子节点
		Map<String, String> map = new HashMap<String, String>();
		if(elementList!=null){
			for (Element e : elementList){
				map.put(e.getName(), e.getText());
			}
		}

		return map;
	}

	/**
	 * 文本消息对象转换成xml
	 * 
	 * @param textMessage 文本消息对象
	 * @return xml
	 */
	public static String textMessageToXml(TextMessage textMessage) {
		xstream.alias("xml", textMessage.getClass());
		return xstream.toXML(textMessage);
	}

	/**
	 * 音乐消息对象转换成xml
	 * 
	 * @param musicMessage 音乐消息对象
	 * @return xml
	 */
	public static String musicMessageToXml(MusicMessage musicMessage) {
		xstream.alias("xml", musicMessage.getClass());
		return xstream.toXML(musicMessage);
	}

	/**
	 * 图文消息对象转换成xml
	 * 
	 * @param newsMessage 图文消息对象
	 * @return xml
	 */
	public static String newsMessageToXml(NewsMessage newsMessage) {
		xstream.alias("xml", newsMessage.getClass());
		xstream.alias("item", new Article().getClass());
		return xstream.toXML(newsMessage);
	}

	/**
	 * 扩展xstream，使其支持CDATA块
	 * 
	 * @date 2013-05-19
	 */
	private static XStream xstream = new XStream(new XppDriver() {
		public HierarchicalStreamWriter createWriter(Writer out) {
			return new PrettyPrintWriter(out) {
				// 对所有xml节点的转换都增加CDATA标记
				boolean cdata = true;

				@SuppressWarnings({ "rawtypes" })
				public void startNode(String name, Class clazz) {
					super.startNode(name, clazz);
				}

				protected void writeText(QuickWriter writer, String text) {
					if (cdata) {
						writer.write("<![CDATA[");
						writer.write(text);
						writer.write("]]>");
					} else {
						writer.write(text);
					}
				}
			};
		}
	});
}
