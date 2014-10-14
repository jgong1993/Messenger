package com.example.newmessenger;

import java.io.Serializable;

public interface MessageListener {
	public void onMessage(Serializable msg);
}
