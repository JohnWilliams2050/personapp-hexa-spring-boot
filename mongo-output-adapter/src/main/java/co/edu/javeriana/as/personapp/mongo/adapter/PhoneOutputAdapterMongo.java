package co.edu.javeriana.as.personapp.mongo.adapter;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import co.edu.javeriana.as.personapp.application.port.out.PhoneOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.mongo.document.PersonaDocument;
import co.edu.javeriana.as.personapp.mongo.mapper.TelefonoMapperMongo;
import co.edu.javeriana.as.personapp.mongo.repository.TelefonoRepositoryMongo;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Adapter("phoneOutputAdapterMongo")
public class PhoneOutputAdapterMongo implements PhoneOutputPort {

	@Autowired
	private TelefonoRepositoryMongo telefonoRepositoryMongo;

	@Autowired
	private TelefonoMapperMongo telefonoMapperMongo;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public Phone save(Phone phone) {
		log.debug("Into save on Adapter MongoDB");
		Integer owner = phone.getOwner().getIdentification();
		// MariaDB lo rechaza con su llave foranea; Mongo no valida referencias, asi que se valida aqui
		if (!mongoTemplate.exists(Query.query(Criteria.where("_id").is(owner)), PersonaDocument.class))
			throw new IllegalArgumentException("The person with id " + owner + " does not exist into db, cannot save the phone");
		telefonoRepositoryMongo.save(telefonoMapperMongo.fromDomainToAdapter(phone));
		// Se relee para devolver el telefono con su dueño completo (la referencia guardada solo lleva el id)
		return findById(phone.getNumber());
	}

	@Override
	public Boolean delete(String number) {
		log.debug("Into delete on Adapter MongoDB");
		telefonoRepositoryMongo.deleteById(number);
		return telefonoRepositoryMongo.findById(number).isEmpty();
	}

	@Override
	public List<Phone> find() {
		log.debug("Into find on Adapter MongoDB");
		return telefonoRepositoryMongo.findAll().stream().map(telefonoMapperMongo::fromAdapterToDomain)
				.collect(Collectors.toList());
	}

	@Override
	public Phone findById(String number) {
		log.debug("Into findById on Adapter MongoDB");
		return telefonoRepositoryMongo.findById(number).map(telefonoMapperMongo::fromAdapterToDomain).orElse(null);
	}

	@Override
	public Long count() {
		log.debug("Into count on Adapter MongoDB");
		return telefonoRepositoryMongo.count();
	}
}
