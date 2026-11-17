package co.edu.javeriana.as.personapp.application.port.in;

import java.util.List;

import co.edu.javeriana.as.personapp.application.port.out.PersonOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.Port;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.common.exceptions.AlreadyExistException;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.domain.Study;

//Los input adapters solo pueden interactuar con los input ports. Solo conocen esta interfaz.
//Usa el lenguaje del dominio. No conoce ni el lenguaje de la infraestructura ni el de la aplicacion. Solo conoce el lenguaje del dominio.
@Port
public interface PersonInputPort {
	
	public void setPersintence(PersonOutputPort personPersintence);
	
	public Person create(Person person) throws AlreadyExistException;

	public Person edit(Integer identification, Person person) throws NoExistException;

	public Boolean drop(Integer identification) throws NoExistException;

	public List<Person> findAll();

	public Person findOne(Integer identification) throws NoExistException;

	public Integer count();

	public List<Phone> getPhones(Integer identification) throws NoExistException;

	public List<Study> getStudies(Integer identification) throws NoExistException;
}
