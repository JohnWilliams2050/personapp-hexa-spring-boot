package co.edu.javeriana.as.personapp.terminal.adapter;

import java.util.List;

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
import co.edu.javeriana.as.personapp.terminal.mapper.EstudioMapperCli;
import co.edu.javeriana.as.personapp.terminal.mapper.PersonaMapperCli;
import co.edu.javeriana.as.personapp.terminal.mapper.TelefonoMapperCli;
import co.edu.javeriana.as.personapp.terminal.model.PersonaModelCli;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Adapter
public class PersonaInputAdapterCli {

	@Autowired
	@Qualifier("personOutputAdapterMaria")
	private PersonOutputPort personOutputPortMaria;

	@Autowired
	@Qualifier("personOutputAdapterMongo")
	private PersonOutputPort personOutputPortMongo;

	@Autowired
	private PersonaMapperCli personaMapperCli;

	@Autowired
	private TelefonoMapperCli telefonoMapperCli;

	@Autowired
	private EstudioMapperCli estudioMapperCli;

	// La CLI es una sesión de un solo usuario: la base de datos elegida en el menú
	// permanece seleccionada hasta que el usuario regresa, por lo que mantenerla en un campo
	// es correcto aquí (a diferencia de REST).
	PersonInputPort personInputPort;

	public void setPersonOutputPortInjection(String dbOption) throws InvalidOptionException {
		if (dbOption.equalsIgnoreCase(DatabaseOption.MARIA.toString())) {
			personInputPort = new PersonUseCase(personOutputPortMaria);
		} else if (dbOption.equalsIgnoreCase(DatabaseOption.MONGO.toString())) {
			personInputPort = new PersonUseCase(personOutputPortMongo);
		} else {
			throw new InvalidOptionException("Invalid database option: " + dbOption);
		}
	}

	// ---------- Lectura ----------

	public void historial() {
		log.info("Dentro de historial PersonaEntity en Input Adapter");
		List<Person> personas = personInputPort.findAll();
		if (personas.isEmpty())
			System.out.println("No hay personas registradas");
		personas.stream().map(personaMapperCli::fromDomainToAdapterCli).forEach(System.out::println);
	}

	public void buscar(Integer cc) throws NoExistException {
		System.out.println(personaMapperCli.fromDomainToAdapterCli(personInputPort.findOne(cc)));
	}

	public void contar() {
		System.out.println("Total de personas: " + personInputPort.count());
	}

	// ---------- Lectura: agregación (teléfonos y estudios de la persona) ----------

	public void telefonos(Integer cc) throws NoExistException {
		var telefonos = personInputPort.getPhones(cc);
		if (telefonos.isEmpty())
			System.out.println("La persona " + cc + " no tiene teléfonos");
		telefonos.stream().map(telefonoMapperCli::fromDomainToAdapterCli).forEach(System.out::println);
	}

	public void estudios(Integer cc) throws NoExistException {
		var estudios = personInputPort.getStudies(cc);
		if (estudios.isEmpty())
			System.out.println("La persona " + cc + " no tiene estudios");
		estudios.stream().map(estudioMapperCli::fromDomainToAdapterCli).forEach(System.out::println);
	}

	// ---------- Crear / Actualizar / Eliminar ----------

	public void crear(PersonaModelCli persona) throws AlreadyExistException {
		Person creada = personInputPort.create(personaMapperCli.fromAdapterCliToDomain(persona));
		System.out.println("Persona creada: " + personaMapperCli.fromDomainToAdapterCli(creada));
	}

	public void editar(PersonaModelCli persona) throws NoExistException {
		Person editada = personInputPort.edit(persona.getCc(), personaMapperCli.fromAdapterCliToDomain(persona));
		System.out.println("Persona editada: " + personaMapperCli.fromDomainToAdapterCli(editada));
	}

	public void eliminar(Integer cc) throws NoExistException {
		System.out.println(personInputPort.drop(cc) ? "Persona " + cc + " eliminada"
				: "La persona " + cc + " no se pudo eliminar");
	}
}
