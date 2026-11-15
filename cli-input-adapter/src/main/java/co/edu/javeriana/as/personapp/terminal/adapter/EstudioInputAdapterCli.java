package co.edu.javeriana.as.personapp.terminal.adapter;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.terminal.mapper.PersonaMapperCli;


import co.edu.javeriana.as.personapp.application.port.in.StudyInputPort;
import co.edu.javeriana.as.personapp.application.port.out.StudyOutputPort;
import co.edu.javeriana.as.personapp.application.usecase.StudyUseCase;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.common.exceptions.AlreadyExistException;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.common.setup.DatabaseOption;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.terminal.mapper.EstudioMapperCli;
import co.edu.javeriana.as.personapp.terminal.model.EstudioModelCli;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Adapter
public class EstudioInputAdapterCli {

        @Autowired
        @Qualifier("studyOutputAdapterMaria")
        private StudyOutputPort studyOutputPortMaria;

        @Autowired
        @Qualifier("studyOutputAdapterMongo")
        private StudyOutputPort studyOutputPortMongo;

        @Autowired
        private EstudioMapperCli estudioMapperCli;
        
        @Autowired
        private PersonaMapperCli personaMapperCli;

        // Igual que en persona: la CLI es de un solo usuario, así que la base elegida en el menú puede vivir en un campo
        StudyInputPort studyInputPort;

        // Elige el puerto de salida según la base de datos que escogió el usuario en el menú
        public void setStudyOutputPortInjection(String dbOption) throws InvalidOptionException {
                if (dbOption.equalsIgnoreCase(DatabaseOption.MARIA.toString())) {
                        studyInputPort = new StudyUseCase(studyOutputPortMaria);
                } else if (dbOption.equalsIgnoreCase(DatabaseOption.MONGO.toString())) {
                        studyInputPort = new StudyUseCase(studyOutputPortMongo);
                } else {
                        throw new InvalidOptionException("Invalid database option: " + dbOption);
                }
        }

        // ---------- Read ----------

        public void historial() {
                log.info("Into historial EstudiosEntity in Input Adapter");
                List<Study> estudios = studyInputPort.findAll();
                if (estudios.isEmpty())
                        System.out.println("No hay estudios registrados");
                estudios.stream().map(estudioMapperCli::fromDomainToAdapterCli).forEach(System.out::println);
        }

        public void buscar(Integer ccPer, Integer idProf) throws NoExistException {
                System.out.println(estudioMapperCli.fromDomainToAdapterCli(studyInputPort.findOne(ccPer, idProf)));
        }

        public void contar() {
                System.out.println("Total de estudios: " + studyInputPort.count());
        }

        // ---------- Read: agregación (persona y profesión del estudio) ----------

        public void persona(Integer ccPer, Integer idProf) throws NoExistException {
                System.out.println(personaMapperCli.fromDomainToAdapterCli(studyInputPort.getPerson(ccPer, idProf)));
        }

        public void profesion(Integer ccPer, Integer idProf) throws NoExistException {
                Profession profesion = studyInputPort.getProfession(ccPer, idProf);
                // No hay mapper CLI de profesión (es de otro módulo), así que se imprime directo
                System.out.println("Profesión " + profesion.getIdentification() + ": " + profesion.getName()
                                + " - " + profesion.getDescription());
        }

        // ---------- Create / Update / Delete ----------

        public void crear(EstudioModelCli estudio) throws AlreadyExistException {
                Study creado = studyInputPort.create(estudioMapperCli.fromAdapterCliToDomain(estudio));
                System.out.println("Estudio creado: " + estudioMapperCli.fromDomainToAdapterCli(creado));
        }

        public void editar(EstudioModelCli estudio) throws NoExistException {
                Study editado = studyInputPort.edit(estudio.getCcPer(), estudio.getIdProf(),
                                estudioMapperCli.fromAdapterCliToDomain(estudio));
                System.out.println("Estudio editado: " + estudioMapperCli.fromDomainToAdapterCli(editado));
        }

        public void eliminar(Integer ccPer, Integer idProf) throws NoExistException {
                System.out.println(studyInputPort.drop(ccPer, idProf)
                                ? "Estudio de la persona " + ccPer + " y profesión " + idProf + " eliminado"
                                : "El estudio no se pudo eliminar");
        }
}