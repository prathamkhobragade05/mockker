package com.projects.mockker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projects.mockker.model.AllTables;
import com.projects.mockker.service.AllTablesService;


@RestController
@RequestMapping("/database")
public class AllTablesController {
	@Autowired private AllTablesService tableService;
	
	@GetMapping("/{userid}")
	public ResponseEntity<AllTables> getAllTables(@PathVariable("userid") Long userId){
		return ResponseEntity.ok(tableService.getAllTables(userId));
		
	}
}
