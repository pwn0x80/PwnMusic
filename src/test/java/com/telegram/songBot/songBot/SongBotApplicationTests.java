package com.telegram.songBot.songBot;

import io.github.cdimascio.dotenv.Dotenv;
import com.telegram.songBot.songBot.service.PlaylistService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class SongBotApplicationTests {

	@BeforeAll
	static void loadEnv() {
		Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
		dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
	}

	@Autowired(required = false)
	private PlaylistService playlistService;

	@Test
	void contextLoads() {
		assertNotNull(playlistService);
	}

}
