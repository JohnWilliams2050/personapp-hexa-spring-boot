package co.edu.javeriana.as.personapp.mapper;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Gender;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.model.request.PersonaRequest;
import co.edu.javeriana.as.personapp.model.response.PersonaResponse;

@Mapper
public class PersonaMapperRest {
	
	public PersonaResponse fromDomainToAdapterRestMaria(Person person) {
		return fromDomainToAdapterRest(person, "MariaDB");
	}
	public PersonaResponse fromDomainToAdapterRestMongo(Person person) {
		return fromDomainToAdapterRest(person, "MongoDB");
	}
	
	public PersonaResponse fromDomainToAdapterRest(Person person, String database) {
		return new PersonaResponse(
				person.getIdentification()+"", 
				person.getFirstName(), 
				person.getLastName(), 
				person.getAge()!= null ? person.getAge()+"" : null, 
				person.getGender().toString(), 
				database,
				"OK");
	}
	//Anteriormente el mapper tiraba el request a un lado y retornaba una persona vacia.
	//Entonces cada campo estaba en null.
	//Los output mappers tiraban la excepcion en validateGenero(@NonNull Gender gender)
	//Por esa razon se agrego la logica de validacion de genero y edad en este mapper.
	//Y tambien se hizo para que el mapper reciba un request y retorne una persona con los campos llenos.
	//Una persona invalida nunca llega a los casos de uso o a los adaptadores de persitencia.
	public Person fromAdapterToDomain(PersonaRequest request) {
		Person person = new Person();
		person.setIdentification(validateDni(request.getDni()));
		person.setFirstName(required(request.getFirstName(), "firstName"));
		person.setLastName(required(request.getLastName(), "lastName"));
		person.setGender(validateGender(request.getSex()));
		person.setAge(validateAge(request.getAge()));
		return person;
	}
	private Integer validateDni(String dni) {
		try {
			return Integer.parseInt(required(dni, "dni"));
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("dni must be a number: " + dni);
		}
	}

	private String required(String value, String field) {
		if (value == null || value.isBlank())
			throw new IllegalArgumentException(field + " is required");
		return value.trim();
	}

	private Gender validateGender(String sex) {
		String value = required(sex, "sex");
		if ("F".equalsIgnoreCase(value) || "FEMALE".equalsIgnoreCase(value))
			return Gender.FEMALE;
		if ("M".equalsIgnoreCase(value) || "MALE".equalsIgnoreCase(value))
			return Gender.MALE;
		throw new IllegalArgumentException("sex must be M or F: " + sex);
	}

	private Integer validateAge(String age) {
		if (age == null || age.isBlank())
			return null;
		try {
			int value = Integer.parseInt(age.trim());
			if (value < 0)
				throw new IllegalArgumentException("age cannot be negative: " + age);
			return value;
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("age must be a number: " + age);
		}
	}
}
