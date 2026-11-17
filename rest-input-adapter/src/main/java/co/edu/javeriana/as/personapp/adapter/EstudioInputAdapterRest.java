package co.edu.javeriana.as.personapp.adapter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import co.edu.javeriana.as.personapp.application.port.in.StudyInputPort;
import co.edu.javeriana.as.personapp.application.port.out.StudyOutputPort;
import co.edu.javeriana.as.personapp.application.usecase.StudyUseCase;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.common.exceptions.AlreadyExistException;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.common.setup.DatabaseOption;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.mapper.EstudioMapperRest;
import co.edu.javeriana.as.personapp.mapper.PersonaMapperRest;
import co.edu.javeriana.as.personapp.model.request.EstudioRequest;
import co.edu.javeriana.as.personapp.model.response.CountResponse;
import co.edu.javeriana.as.personapp.model.response.EstudioProfesionResponse;
import co.edu.javeriana.as.personapp.model.response.EstudioResponse;
import co.edu.javeriana.as.personapp.model.response.PersonaResponse;
import co.edu.javeriana.as.personapp.model.response.Response;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Profession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Adapter
public class EstudioInputAdapterRest {

        @Autowired
        @Qualifier("studyOutputAdapterMaria")
        private StudyOutputPort studyOutputPortMaria;

        @Autowired
        @Qualifier("studyOutputAdapterMongo")
        private StudyOutputPort studyOutputPortMongo;

        @Autowired
        private EstudioMapperRest estudioMapperRest;

        @Autowired
        private PersonaMapperRest personaMapperRest;

        // El caso de uso se devuelve, no se guarda en un campo: este adaptador es singleton y lo comparten
        // todas las peticiones HTTP, así que un campo mezclaría la base elegida entre peticiones concurrentes.
        private StudyInputPort studyInputPort(DatabaseOption database) {
                return new StudyUseCase(database == DatabaseOption.MARIA ? studyOutputPortMaria : studyOutputPortMongo);
        }

        private DatabaseOption validateDatabase(String dbOption) throws InvalidOptionException {
                if (dbOption != null) {
                        for (DatabaseOption option : DatabaseOption.values())
                                if (option.toString().equalsIgnoreCase(dbOption.trim()))
                                        return option;
                }
                throw new InvalidOptionException("Invalid database option: " + dbOption);
        }

        // ---------- Read ----------

        public List<EstudioResponse> historial(String dbOption) throws InvalidOptionException {
                log.info("Into historial EstudiosEntity in Input Adapter");
                DatabaseOption database = validateDatabase(dbOption);
                return studyInputPort(database).findAll().stream().map(estudioMapperRest::fromDomainToAdapterRest)
                                .collect(Collectors.toList());
        }

        public EstudioResponse buscarEstudio(String dbOption, Integer ccPer, Integer idProf)
                        throws InvalidOptionException, NoExistException {
                DatabaseOption database = validateDatabase(dbOption);
                return estudioMapperRest.fromDomainToAdapterRest(studyInputPort(database).findOne(ccPer, idProf));
        }

        public CountResponse contarEstudios(String dbOption) throws InvalidOptionException {
                DatabaseOption database = validateDatabase(dbOption);
                return new CountResponse(database.toString(), studyInputPort(database).count(), "OK");
        }

        // ---------- Read: agregación (la persona y la profesión del estudio) ----------

        public PersonaResponse personaEstudio(String dbOption, Integer ccPer, Integer idProf)
                        throws InvalidOptionException, NoExistException {
                DatabaseOption database = validateDatabase(dbOption);
                var person = studyInputPort(database).getPerson(ccPer, idProf);
                return database == DatabaseOption.MARIA ? personaMapperRest.fromDomainToAdapterRestMaria(person)
                                : personaMapperRest.fromDomainToAdapterRestMongo(person);
        }

        public EstudioProfesionResponse profesionEstudio(String dbOption, Integer ccPer, Integer idProf)
                        throws InvalidOptionException, NoExistException {
                DatabaseOption database = validateDatabase(dbOption);
                return estudioMapperRest.fromDomainToAdapterRestProfesion(studyInputPort(database).getProfession(ccPer, idProf));
        }

        // ---------- Create / Update / Delete ----------

        public EstudioResponse crearEstudio(EstudioRequest request) throws InvalidOptionException, AlreadyExistException {
                DatabaseOption database = validateDatabase(request.getDatabase());
                Study study = studyInputPort(database).create(estudioMapperRest.fromAdapterToDomain(request));
                return estudioMapperRest.fromDomainToAdapterRest(study);
        }

        public EstudioResponse editarEstudio(EstudioRequest request) throws InvalidOptionException, NoExistException {
                DatabaseOption database = validateDatabase(request.getDatabase());
                Study study = estudioMapperRest.fromAdapterToDomain(request);
                Study editado = studyInputPort(database).edit(study.getPerson().getIdentification(),
                                study.getProfession().getIdentification(), study);
                return estudioMapperRest.fromDomainToAdapterRest(editado);
        }

        public Response eliminarEstudio(String dbOption, Integer ccPer, Integer idProf)
                        throws InvalidOptionException, NoExistException {
                DatabaseOption database = validateDatabase(dbOption);
                Boolean deleted = studyInputPort(database).drop(ccPer, idProf);
                String key = "Study (person " + ccPer + ", profession " + idProf + ")";
                return new Response(deleted ? "OK" : "ERROR",
                                deleted ? key + " deleted from " + database : key + " could not be deleted",
                                LocalDateTime.now());
        }
}