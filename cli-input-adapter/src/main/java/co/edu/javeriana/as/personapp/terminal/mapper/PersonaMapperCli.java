package co.edu.javeriana.as.personapp.terminal.mapper;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Gender;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.terminal.model.PersonaModelCli;

@Mapper
public class PersonaMapperCli {

	public PersonaModelCli fromDomainToAdapterCli(Person person) {
		PersonaModelCli personaModelCli = new PersonaModelCli();
		personaModelCli.setCc(person.getIdentification());
		personaModelCli.setNombre(person.getFirstName());
		personaModelCli.setApellido(person.getLastName());
		personaModelCli.setGenero(person.getGender().toString());
		personaModelCli.setEdad(person.getAge());
		return personaModelCli;
	}

	public Person fromAdapterCliToDomain(PersonaModelCli personaModelCli) {
		Person person = new Person();
		person.setIdentification(personaModelCli.getCc());
		person.setFirstName(personaModelCli.getNombre());
		person.setLastName(personaModelCli.getApellido());
		person.setGender(validateGender(personaModelCli.getGenero()));
		person.setAge(personaModelCli.getEdad());
		return person;
	}

	private Gender validateGender(String genero) {
		if ("F".equalsIgnoreCase(genero))
			return Gender.FEMALE;
		if ("M".equalsIgnoreCase(genero))
			return Gender.MALE;
		throw new IllegalArgumentException("El genero debe ser M o F: " + genero);
	}
}
