package com.mashibing.io.bio;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class Client {
	
	private OutputStream out;
	
	private String name;
	
	private void init() throws Exception {
		Socket s = new Socket("127.0.0.1", 8888);
		this.out = s.getOutputStream();
	}
	
	public void sayName() throws IOException {
		this.out.write(name.getBytes());
	}

	public Client(String name) throws Exception {
		super();
		this.name = name;
		init();
	}
	
	public static void main(String[] args) throws Exception {
		
		Client c1 = new Client("c1");
		Client c2 = new Client("c2");
		
		c1.sayName();
		c2.sayName();
		c1.sayName();
		c2.sayName();
		
	}
	
    
    
}
