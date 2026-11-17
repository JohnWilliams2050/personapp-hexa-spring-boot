package co.edu.javeriana.as.personapp.mariadb.adapter;

import java.util.List;

import java.util.stream.Collectors;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;

import co.edu.javeriana.as.personapp.application.port.out.StudyOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.mariadb.entity.EstudiosEntity;
import co.edu.javeriana.as.personapp.mariadb.entity.EstudiosEntityPK;
import co.edu.javeriana.as.personapp.mariadb.mapper.EstudiosMapperMaria;
import co.edu.javeriana.as.personapp.mariadb.repository.EstudioRepositoryMaria;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Adapter("studyOutputAdapterMaria")
@Transactional
public class StudyOutputAdapterMaria implements StudyOutputPort {

	@Autowired
	private EstudioRepositoryMaria estudioRepositoryMaria;

	@Autowired
	private EstudiosMapperMaria estudioMapperMaria;

	@Override
	public Study save(Study study) {
		log.debug("Into save on Adapter MariaDB");
		EstudiosEntity persistedEstudio = estudioRepositoryMaria.save(estudioMapperMaria.fromDomainToAdapter(study));
		return estudioMapperMaria.fromAdapterToDomain(persistedEstudio);
	}

	@Override
	public Boolean delete(Integer ccPer, Integer idProf) {
		log.debug("Into delete on Adapter MariaDB");
		EstudiosEntityPK pk = new EstudiosEntityPK(idProf, ccPer); 
		estudioRepositoryMaria.deleteById(pk);
		return estudioRepositoryMaria.findById(pk).isEmpty();
	}

	@Override
	public List<Study> find() {
		log.debug("Into find on Adapter MariaDB");
		return estudioRepositoryMaria.findAll().stream().map(estudioMapperMaria::fromAdapterToDomain)
				.collect(Collectors.toList());
	}

	@Override
	public Study findById(Integer ccPer, Integer idProf) {
		log.debug("Into findById on Adapter MariaDB");
		EstudiosEntityPK pk = new EstudiosEntityPK(idProf, ccPer);
		if (estudioRepositoryMaria.findById(pk).isEmpty()) {
			return null;
		} else {
			return estudioMapperMaria.fromAdapterToDomain(estudioRepositoryMaria.findById(pk).get());
		}
	}

	//para que no se cuente por cargar toda la lista de estudios, sino que se haga un count en la base de datos.
	@Override
	public Long count() {
		log.debug("Into count on Adapter MariaDB");
		return estudioRepositoryMaria.count();
	}

}