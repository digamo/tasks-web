package br.com.digamo.tasks.web.controller;


import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import br.com.digamo.tasks.web.model.Todo;

@Controller
public class TasksController {
	
	@Value("${api.host}")
	private String API_HOST;

	@Value("${api.port}")
	private String API_PORT;

	@Value("${api.mapping}")
	private String API_MAPPING;

	@Value("${app.version}")
	private String VERSION;

	public String getApiURL() {
		return "http://" + API_HOST + ":" + API_PORT + API_MAPPING;
	}
	
	@GetMapping("")
	public String index(Model model) {
		model.addAttribute("todos", listOfTodo());
		if(VERSION.startsWith("build"))
			model.addAttribute("version", VERSION);
		return "index";
	}
	
	@GetMapping("add")
	public String add(Model model) {
		model.addAttribute("todo", new Todo());
		return "add";
	}

	@PostMapping("save")
	public String save(Todo todo, Model model) throws ParseException {
		try {
			RestTemplate restTemplate = new RestTemplate();
			restTemplate.postForObject(
					getApiURL(), todo, Object.class);			
			model.addAttribute("success", "Success!");
			return "index";
		} catch(HttpClientErrorException e) {

			JSONParser parser = new JSONParser();
			Object responseErrors = parser.parse(e.getResponseBodyAsString());
	        JSONArray arrayErrors = (JSONArray)responseErrors;
	        JSONObject error = (JSONObject)arrayErrors.get(0);
			
			model.addAttribute("error", error.get("message"));
			model.addAttribute("todo", todo);
			return "add"; 
		} finally {
			model.addAttribute("todos", listOfTodo());
		}
	}
	 
	
	@GetMapping("delete/{id}")
	public String delete(@PathVariable Long id, Model model) {
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.delete(getApiURL() + "/" + id);			
		model.addAttribute("success", "Success!");
		model.addAttribute("todos", listOfTodo());
		return "index";
	}

	
	@SuppressWarnings("unchecked")
	private List<Todo> listOfTodo() {
		RestTemplate restTemplate = new RestTemplate();
		return restTemplate.getForObject(
				getApiURL(), List.class);
	}
}
