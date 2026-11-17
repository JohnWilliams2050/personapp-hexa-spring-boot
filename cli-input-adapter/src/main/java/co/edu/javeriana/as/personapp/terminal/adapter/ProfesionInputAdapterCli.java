package co.edu.javeriana.as.personapp.terminal.adapter;

import java.util.List;

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
import co.edu.javeriana.as.personapp.terminal.mapper.EstudioMapperCli;
import co.edu.javeriana.as.personapp.terminal.mapper.ProfesionMapperCli;
import co.edu.javeriana.as.personapp.terminal.model.ProfesionModelCli;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Adapter
public class ProfesionInputAdapterCli {

        @Autowired
        @Qualifier("professionOutputAdapterMaria")
        private ProfessionOutputPort professionOutputPortMaria;

        @Autowired
        @Qualifier("professionOutputAdapterMongo")
        private ProfessionOutputPort professionOutputPortMongo;

        @Autowired
        private ProfesionMapperCli profesionMapperCli;

        @Autowired
        private EstudioMapperCli estudioMapperCli;

        // La CLI es de un solo usuario, así que la base elegida en el menú puede vivir en un campo
        ProfessionInputPort professionInputPort;

        // Elige el puerto de salida según la base de datos que escogió el usuario en el menú
        public void setProfessionOutputPortInjection(String dbOption) throws InvalidOptionException {
                if (dbOption.equalsIgnoreCase(DatabaseOption.MARIA.toString())) {
                        professionInputPort = new ProfessionUseCase(professionOutputPortMaria);
                } else if (dbOption.equalsIgnoreCase(DatabaseOption.MONGO.toString())) {
                        professionInputPort = new ProfessionUseCase(professionOutputPortMongo);
                } else {
                        throw new InvalidOptionException("Invalid database option: " + dbOption);
                }
        }

        // ---------- Read ----------

        public void historial() {
                log.info("Into historial ProfesionEntity in Input Adapter");
                List<Profession> profesiones = professionInputPort.findAll();
                if (profesiones.isEmpty())
                        System.out.println("No hay profesiones registradas");
                profesiones.stream().map(profesionMapperCli::fromDomainToAdapterCli).forEach(System.out::println);
        }

        public void buscar(Integer id) throws NoExistException {
                System.out.println(profesionMapperCli.fromDomainToAdapterCli(professionInputPort.findOne(id)));
        }

        public void contar() {
                System.out.println("Total de profesiones: " + professionInputPort.count());
        }

        // ---------- Read: agregación (los estudios de la profesión) ----------

        public void estudios(Integer id) throws NoExistException {
                var estudios = professionInputPort.getStudies(id);
                if (estudios.isEmpty())
                        System.out.println("La profesión no tiene estudios registrados");
                estudios.stream().map(estudioMapperCli::fromDomainToAdapterCli).forEach(System.out::println);
        }

        // ---------- Create / Update / Delete ----------

        public void crear(ProfesionModelCli profesion) throws AlreadyExistException {
                Profession creada = professionInputPort.create(profesionMapperCli.fromAdapterCliToDomain(profesion));
                System.out.println("Profesión creada: " + profesionMapperCli.fromDomainToAdapterCli(creada));
        }

        public void editar(ProfesionModelCli profesion) throws NoExistException {
                Profession editada = professionInputPort.edit(profesion.getId(),
                                profesionMapperCli.fromAdapterCliToDomain(profesion));
                System.out.println("Profesión editada: " + profesionMapperCli.fromDomainToAdapterCli(editada));
        }

        public void eliminar(Integer id) throws NoExistException {
                System.out.println(professionInputPort.drop(id) ? "Profesión " + id + " eliminada"
                                : "La profesión no se pudo eliminar");
        }
}
