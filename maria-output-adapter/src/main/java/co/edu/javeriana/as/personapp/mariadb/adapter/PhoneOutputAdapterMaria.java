package co.edu.javeriana.as.personapp.mariadb.adapter;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import co.edu.javeriana.as.personapp.application.port.out.PhoneOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.mariadb.mapper.TelefonoMapperMaria;
import co.edu.javeriana.as.personapp.mariadb.repository.PersonaRepositoryMaria;
import co.edu.javeriana.as.personapp.mariadb.repository.TelefonoRepositoryMaria;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Adapter("phoneOutputAdapterMaria")
@Transactional
public class PhoneOutputAdapterMaria implements PhoneOutputPort {

	@Autowired
	private TelefonoRepositoryMaria telefonoRepositoryMaria;

	@Autowired
	private PersonaRepositoryMaria personaRepositoryMaria;

	@Autowired
	private TelefonoMapperMaria telefonoMapperMaria;

	@Override
	public Phone save(Phone phone) {
		log.debug("Into save on Adapter MariaDB");
		Integer owner = phone.getOwner().getIdentification();
		// Se valida aqui para devolver un error claro (400) en vez de una violacion de llave foranea
		if (!personaRepositoryMaria.existsById(owner))
			throw new IllegalArgumentException("The person with id " + owner + " does not exist into db, cannot save the phone");
		telefonoRepositoryMaria.save(telefonoMapperMaria.fromDomainToAdapter(phone));
		// Se relee para devolver el telefono con su dueño completo (el dueño guardado solo lleva el id)
		return findById(phone.getNumber());
	}

	@Override
	public Boolean delete(String number) {
		log.debug("Into delete on Adapter MariaDB");
		telefonoRepositoryMaria.deleteById(number);
		return telefonoRepositoryMaria.findById(number).isEmpty();
	}

	@Override
	public List<Phone> find() {
		log.debug("Into find on Adapter MariaDB");
		return telefonoRepositoryMaria.findAll().stream().map(telefonoMapperMaria::fromAdapterToDomain)
				.collect(Collectors.toList());
	}

	@Override
	public Phone findById(String number) {
		log.debug("Into findById on Adapter MariaDB");
		return telefonoRepositoryMaria.findById(number).map(telefonoMapperMaria::fromAdapterToDomain).orElse(null);
	}

	@Override
	public Long count() {
		log.debug("Into count on Adapter MariaDB");
		return telefonoRepositoryMaria.count();
	}
}
