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
				person.getAge()+"", 
				person.getGender().toString(), 
				database,
				"OK");
	}
	//Anteriormente el mapper tiraba el request a un lado y retornaba una persona vacia.
	//Entonces cada campo estaba en null.
	//Los output mappers tiraban la excepcion en validateGenero(@NonNull Gender gender)
	//Por esa razon se agrego la logica de validacion de genero y edad en este mapper.
	//Y tambien se hizo para que el mapper reciba un request y retorne una persona con los campos llenos.
	public Person fromAdapterToDomain(PersonaRequest request) {
		Person person = new Person();
		person.setIdentification(Integer.parseInt(request.getDni()));
		person.setFirstName(request.getFirstName());
		person.setLastName(request.getLastName());
		person.setGender(validateGender(request.getSex()));
		person.setAge(validateAge(request.getAge()));
		return person;
	}
	private Gender validateGender(String sex) {
		return "F".equalsIgnoreCase(sex) || "FEMALE".equalsIgnoreCase(sex) ? Gender.FEMALE
				: "M".equalsIgnoreCase(sex) || "MALE".equalsIgnoreCase(sex) ? Gender.MALE : Gender.OTHER;
	}
	private Integer validateAge(String age) {
		return age != null && !age.isBlank() ? Integer.parseInt(age) : null;
	}
}
