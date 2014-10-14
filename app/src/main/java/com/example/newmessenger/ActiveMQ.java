package com.example.newmessenger;

import android.app.Activity;

import org.fusesource.hawtbuf.Buffer;
import org.fusesource.hawtbuf.UTF8Buffer;
import org.fusesource.mqtt.client.Callback;
import org.fusesource.mqtt.client.CallbackConnection;
import org.fusesource.mqtt.client.Listener;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URISyntaxException;

public class ActiveMQ {
	
	private MQTT mqtt;
	private CallbackConnection connection;
	private Activity activity;
	private MessageListener listener;
	private boolean connected = false;

	public ActiveMQ(Activity activity) {
		this.activity = activity;
	}

	// callback used for Future to make sure your code is called on the right
	// thread
	private <T> Callback<T> onui(final Callback<T> original) {
		return new Callback<T>() {
			public void onSuccess(final T value) {
				activity.runOnUiThread(new Runnable() {
					public void run() {
						original.onSuccess(value);
					}
				});
			}

			public void onFailure(final Throwable error) {
				activity.runOnUiThread(new Runnable() {
					public void run() {
						original.onFailure(error);
					}
				});
			}
		};
	}

	public void setListener(MessageListener listener) {
		this.listener = listener;
	}

	private void processOnPublish(final UTF8Buffer topicName, final Buffer payload, Runnable cb) {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				if (listener!=null) {
					ObjectInputStream oi=null;
					ByteArrayInputStream is=null;
					try {
						is = new ByteArrayInputStream(payload.toByteArray());
						oi = new ObjectInputStream(is);
						Serializable obj = (Serializable) oi.readObject();
						listener.onMessage(obj);
				
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} finally {
						try {
							oi.close();
						} catch (Throwable e) {
							// Ignore exceptions here
						}
					}
				}
			}
		});
		cb.run();	
	}

	public void connect(final Callback<Void> callback) {
		if (connection != null && connected) {
			connection.disconnect(new Callback<Void>() {

				@Override
				public void onSuccess(Void arg0) {
					localConnectExecute(callback);
				}

				@Override
				public void onFailure(Throwable arg0) {
					// it never fails
				}
			});
			connected = false;
		} else
			localConnectExecute(callback);
	}

	private void localConnectExecute(final Callback<Void> callback) {
		mqtt = new MQTT();
		try {
			mqtt.setHost(Constants.ACTIVEMQ_URL_MQTT);
		} catch (final URISyntaxException e) {
			activity.runOnUiThread(new Runnable() {
				public void run() {
					callback.onFailure(e);
				}
			});
		}
		mqtt.setUserName(Constants.USERNAME);
		mqtt.setPassword(Constants.PASSWORD);

		connection = mqtt.callbackConnection();
		connection.listener(new Listener() {

			@Override
			public void onPublish(UTF8Buffer arg0, Buffer arg1, Runnable arg2) {
				processOnPublish(arg0, arg1, arg2);
			}

			@Override
			public void onFailure(Throwable arg0) {
				connected = false;
				Thread.currentThread().getUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), arg0);
			}

			@Override
			public void onDisconnected() {
				connected = false;
			}

			@Override
			public void onConnected() {
				connected = true;
			}
		});
		connection.connect(onui(callback));
	}

	public void subscribe(final String queue, final Callback<byte[]> callback) {
		connection.getDispatchQueue().execute(new Runnable() {
			public void run() {
				if (connection != null && connected) {
					Topic[] topics = { new Topic(queue, QoS.AT_LEAST_ONCE) };
					connection.subscribe(topics, onui(callback));
				} else
					callback.onFailure(new Exception(
							"Connection not established"));
			}
		});
	}

	public void unsubscribe(final String queue, final Callback<Void> callback) {
		connection.getDispatchQueue().execute(new Runnable() {
			public void run() {
				if (connection != null && connected) {
					UTF8Buffer[] topics = { new UTF8Buffer(queue) };
					connection.unsubscribe(topics, onui(callback));
				} else
					callback.onFailure(new Exception(
							"Connection not established"));
			}
		});
	}

	public void disconnect(final Callback<Void> callback) {
		connection.getDispatchQueue().execute(new Runnable() {
			public void run() {
				if (connection != null && connected) {
					connection.disconnect(onui(callback));
				} else
					callback.onSuccess(null);
			}
		});
	}

	public void send(final String destination, final Serializable message,
			final Callback<Void> callback) {
		connection.getDispatchQueue().execute(new Runnable() {
			public void run() {
				final Callback<Void> callbackSafe = onui(callback);
				if (connection != null && connected) {
					ByteArrayOutputStream os = null;
					ObjectOutputStream oo = null;
					try {
						os = new ByteArrayOutputStream();
						oo = new ObjectOutputStream(os);
						oo.writeObject(message);
						oo.flush();
						connection.publish(destination, os.toByteArray(),
								QoS.AT_LEAST_ONCE, false, callbackSafe);
					} catch (IOException e) {
						callbackSafe.onFailure(e);
					} finally {
						try {
							oo.close();
							os.close();
						} catch (Throwable e) {
							// Ignore exceptions here
						}
					}
					
				} else {
					callbackSafe.onFailure(new Exception(
							"Connection not established"));
				}
			}
		});
	}

}
