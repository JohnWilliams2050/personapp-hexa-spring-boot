package co.edu.javeriana.as.personapp.adapter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import co.edu.javeriana.as.personapp.application.port.in.PersonInputPort;
import co.edu.javeriana.as.personapp.application.port.out.PersonOutputPort;
import co.edu.javeriana.as.personapp.application.usecase.PersonUseCase;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.common.exceptions.AlreadyExistException;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.common.setup.DatabaseOption;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.mapper.EstudioMapperRest;
import co.edu.javeriana.as.personapp.mapper.PersonaMapperRest;
import co.edu.javeriana.as.personapp.mapper.TelefonoMapperRest;
import co.edu.javeriana.as.personapp.model.request.PersonaRequest;
import co.edu.javeriana.as.personapp.model.response.CountResponse;
import co.edu.javeriana.as.personapp.model.response.EstudioResponse;
import co.edu.javeriana.as.personapp.model.response.PersonaResponse;
import co.edu.javeriana.as.personapp.model.response.Response;
import co.edu.javeriana.as.personapp.model.response.TelefonoResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Adapter
public class PersonaInputAdapterRest {

	@Autowired
	@Qualifier("personOutputAdapterMaria")
	private PersonOutputPort personOutputPortMaria;

	@Autowired
	@Qualifier("personOutputAdapterMongo")
	private PersonOutputPort personOutputPortMongo;

	@Autowired
	private PersonaMapperRest personaMapperRest;

	@Autowired
	private TelefonoMapperRest telefonoMapperRest;

	@Autowired
	private EstudioMapperRest estudioMapperRest;

	/**
	 * Strategy selection: picks the output port (MariaDB or MongoDB) for this request.
	 * The use case is returned instead of being stored in a field: this adapter is a
	 * singleton shared by all HTTP requests, so a field would let two concurrent requests
	 * overwrite each other's database choice.
	 */
	private PersonInputPort personInputPort(DatabaseOption database) {
		return new PersonUseCase(database == DatabaseOption.MARIA ? personOutputPortMaria : personOutputPortMongo);
	}

	private DatabaseOption validateDatabase(String dbOption) throws InvalidOptionException {
		if (dbOption != null) {
			for (DatabaseOption option : DatabaseOption.values())
				if (option.toString().equalsIgnoreCase(dbOption.trim()))
					return option;
		}
		throw new InvalidOptionException("Invalid database option: " + dbOption);
	}

	private PersonaResponse toResponse(Person person, DatabaseOption database) {
		return database == DatabaseOption.MARIA ? personaMapperRest.fromDomainToAdapterRestMaria(person)
				: personaMapperRest.fromDomainToAdapterRestMongo(person);
	}

	// ---------- Read ----------

	public List<PersonaResponse> historial(String dbOption) throws InvalidOptionException {
		log.info("Into historial PersonaEntity in Input Adapter");
		DatabaseOption database = validateDatabase(dbOption);
		return personInputPort(database).findAll().stream().map(person -> toResponse(person, database))
				.collect(Collectors.toList());
	}

	public PersonaResponse buscarPersona(String dbOption, Integer cc)
			throws InvalidOptionException, NoExistException {
		DatabaseOption database = validateDatabase(dbOption);
		return toResponse(personInputPort(database).findOne(cc), database);
	}

	public CountResponse contarPersonas(String dbOption) throws InvalidOptionException {
		DatabaseOption database = validateDatabase(dbOption);
		return new CountResponse(database.toString(), personInputPort(database).count(), "OK");
	}

	// ---------- Read: aggregation (the person's phones and studies) ----------

	public List<TelefonoResponse> telefonosPersona(String dbOption, Integer cc)
			throws InvalidOptionException, NoExistException {
		DatabaseOption database = validateDatabase(dbOption);
		return personInputPort(database).getPhones(cc).stream().map(telefonoMapperRest::fromDomainToAdapterRest)
				.collect(Collectors.toList());
	}

	public List<EstudioResponse> estudiosPersona(String dbOption, Integer cc)
			throws InvalidOptionException, NoExistException {
		DatabaseOption database = validateDatabase(dbOption);
		return personInputPort(database).getStudies(cc).stream().map(estudioMapperRest::fromDomainToAdapterRest)
				.collect(Collectors.toList());
	}

	// ---------- Create / Update / Delete ----------

	public PersonaResponse crearPersona(PersonaRequest request) throws InvalidOptionException, AlreadyExistException {
		DatabaseOption database = validateDatabase(request.getDatabase());
		Person person = personInputPort(database).create(personaMapperRest.fromAdapterToDomain(request));
		return toResponse(person, database);
	}

	public PersonaResponse editarPersona(PersonaRequest request) throws InvalidOptionException, NoExistException {
		DatabaseOption database = validateDatabase(request.getDatabase());
		Person person = personaMapperRest.fromAdapterToDomain(request);
		return toResponse(personInputPort(database).edit(person.getIdentification(), person), database);
	}

	public Response eliminarPersona(String dbOption, Integer cc) throws InvalidOptionException, NoExistException {
		DatabaseOption database = validateDatabase(dbOption);
		Boolean deleted = personInputPort(database).drop(cc);
		return new Response(deleted ? "OK" : "ERROR",
				deleted ? "Person " + cc + " deleted from " + database : "Person " + cc + " could not be deleted",
				LocalDateTime.now());
	}

}
