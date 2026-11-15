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

import co.edu.javeriana.as.personapp.adapter.EstudioInputAdapterRest;
import co.edu.javeriana.as.personapp.common.exceptions.AlreadyExistException;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.model.request.EstudioRequest;
import co.edu.javeriana.as.personapp.model.response.CountResponse;
import co.edu.javeriana.as.personapp.model.response.EstudioProfesionResponse;
import co.edu.javeriana.as.personapp.model.response.EstudioResponse;
import co.edu.javeriana.as.personapp.model.response.PersonaResponse;
import co.edu.javeriana.as.personapp.model.response.Response;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(path = "/api/v1/estudio", produces = MediaType.APPLICATION_JSON_VALUE)
public class EstudioControllerV1 {

        @Autowired
        private EstudioInputAdapterRest estudioInputAdapterRest;

        @GetMapping("/{database}")
        public List<EstudioResponse> estudios(@PathVariable String database) throws InvalidOptionException {
                log.info("Into estudios REST API");
                return estudioInputAdapterRest.historial(database);
        }

        @GetMapping("/{database}/count")
        public CountResponse contarEstudios(@PathVariable String database) throws InvalidOptionException {
                return estudioInputAdapterRest.contarEstudios(database);
        }

        // La llave del estudio es compuesta, así que el id viaja como dos segmentos: /{ccPer}/{idProf}
        @GetMapping("/{database}/{ccPer}/{idProf}")
        public EstudioResponse estudio(@PathVariable String database, @PathVariable Integer ccPer,
                        @PathVariable Integer idProf) throws InvalidOptionException, NoExistException {
                return estudioInputAdapterRest.buscarEstudio(database, ccPer, idProf);
        }

        @GetMapping("/{database}/{ccPer}/{idProf}/persona")
        public PersonaResponse persona(@PathVariable String database, @PathVariable Integer ccPer,
                        @PathVariable Integer idProf) throws InvalidOptionException, NoExistException {
                return estudioInputAdapterRest.personaEstudio(database, ccPer, idProf);
        }

        @GetMapping("/{database}/{ccPer}/{idProf}/profesion")
        public EstudioProfesionResponse profesion(@PathVariable String database, @PathVariable Integer ccPer,
                        @PathVariable Integer idProf) throws InvalidOptionException, NoExistException {
                return estudioInputAdapterRest.profesionEstudio(database, ccPer, idProf);
        }

        @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
        @ResponseStatus(HttpStatus.CREATED)
        public EstudioResponse crearEstudio(@RequestBody EstudioRequest request)
                        throws InvalidOptionException, AlreadyExistException {
                log.info("Into crearEstudio REST API");
                return estudioInputAdapterRest.crearEstudio(request);
        }

        @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
        public EstudioResponse editarEstudio(@RequestBody EstudioRequest request)
                        throws InvalidOptionException, NoExistException {
                log.info("Into editarEstudio REST API");
                return estudioInputAdapterRest.editarEstudio(request);
        }

        @DeleteMapping("/{database}/{ccPer}/{idProf}")
        public Response eliminarEstudio(@PathVariable String database, @PathVariable Integer ccPer,
                        @PathVariable Integer idProf) throws InvalidOptionException, NoExistException {
                log.info("Into eliminarEstudio REST API");
                return estudioInputAdapterRest.eliminarEstudio(database, ccPer, idProf);
        }
}