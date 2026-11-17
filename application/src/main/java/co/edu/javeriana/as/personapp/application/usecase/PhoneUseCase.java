package co.edu.javeriana.as.personapp.application.usecase;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;

import co.edu.javeriana.as.personapp.application.port.in.PhoneInputPort;
import co.edu.javeriana.as.personapp.application.port.out.PhoneOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.UseCase;
import co.edu.javeriana.as.personapp.common.exceptions.AlreadyExistException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Phone;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UseCase
public class PhoneUseCase implements PhoneInputPort {

	private PhoneOutputPort phonePersintence;

	public PhoneUseCase(@Qualifier("phoneOutputAdapterMaria") PhoneOutputPort phonePersintence) {
		this.phonePersintence = phonePersintence;
	}

	@Override
	public void setPersintence(PhoneOutputPort phonePersintence) {
		this.phonePersintence = phonePersintence;
	}

	@Override
	public Phone create(Phone phone) throws AlreadyExistException {
		log.debug("Into create on Application Domain");
		// save actua como "guardar o actualizar": sin esta validacion, crear sobre un numero existente lo sobrescribe
		if (phonePersintence.findById(phone.getNumber()) != null)
			throw new AlreadyExistException("The Phone " + phone.getNumber() + " already exists into db, cannot be created");
		return phonePersintence.save(phone);
	}

	@Override
	public Phone edit(String number, Phone phone) throws NoExistException {
		Phone old = phonePersintence.findById(number);
		if (old != null) {
			phone.setNumber(old.getNumber()); // el numero (llave) no cambia; si cambian operador y/o dueño
			return phonePersintence.save(phone);
		}
		throw new NoExistException("The Phone " + number + " does not exist into db, cannot be edited");
	}

	@Override
	public Boolean drop(String number) throws NoExistException {
		Phone old = phonePersintence.findById(number);
		if (old != null)
			return phonePersintence.delete(number);
		throw new NoExistException("The Phone " + number + " does not exist into db, cannot be dropped");
	}

	@Override
	public List<Phone> findAll() {
		log.info("Output: " + phonePersintence.getClass());
		return phonePersintence.find();
	}

	@Override
	public Phone findOne(String number) throws NoExistException {
		Phone old = phonePersintence.findById(number);
		if (old != null)
			return old;
		throw new NoExistException("The Phone " + number + " does not exist into db, cannot be found");
	}

	@Override
	public Integer count() {
		return Math.toIntExact(phonePersintence.count());
	}

	@Override
	public Person getOwner(String number) throws NoExistException {
		Phone old = phonePersintence.findById(number);
		if (old != null)
			return old.getOwner();
		throw new NoExistException("The Phone " + number + " does not exist into db, cannot be getting owner");
	}
}
