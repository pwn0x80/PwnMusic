package com.telegram.songBot.songBot.client;

import java.io.IOException;
import java.util.List;

public interface YtDlpClient {
    List<String> executeProcess(List<String> command) throws IOException, InterruptedException;
}
