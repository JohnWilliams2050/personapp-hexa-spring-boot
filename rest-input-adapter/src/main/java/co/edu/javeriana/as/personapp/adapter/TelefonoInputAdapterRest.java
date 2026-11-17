package co.edu.javeriana.as.personapp.adapter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import co.edu.javeriana.as.personapp.application.port.in.PhoneInputPort;
import co.edu.javeriana.as.personapp.application.port.out.PhoneOutputPort;
import co.edu.javeriana.as.personapp.application.usecase.PhoneUseCase;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.common.exceptions.AlreadyExistException;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.common.setup.DatabaseOption;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.mapper.PersonaMapperRest;
import co.edu.javeriana.as.personapp.mapper.TelefonoMapperRest;
import co.edu.javeriana.as.personapp.model.request.TelefonoRequest;
import co.edu.javeriana.as.personapp.model.response.CountResponse;
import co.edu.javeriana.as.personapp.model.response.PersonaResponse;
import co.edu.javeriana.as.personapp.model.response.Response;
import co.edu.javeriana.as.personapp.model.response.TelefonoResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Adapter
public class TelefonoInputAdapterRest {

        @Autowired
        @Qualifier("phoneOutputAdapterMaria")
        private PhoneOutputPort phoneOutputPortMaria;

        @Autowired
        @Qualifier("phoneOutputAdapterMongo")
        private PhoneOutputPort phoneOutputPortMongo;

        @Autowired
        private TelefonoMapperRest telefonoMapperRest;

        @Autowired
        private PersonaMapperRest personaMapperRest;

        // El caso de uso se devuelve, no se guarda en un campo: este adaptador es singleton y lo comparten
        // todas las peticiones HTTP, así que un campo mezclaría la base elegida entre peticiones concurrentes.
        private PhoneInputPort phoneInputPort(DatabaseOption database) {
                return new PhoneUseCase(database == DatabaseOption.MARIA ? phoneOutputPortMaria : phoneOutputPortMongo);
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

        public List<TelefonoResponse> historial(String dbOption) throws InvalidOptionException {
                log.info("Into historial TelefonoEntity in Input Adapter");
                DatabaseOption database = validateDatabase(dbOption);
                return phoneInputPort(database).findAll().stream().map(telefonoMapperRest::fromDomainToAdapterRest)
                                .collect(Collectors.toList());
        }

        public TelefonoResponse buscarTelefono(String dbOption, String number)
                        throws InvalidOptionException, NoExistException {
                DatabaseOption database = validateDatabase(dbOption);
                return telefonoMapperRest.fromDomainToAdapterRest(phoneInputPort(database).findOne(number));
        }

        public CountResponse contarTelefonos(String dbOption) throws InvalidOptionException {
                DatabaseOption database = validateDatabase(dbOption);
                return new CountResponse(database.toString(), phoneInputPort(database).count(), "OK");
        }

        // ---------- Read: agregación (el dueño del teléfono) ----------

        public PersonaResponse duenioTelefono(String dbOption, String number)
                        throws InvalidOptionException, NoExistException {
                DatabaseOption database = validateDatabase(dbOption);
                Person owner = phoneInputPort(database).getOwner(number);
                return database == DatabaseOption.MARIA ? personaMapperRest.fromDomainToAdapterRestMaria(owner)
                                : personaMapperRest.fromDomainToAdapterRestMongo(owner);
        }

        // ---------- Create / Update / Delete ----------

        public TelefonoResponse crearTelefono(TelefonoRequest request) throws InvalidOptionException, AlreadyExistException {
                DatabaseOption database = validateDatabase(request.getDatabase());
                Phone phone = phoneInputPort(database).create(telefonoMapperRest.fromAdapterToDomain(request));
                return telefonoMapperRest.fromDomainToAdapterRest(phone);
        }

        public TelefonoResponse editarTelefono(TelefonoRequest request) throws InvalidOptionException, NoExistException {
                DatabaseOption database = validateDatabase(request.getDatabase());
                Phone phone = telefonoMapperRest.fromAdapterToDomain(request);
                Phone editado = phoneInputPort(database).edit(phone.getNumber(), phone);
                return telefonoMapperRest.fromDomainToAdapterRest(editado);
        }

        public Response eliminarTelefono(String dbOption, String number) throws InvalidOptionException, NoExistException {
                DatabaseOption database = validateDatabase(dbOption);
                Boolean deleted = phoneInputPort(database).drop(number);
                String key = "Phone " + number;
                return new Response(deleted ? "OK" : "ERROR",
                                deleted ? key + " deleted from " + database : key + " could not be deleted",
                                LocalDateTime.now());
        }
}
