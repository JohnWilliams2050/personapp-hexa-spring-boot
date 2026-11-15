package co.edu.javeriana.as.personapp.application.port.in;

import java.util.List;

import co.edu.javeriana.as.personapp.application.port.out.StudyOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.Port;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.common.exceptions.AlreadyExistException;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Profession;
//Los input adapters solo pueden interactuar con los input ports. Solo conocen esta interfaz.
//Usa el lenguaje del dominio. No conoce ni el lenguaje de la infraestructura ni el de la aplicacion. Solo conoce el lenguaje del dominio.
@Port
public interface StudyInputPort {
	
	public void setPersintence(StudyOutputPort studyPersintence);
	
	public Study create(Study study) throws AlreadyExistException;

	public Study edit(Integer ccPer, Integer idProf, Study study) throws NoExistException;

	public Boolean drop(Integer ccPer, Integer idProf) throws NoExistException;

	public List<Study> findAll();

	public Study findOne(Integer ccPer, Integer idProf) throws NoExistException;

	public Integer count();

	public Person getPerson(Integer ccPer, Integer idProf) throws NoExistException;

	public Profession getProfession(Integer ccPer, Integer idProf) throws NoExistException;
}
