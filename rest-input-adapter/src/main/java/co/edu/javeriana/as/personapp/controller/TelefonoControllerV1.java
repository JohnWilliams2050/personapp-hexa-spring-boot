package co.edu.javeriana.as.personapp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import co.edu.javeriana.as.personapp.adapter.TelefonoInputAdapterRest;
import co.edu.javeriana.as.personapp.common.exceptions.AlreadyExistException;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.model.request.TelefonoRequest;
import co.edu.javeriana.as.personapp.model.response.CountResponse;
import co.edu.javeriana.as.personapp.model.response.PersonaResponse;
import co.edu.javeriana.as.personapp.model.response.Response;
import co.edu.javeriana.as.personapp.model.response.TelefonoResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(path = "/api/v1/telefono", produces = MediaType.APPLICATION_JSON_VALUE)
public class TelefonoControllerV1 {

        @Autowired
        private TelefonoInputAdapterRest telefonoInputAdapterRest;

        @GetMapping("/{database}")
        public List<TelefonoResponse> telefonos(@PathVariable String database) throws InvalidOptionException {
                log.info("Into telefonos REST API");
                return telefonoInputAdapterRest.historial(database);
        }

        @GetMapping("/{database}/count")
        public CountResponse contarTelefonos(@PathVariable String database) throws InvalidOptionException {
                return telefonoInputAdapterRest.contarTelefonos(database);
        }

        @GetMapping("/{database}/{number}")
        public TelefonoResponse telefono(@PathVariable String database, @PathVariable String number)
                        throws InvalidOptionException, NoExistException {
                return telefonoInputAdapterRest.buscarTelefono(database, number);
        }

        @GetMapping("/{database}/{number}/duenio")
        public PersonaResponse duenio(@PathVariable String database, @PathVariable String number)
                        throws InvalidOptionException, NoExistException {
                return telefonoInputAdapterRest.duenioTelefono(database, number);
        }

        @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
        @ResponseStatus(HttpStatus.CREATED)
        public TelefonoResponse crearTelefono(@RequestBody TelefonoRequest request)
                        throws InvalidOptionException, AlreadyExistException {
                log.info("Into crearTelefono REST API");
                return telefonoInputAdapterRest.crearTelefono(request);
        }

        @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
        public TelefonoResponse editarTelefono(@RequestBody TelefonoRequest request)
                        throws InvalidOptionException, NoExistException {
                log.info("Into editarTelefono REST API");
                return telefonoInputAdapterRest.editarTelefono(request);
        }

        @DeleteMapping("/{database}/{number}")
        public Response eliminarTelefono(@PathVariable String database, @PathVariable String number)
                        throws InvalidOptionException, NoExistException {
                log.info("Into eliminarTelefono REST API");
                return telefonoInputAdapterRest.eliminarTelefono(database, number);
        }
}
