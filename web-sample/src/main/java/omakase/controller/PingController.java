package omakase.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/ping")
public class PingController {

	@RequestMapping
	@ResponseBody
	public Map<String, String> ping() {
		Map<String, String> result = new HashMap<String, String>();
		result.put("status", "ok");
		return result;
	}
}
