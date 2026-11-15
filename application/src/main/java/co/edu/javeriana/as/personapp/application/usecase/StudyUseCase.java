package co.edu.javeriana.as.personapp.application.usecase;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;

import co.edu.javeriana.as.personapp.application.port.in.StudyInputPort;
import co.edu.javeriana.as.personapp.application.port.out.StudyOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.UseCase;
import co.edu.javeriana.as.personapp.common.exceptions.AlreadyExistException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Profession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UseCase
public class StudyUseCase implements StudyInputPort {

	private StudyOutputPort studyPersintence;

	public StudyUseCase(@Qualifier("studyOutputAdapterMaria") StudyOutputPort studyPersintence) {
		this.studyPersintence = studyPersintence;
	}

	@Override
	public void setPersintence(StudyOutputPort studyPersintence) {
		this.studyPersintence = studyPersintence;
	}

	@Override
	public Study create(Study study) throws AlreadyExistException {
		log.debug("Into create on Application Domain");
		Integer ccPer = study.getPerson().getIdentification();
		Integer idProf = study.getProfession().getIdentification();
		// save actua como "guardar o actualizar": sin esta validacion, crear sobre una llave existente la sobrescribe
		if (studyPersintence.findById(ccPer, idProf) != null)
			throw new AlreadyExistException(
					"The Study with Person ID " + ccPer + " and Profesion ID " + idProf + " already exists into db, cannot be created");
		return studyPersintence.save(study);
	}

	@Override
	public Study edit(Integer ccPer, Integer idProf, Study study) throws NoExistException {
		Study oldStudy = studyPersintence.findById(ccPer, idProf);
		if (oldStudy != null) {
			// La llave (persona + profesion) no cambia: se conserva la del registro existente
			study.setPerson(oldStudy.getPerson());
			study.setProfession(oldStudy.getProfession());
			return studyPersintence.save(study);
		}
		throw new NoExistException(
				"The Study with Person ID " + ccPer + " and Profesion ID " + idProf + " does not exist into db, cannot be edited");
	}

	@Override
	public Boolean drop(Integer ccPer, Integer idProf) throws NoExistException {
		Study oldStudy = studyPersintence.findById(ccPer, idProf);
		if (oldStudy != null)
			return studyPersintence.delete(ccPer, idProf);
		throw new NoExistException(
				"The Study with Person ID " + ccPer + " and Profesion ID " + idProf + " does not exist into db, cannot be dropped");
	}

	@Override
	public List<Study> findAll() {
		log.info("Output: " + studyPersintence.getClass());
		return studyPersintence.find();
	}

	@Override
	public Study findOne(Integer ccPer, Integer idProf) throws NoExistException {
		Study oldStudy = studyPersintence.findById(ccPer, idProf);
		if (oldStudy != null)
			return oldStudy;
		throw new NoExistException(
				"The Study with Person ID " + ccPer + " and Profesion ID " + idProf + " does not exist into db, cannot be found");
	}

	@Override
	public Integer count() {
		return Math.toIntExact(studyPersintence.count()); // count en la base de datos, sin cargar toda la lista de estudios.
	}

	@Override
	public Person getPerson(Integer ccPer, Integer idProf) throws NoExistException {
		Study oldStudy = studyPersintence.findById(ccPer, idProf);
		if (oldStudy != null)
			return oldStudy.getPerson();
		throw new NoExistException(
				"The Study with Person ID " + ccPer + " and Profesion ID " + idProf + " does not exist into db, cannot be getting person");
	}

	@Override
	public Profession getProfession(Integer ccPer, Integer idProf) throws NoExistException {
		Study oldStudy = studyPersintence.findById(ccPer, idProf);
		if (oldStudy != null)
			return oldStudy.getProfession();
		throw new NoExistException(
				"The Study with Person ID " + ccPer + " and Profesion ID " + idProf + " does not exist into db, cannot be getting profession");
	}
}