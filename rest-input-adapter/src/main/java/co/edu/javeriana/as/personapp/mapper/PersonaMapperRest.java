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
	// Anteriormente, el mapeador descartaba la solicitud y devolvía una persona vacía.
	// Por eso, todos los campos quedaban en null.
	// Los mapeadores de salida lanzaban la excepción en validateGenero(@NonNull Gender gender).
	// Por esa razón, se agregó la lógica de validación del género y la edad en este mapeador.
	// También se hizo para que el mapeador recibiera una solicitud y devolviera una persona
	// con todos sus campos completos.
	// Una persona no válida nunca llega a los casos de uso ni a los adaptadores de persistencia.
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
			throw new IllegalArgumentException("El DNI debe ser un número: " + dni);
		}
	}

	private String required(String value, String field) {
		if (value == null || value.isBlank())
			throw new IllegalArgumentException("El campo " + field + " es obligatorio");
		return value.trim();
	}

	private Gender validateGender(String sex) {
		String value = required(sex, "sex");
		if ("F".equalsIgnoreCase(value) || "FEMALE".equalsIgnoreCase(value))
			return Gender.FEMALE;
		if ("M".equalsIgnoreCase(value) || "MALE".equalsIgnoreCase(value))
			return Gender.MALE;
		throw new IllegalArgumentException("El sexo debe ser M o F: " + sex);
	}

	private Integer validateAge(String age) {
		if (age == null || age.isBlank())
			return null;
		try {
			int value = Integer.parseInt(age.trim());
			if (value < 0)
				throw new IllegalArgumentException("La edad no puede ser negativa: " + age);
			return value;
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("La edad debe ser un número: " + age);
		}
	}
}
