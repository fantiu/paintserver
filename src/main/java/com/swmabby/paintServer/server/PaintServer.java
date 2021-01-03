package com.swmabby.paintServer.server;

import com.alibaba.fastjson.JSON;
import com.swmabby.paintServer.entity.Client;
import com.swmabby.paintServer.entity.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static org.apache.logging.log4j.message.MapMessage.MapFormat.JSON;

@ServerEndpoint(value = "/socketServer/{userName}")
@Component
public class PaintServer {

	private static final Logger logger = LoggerFactory.getLogger(PaintServer.class);

	/**
	 * 用线程安全的CopyOnWriteArraySet来存放客户端连接的信息
	 */
	private static CopyOnWriteArraySet<Client> socketServers = new CopyOnWriteArraySet<>();

	private Session session;

	private final static String SYS_USERNAME = "First!@#Hello";

	@OnOpen
	public void open(Session session,@PathParam(value="userName")String userName) {

		AtomicBoolean bExist = new AtomicBoolean(false);
		socketServers.forEach(client -> {
			if (userName.equals(client.getUserName())) {
				bExist.compareAndSet(false, true);
				client.setSession(session);
				logger.info("更新客户端链接 :【{}】", client.getUserName());
			}
		});

		if (!bExist.get()) {
			this.session = session;
			socketServers.add(new Client(userName, session));
			logger.info("new conn");
		}

		logger.info("客户端:【{}】连接成功", userName);
	}

	@OnMessage
	public void onMessage(String message){

		Message messageObject = null;
		try {
			messageObject = com.alibaba.fastjson.JSON.parseObject(message, Message.class);
		} catch (Exception e) {
			logger.error("message json parse error.{}", message);
			return;
		}

		Client fromClient = socketServers.stream().filter( cli -> cli.getSession() == session)
				.collect(Collectors.toList()).get(0);
		// sendMessage(client.getUserName()+"<--"+message,SYS_USERNAME);

		socketServers.stream().filter(cli -> cli.getUserName() != fromClient.getUserName())
				.forEach(client -> {
					try {
						client.getSession().getBasicRemote().sendText(message);
					} catch (IOException e) {
						e.printStackTrace();
					}
				});

		logger.info("客户端:【{}】发送信息:{}",fromClient.getUserName(),message);
	}

	@OnClose
	public void onClose(){
		socketServers.forEach(client ->{
			if (client.getSession().getId().equals(session.getId())) {

				logger.info("客户端:【{}】断开连接",client.getUserName());
				socketServers.remove(client);

			}
		});
	}

    @OnError
    public void onError(Throwable error) {
		socketServers.forEach(client ->{
			if (client.getSession().getId().equals(session.getId())) {
				socketServers.remove(client);
				logger.error("客户端:【{}】发生异常",client.getUserName());
				error.printStackTrace();
			}
		});
    }

	public synchronized static void sendMessage(String message,String userName) {

		socketServers.forEach(client ->{
			if (userName.equals(client.getUserName())) {
				try {
					client.getSession().getBasicRemote().sendText(message);

					logger.info("服务端推送给客户端 :【{}】",client.getUserName(),message);

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}

	public synchronized static int getOnlineNum(){
		return socketServers.stream().filter(client -> !client.getUserName().equals(SYS_USERNAME))
				.collect(Collectors.toList()).size();
	}

	public synchronized static List<String> getOnlineUsers(){

		List<String> onlineUsers = socketServers.stream()
				.filter(client -> !client.getUserName().equals(SYS_USERNAME))
				.map(client -> client.getUserName())
				.collect(Collectors.toList());

	    return onlineUsers;
	}

	public synchronized static void sendAll(String message) {
		//群发，不能发送给服务端自己
		socketServers.stream().filter(cli -> cli.getUserName() != SYS_USERNAME)
				.forEach(client -> {
			try {
				client.getSession().getBasicRemote().sendText(message);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});

		logger.info("服务端推送给所有客户端 :【{}】",message);
	}

	public synchronized static void SendMany(String message,String [] persons) {
		for (String userName : persons) {
			sendMessage(message,userName);
		}
	}
}
