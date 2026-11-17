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

import co.edu.javeriana.as.personapp.adapter.ProfesionInputAdapterRest;
import co.edu.javeriana.as.personapp.common.exceptions.AlreadyExistException;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.model.request.ProfesionRequest;
import co.edu.javeriana.as.personapp.model.response.CountResponse;
import co.edu.javeriana.as.personapp.model.response.EstudioResponse;
import co.edu.javeriana.as.personapp.model.response.ProfesionResponse;
import co.edu.javeriana.as.personapp.model.response.Response;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(path = "/api/v1/profesion", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProfesionControllerV1 {

        @Autowired
        private ProfesionInputAdapterRest profesionInputAdapterRest;

        @GetMapping("/{database}")
        public List<ProfesionResponse> profesiones(@PathVariable String database) throws InvalidOptionException {
                log.info("Into profesiones REST API");
                return profesionInputAdapterRest.historial(database);
        }

        @GetMapping("/{database}/count")
        public CountResponse contarProfesiones(@PathVariable String database) throws InvalidOptionException {
                return profesionInputAdapterRest.contarProfesiones(database);
        }

        @GetMapping("/{database}/{id}")
        public ProfesionResponse profesion(@PathVariable String database, @PathVariable Integer id)
                        throws InvalidOptionException, NoExistException {
                return profesionInputAdapterRest.buscarProfesion(database, id);
        }

        @GetMapping("/{database}/{id}/estudios")
        public List<EstudioResponse> estudios(@PathVariable String database, @PathVariable Integer id)
                        throws InvalidOptionException, NoExistException {
                return profesionInputAdapterRest.estudiosProfesion(database, id);
        }

        @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
        @ResponseStatus(HttpStatus.CREATED)
        public ProfesionResponse crearProfesion(@RequestBody ProfesionRequest request)
                        throws InvalidOptionException, AlreadyExistException {
                log.info("Into crearProfesion REST API");
                return profesionInputAdapterRest.crearProfesion(request);
        }

        @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
        public ProfesionResponse editarProfesion(@RequestBody ProfesionRequest request)
                        throws InvalidOptionException, NoExistException {
                log.info("Into editarProfesion REST API");
                return profesionInputAdapterRest.editarProfesion(request);
        }

        @DeleteMapping("/{database}/{id}")
        public Response eliminarProfesion(@PathVariable String database, @PathVariable Integer id)
                        throws InvalidOptionException, NoExistException {
                log.info("Into eliminarProfesion REST API");
                return profesionInputAdapterRest.eliminarProfesion(database, id);
        }
}
