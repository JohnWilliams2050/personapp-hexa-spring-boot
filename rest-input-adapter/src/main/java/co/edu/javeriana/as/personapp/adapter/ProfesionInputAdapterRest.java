package co.edu.javeriana.as.personapp.adapter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import co.edu.javeriana.as.personapp.application.port.in.ProfessionInputPort;
import co.edu.javeriana.as.personapp.application.port.out.ProfessionOutputPort;
import co.edu.javeriana.as.personapp.application.usecase.ProfessionUseCase;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.common.exceptions.AlreadyExistException;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.common.setup.DatabaseOption;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.mapper.EstudioMapperRest;
import co.edu.javeriana.as.personapp.mapper.ProfesionMapperRest;
import co.edu.javeriana.as.personapp.model.request.ProfesionRequest;
import co.edu.javeriana.as.personapp.model.response.CountResponse;
import co.edu.javeriana.as.personapp.model.response.EstudioResponse;
import co.edu.javeriana.as.personapp.model.response.ProfesionResponse;
import co.edu.javeriana.as.personapp.model.response.Response;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Adapter
public class ProfesionInputAdapterRest {

        @Autowired
        @Qualifier("professionOutputAdapterMaria")
        private ProfessionOutputPort professionOutputPortMaria;

        @Autowired
        @Qualifier("professionOutputAdapterMongo")
        private ProfessionOutputPort professionOutputPortMongo;

        @Autowired
        private ProfesionMapperRest profesionMapperRest;

        @Autowired
        private EstudioMapperRest estudioMapperRest;

        // El caso de uso se devuelve, no se guarda en un campo: este adaptador es singleton y lo comparten
        // todas las peticiones HTTP, así que un campo mezclaría la base elegida entre peticiones concurrentes.
        private ProfessionInputPort professionInputPort(DatabaseOption database) {
                return new ProfessionUseCase(
                                database == DatabaseOption.MARIA ? professionOutputPortMaria : professionOutputPortMongo);
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

        public List<ProfesionResponse> historial(String dbOption) throws InvalidOptionException {
                log.info("Into historial ProfesionEntity in Input Adapter");
                DatabaseOption database = validateDatabase(dbOption);
                return professionInputPort(database).findAll().stream().map(profesionMapperRest::fromDomainToAdapterRest)
                                .collect(Collectors.toList());
        }

        public ProfesionResponse buscarProfesion(String dbOption, Integer id)
                        throws InvalidOptionException, NoExistException {
                DatabaseOption database = validateDatabase(dbOption);
                return profesionMapperRest.fromDomainToAdapterRest(professionInputPort(database).findOne(id));
        }

        public CountResponse contarProfesiones(String dbOption) throws InvalidOptionException {
                DatabaseOption database = validateDatabase(dbOption);
                return new CountResponse(database.toString(), professionInputPort(database).count(), "OK");
        }

        // ---------- Read: agregación (los estudios de la profesión) ----------

        public List<EstudioResponse> estudiosProfesion(String dbOption, Integer id)
                        throws InvalidOptionException, NoExistException {
                DatabaseOption database = validateDatabase(dbOption);
                return professionInputPort(database).getStudies(id).stream()
                                .map(estudioMapperRest::fromDomainToAdapterRest).collect(Collectors.toList());
        }

        // ---------- Create / Update / Delete ----------

        public ProfesionResponse crearProfesion(ProfesionRequest request)
                        throws InvalidOptionException, AlreadyExistException {
                DatabaseOption database = validateDatabase(request.getDatabase());
                Profession profession = professionInputPort(database)
                                .create(profesionMapperRest.fromAdapterToDomain(request));
                return profesionMapperRest.fromDomainToAdapterRest(profession);
        }

        public ProfesionResponse editarProfesion(ProfesionRequest request)
                        throws InvalidOptionException, NoExistException {
                DatabaseOption database = validateDatabase(request.getDatabase());
                Profession profession = profesionMapperRest.fromAdapterToDomain(request);
                Profession editada = professionInputPort(database).edit(profession.getIdentification(), profession);
                return profesionMapperRest.fromDomainToAdapterRest(editada);
        }

        public Response eliminarProfesion(String dbOption, Integer id) throws InvalidOptionException, NoExistException {
                DatabaseOption database = validateDatabase(dbOption);
                Boolean deleted = professionInputPort(database).drop(id);
                String key = "Profession " + id;
                return new Response(deleted ? "OK" : "ERROR",
                                deleted ? key + " deleted from " + database : key + " could not be deleted",
                                LocalDateTime.now());
        }
}
