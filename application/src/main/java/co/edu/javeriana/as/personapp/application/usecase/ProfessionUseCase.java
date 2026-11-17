package co.edu.javeriana.as.personapp.application.usecase;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;

import co.edu.javeriana.as.personapp.application.port.in.ProfessionInputPort;
import co.edu.javeriana.as.personapp.application.port.out.ProfessionOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.UseCase;
import co.edu.javeriana.as.personapp.common.exceptions.AlreadyExistException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.domain.Study;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UseCase
public class ProfessionUseCase implements ProfessionInputPort {

	private ProfessionOutputPort professionPersintence;

	public ProfessionUseCase(@Qualifier("professionOutputAdapterMaria") ProfessionOutputPort professionPersintence) {
		this.professionPersintence = professionPersintence;
	}

	@Override
	public void setPersintence(ProfessionOutputPort professionPersintence) {
		this.professionPersintence = professionPersintence;
	}

	@Override
	public Profession create(Profession profession) throws AlreadyExistException {
		log.debug("Into create on Application Domain");
		// save actua como "guardar o actualizar": sin esta validacion, crear sobre un id existente lo sobrescribe
		if (professionPersintence.findById(profession.getIdentification()) != null)
			throw new AlreadyExistException("The Profession with ID " + profession.getIdentification()
					+ " already exists into db, cannot be created");
		return professionPersintence.save(profession);
	}

	@Override
	public Profession edit(Integer identification, Profession profession) throws NoExistException {
		Profession old = professionPersintence.findById(identification);
		if (old != null) {
			profession.setIdentification(old.getIdentification()); // el id no cambia
			return professionPersintence.save(profession);
		}
		throw new NoExistException("The Profession with ID " + identification + " does not exist into db, cannot be edited");
	}

	@Override
	public Boolean drop(Integer identification) throws NoExistException {
		Profession old = professionPersintence.findById(identification);
		if (old != null)
			return professionPersintence.delete(identification);
		throw new NoExistException("The Profession with ID " + identification + " does not exist into db, cannot be dropped");
	}

	@Override
	public List<Profession> findAll() {
		log.info("Output: " + professionPersintence.getClass());
		return professionPersintence.find();
	}

	@Override
	public Profession findOne(Integer identification) throws NoExistException {
		Profession old = professionPersintence.findById(identification);
		if (old != null)
			return old;
		throw new NoExistException("The Profession with ID " + identification + " does not exist into db, cannot be found");
	}

	@Override
	public Integer count() {
		return Math.toIntExact(professionPersintence.count());
	}

	@Override
	public List<Study> getStudies(Integer identification) throws NoExistException {
		Profession old = professionPersintence.findById(identification);
		if (old != null)
			return old.getStudies();
		throw new NoExistException("The Profession with ID " + identification + " does not exist into db, cannot be getting studies");
	}
}
