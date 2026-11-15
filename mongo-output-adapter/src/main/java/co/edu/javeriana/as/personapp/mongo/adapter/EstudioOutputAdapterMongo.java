package co.edu.javeriana.as.personapp.mongo.adapter;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.mongodb.MongoWriteException;

import co.edu.javeriana.as.personapp.application.port.out.StudyOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.mongo.document.EstudiosDocument;
import co.edu.javeriana.as.personapp.mongo.mapper.EstudiosMapperMongo;
import co.edu.javeriana.as.personapp.mongo.repository.EstudioRepositoryMongo;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Adapter("studyOutputAdapterMongo")
public class EstudioOutputAdapterMongo implements StudyOutputPort {

        @Autowired
        private EstudioRepositoryMongo estudioRepositoryMongo;

        @Autowired
        private EstudiosMapperMongo estudiosMapperMongo;

        private String buildId(Integer ccPer, Integer idProf) {
                return ccPer + "-" + idProf;
        }

        @Override
        public Study save(Study study) {
            log.debug("Into save on Adapter MongoDB");
            try {
                    estudioRepositoryMongo.save(estudiosMapperMongo.fromDomainToAdapter(study));
                    // save devuelve el mismo documento con persona/profesión solo con id (referencia mínima).
                    // Se relee para devolver el estudio completo (Person.firstName es @NonNull).
                    return findById(study.getPerson().getIdentification(), study.getProfession().getIdentification());
            } catch (MongoWriteException e) {
                    log.warn(e.getMessage());
                    return study;
            }
        }

        @Override
        public Boolean delete(Integer ccPer, Integer idProf) {
                log.debug("Into delete on Adapter MongoDB");
                String id = buildId(ccPer, idProf);
                estudioRepositoryMongo.deleteById(id);
                return estudioRepositoryMongo.findById(id).isEmpty();
        }

        @Override
        public List<Study> find() {
                log.debug("Into find on Adapter MongoDB");
                return estudioRepositoryMongo.findAll().stream().map(estudiosMapperMongo::fromAdapterToDomain)
                                .collect(Collectors.toList());
        }

        @Override
        public Study findById(Integer ccPer, Integer idProf) {
                log.debug("Into findById on Adapter MongoDB");
                return estudioRepositoryMongo.findById(buildId(ccPer, idProf))
                                .map(estudiosMapperMongo::fromAdapterToDomain).orElse(null);
        }

        @Override
        public Long count() {
                log.debug("Into count on Adapter MongoDB");
                return estudioRepositoryMongo.count();
        }
}