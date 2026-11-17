package co.edu.javeriana.as.personapp.mongo.adapter;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import co.edu.javeriana.as.personapp.application.port.out.ProfessionOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.mongo.document.EstudiosDocument;
import co.edu.javeriana.as.personapp.mongo.mapper.ProfesionMapperMongo;
import co.edu.javeriana.as.personapp.mongo.repository.ProfesionRepositoryMongo;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Adapter("professionOutputAdapterMongo")
public class ProfessionOutputAdapterMongo implements ProfessionOutputPort {

	@Autowired
	private ProfesionRepositoryMongo profesionRepositoryMongo;

	@Autowired
	private ProfesionMapperMongo profesionMapperMongo;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public Profession save(Profession profession) {
		log.debug("Into save on Adapter MongoDB");
		profesionRepositoryMongo.save(profesionMapperMongo.fromDomainToAdapter(profession));
		return findById(profession.getIdentification());
	}

	@Override
	public Boolean delete(Integer identification) {
		log.debug("Into delete on Adapter MongoDB");
		// Profession es raiz de agregacion de sus estudios. MariaDB los elimina con el cascade de JPA;
		// MongoDB no tiene cascade, asi que se eliminan aqui para no dejar documentos huerfanos.
		mongoTemplate.remove(Query.query(Criteria.where("primaryProfesion").is(identification)), EstudiosDocument.class);
		profesionRepositoryMongo.deleteById(identification);
		return profesionRepositoryMongo.findById(identification).isEmpty();
	}

	@Override
	public List<Profession> find() {
		log.debug("Into find on Adapter MongoDB");
		return profesionRepositoryMongo.findAll().stream().map(profesionMapperMongo::fromAdapterToDomain)
				.collect(Collectors.toList());
	}

	@Override
	public Profession findById(Integer identification) {
		log.debug("Into findById on Adapter MongoDB");
		return profesionRepositoryMongo.findById(identification).map(profesionMapperMongo::fromAdapterToDomain)
				.orElse(null);
	}

	@Override
	public Long count() {
		log.debug("Into count on Adapter MongoDB");
		return profesionRepositoryMongo.count();
	}
}
