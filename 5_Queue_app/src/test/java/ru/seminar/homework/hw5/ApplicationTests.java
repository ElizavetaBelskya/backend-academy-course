package ru.seminar.homework.hw5;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {"grpc.server.address=0.0.0.0", "grpc.server.port=9089"})
class ApplicationTests {

	@Test
	void contextLoads() {
	}

}
