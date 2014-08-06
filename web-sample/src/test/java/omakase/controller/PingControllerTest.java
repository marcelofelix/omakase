package omakase.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;

import omakase.AbstractControllerTest;

public class PingControllerTest extends AbstractControllerTest {

	@Test
	public void testePing() throws Exception {
		perform(get("/ping")).andExpect(status().isOk()).andDo(print());
	}
}
