package dev.o8o1o5.myTextures.manager;

import com.sun.net.httpserver.HttpServer;
import dev.o8o1o5.myTextures.MyTextures;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class WebServerManager {

    private final MyTextures plugin;
    private HttpServer server;

    public WebServerManager(MyTextures plugin) {
        this.plugin = plugin;
    }

    public void startserver(int port) {
        try {
            server = HttpServer.create(new InetSocketAddress(port), 0);

            server.createContext("/resourcepack.zip", exchange -> {
                File zipFile = new File(plugin.getDataFolder(), "resourcepack.zip");

                if (!zipFile.exists()) {
                    String response = "File not found";
                    exchange.sendResponseHeaders(404, response.length());
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(response.getBytes());
                    }
                    return;
                }

                exchange.getResponseHeaders().add("Content-Type", "application/octet-stream");
                exchange.getResponseHeaders().add("Content-Disposition", "attachment; filename=\"resourcepack.zip\"");
                exchange.sendResponseHeaders(200, zipFile.length());

                try (OutputStream os = exchange.getResponseBody();
                     FileInputStream fis = new FileInputStream(zipFile)) {
                    byte[] buffer = new byte[8192];
                    int count;
                    while ((count = fis.read(buffer)) != -1) {
                        os.write(buffer, 0, count);
                    }
                }
            });

            server.setExecutor(null);
            server.start();
            plugin.getLogger().info("내장 웹 서버가 포트 " + port + "에서 시작되었습니다.");
        } catch (IOException e) {
            plugin.getLogger().severe("웹 서버 시작 중 오류 발생: " + e.getMessage());
        }
    }

    public void stopServer() {
        if (server != null) {
            server.stop(0);
            plugin.getLogger().info("내장 웹 서버가 종료되었습니다.");
        }
    }
}
