package io.agora.rtt;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.sun.net.httpserver.HttpServer;

import io.github.cdimascio.dotenv.Dotenv;

public class Main {
	public static int port = Integer.parseInt(Dotenv.load().get("PORT", "80"));

	public static void main(String[] args) {
		// Firebaseの初期化
		try {
			FileInputStream serviceAccount = new FileInputStream("/home/masaofyamad/spajam-service-accounts.json");

			FirebaseOptions options = FirebaseOptions.builder()
					.setCredentials(GoogleCredentials.fromStream(serviceAccount))
					.setDatabaseUrl("https://spajam2024-439107.firebaseio.com")
					.build();

			FirebaseApp.initializeApp(options);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Start the http server
		SimpleHttpServer httpServer = new SimpleHttpServer();
		httpServer.Start(port);
	}

	static class SimpleHttpServer {
		private HttpServer server;

		public void Start(int port) {
			try {
				server = HttpServer.create(new InetSocketAddress(port), 0);
				System.out.println("RTT server started on port " + port);

				server.createContext("/", new Handlers.RootHandler());
				server.createContext("/rttStart", new Handlers.StartHandler());
				server.createContext("/rttQuery", new Handlers.QueryHandler());
				server.createContext("/rttStop", new Handlers.StopHandler());
				server.setExecutor(null);
				server.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void Stop() {
			server.stop(0);
			System.out.println("server stopped");
		}
	}
}
