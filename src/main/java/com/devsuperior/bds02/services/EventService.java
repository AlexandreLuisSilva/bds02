package com.devsuperior.bds02.services;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.bds02.dto.EventDTO;
import com.devsuperior.bds02.entities.City;
import com.devsuperior.bds02.entities.Event;
import com.devsuperior.bds02.repositories.EventRepository;
import com.devsuperior.bds02.services.exceptions.DataBaseException;
import com.devsuperior.bds02.services.exceptions.ResourceNotFoundException;

@Service
public class EventService {

	@Autowired
	private EventRepository repository;
	
	public List<EventDTO> findAll() {
		List<Event> list = repository.findAll(Sort.by("name"));
		return list.stream().map(x -> new EventDTO(x)).collect(Collectors.toList());
	}

	@Transactional
	public EventDTO update(Long id, EventDTO dto) {
		try {
			Event entity = repository.getOne(id);
			copyDTOToEntity(dto, entity);
			entity = repository.save(entity);
			return new EventDTO(entity);
		} 
		catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException("Id not found" + id);
		}
	}
	
	public void delete(Long id) {
		try {
			repository.deleteById(id);
		}
		catch (EmptyResultDataAccessException e) {
			throw new ResourceNotFoundException("Id not found " + id);
		}
		catch (DataIntegrityViolationException e) {
			throw new DataBaseException("Integrity Violation");
		}
	}
	
	private void copyDTOToEntity(EventDTO dto, Event entity) {
	    entity.setName(dto.getName());
	    entity.setUrl(dto.getUrl());
	    entity.setDate(dto.getDate());
	    entity.setCity(new City(dto.getCityId(), null));
	}
}

