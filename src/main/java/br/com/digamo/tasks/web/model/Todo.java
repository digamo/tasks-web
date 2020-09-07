package br.com.digamo.tasks.web.model;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Todo {

	private Long id;
	private String task;
	@DateTimeFormat(pattern="dd/MM/yyyy")
	private LocalDate dueDate;
	
}

