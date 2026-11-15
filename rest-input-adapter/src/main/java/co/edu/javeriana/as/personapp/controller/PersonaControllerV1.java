package co.edu.javeriana.as.personapp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import co.edu.javeriana.as.personapp.adapter.PersonaInputAdapterRest;
import co.edu.javeriana.as.personapp.common.exceptions.AlreadyExistException;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.model.request.PersonaRequest;
import co.edu.javeriana.as.personapp.model.response.CountResponse;
import co.edu.javeriana.as.personapp.model.response.EstudioResponse;
import co.edu.javeriana.as.personapp.model.response.PersonaResponse;
import co.edu.javeriana.as.personapp.model.response.Response;
import co.edu.javeriana.as.personapp.model.response.TelefonoResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * HTTP layer only: routes, verbs and status codes. All the work is delegated to the
 * input adapter, and errors are translated to HTTP by {@link RestExceptionHandler}.
 */
@Slf4j
@RestController
@RequestMapping(path = "/api/v1/persona", produces = MediaType.APPLICATION_JSON_VALUE)
public class PersonaControllerV1 {
	
	@Autowired
	private PersonaInputAdapterRest personaInputAdapterRest;
	
	@GetMapping("/{database}")
	public List<PersonaResponse> personas(@PathVariable String database) throws InvalidOptionException {
		log.info("Into personas REST API");
		return personaInputAdapterRest.historial(database);
	}

	@GetMapping("/{database}/count")
	public CountResponse contarPersonas(@PathVariable String database) throws InvalidOptionException {
		return personaInputAdapterRest.contarPersonas(database);
	}

	@GetMapping("/{database}/{cc}")
	public PersonaResponse persona(@PathVariable String database, @PathVariable Integer cc)
			throws InvalidOptionException, NoExistException {
		return personaInputAdapterRest.buscarPersona(database, cc);
	}

	@GetMapping("/{database}/{cc}/telefonos")
	public List<TelefonoResponse> telefonos(@PathVariable String database, @PathVariable Integer cc)
			throws InvalidOptionException, NoExistException {
		return personaInputAdapterRest.telefonosPersona(database, cc);
	}

	@GetMapping("/{database}/{cc}/estudios")
	public List<EstudioResponse> estudios(@PathVariable String database, @PathVariable Integer cc)
			throws InvalidOptionException, NoExistException {
		return personaInputAdapterRest.estudiosPersona(database, cc);
	}
	
	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	public PersonaResponse crearPersona(@RequestBody PersonaRequest request)
			throws InvalidOptionException, AlreadyExistException {
		log.info("Into crearPersona REST API");
		return personaInputAdapterRest.crearPersona(request);
	}

	@PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public PersonaResponse editarPersona(@RequestBody PersonaRequest request)
			throws InvalidOptionException, NoExistException {
		log.info("Into editarPersona REST API");
		return personaInputAdapterRest.editarPersona(request);
	}

	@DeleteMapping("/{database}/{cc}")
	public Response eliminarPersona(@PathVariable String database, @PathVariable Integer cc)
			throws InvalidOptionException, NoExistException {
		log.info("Into eliminarPersona REST API");
		return personaInputAdapterRest.eliminarPersona(database, cc);
	}
}
