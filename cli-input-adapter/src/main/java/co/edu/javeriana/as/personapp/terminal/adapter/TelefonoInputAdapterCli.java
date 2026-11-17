package co.edu.javeriana.as.personapp.terminal.adapter;

import java.util.List;

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
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.terminal.mapper.PersonaMapperCli;
import co.edu.javeriana.as.personapp.terminal.mapper.TelefonoMapperCli;
import co.edu.javeriana.as.personapp.terminal.model.TelefonoModelCli;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Adapter
public class TelefonoInputAdapterCli {

        @Autowired
        @Qualifier("phoneOutputAdapterMaria")
        private PhoneOutputPort phoneOutputPortMaria;

        @Autowired
        @Qualifier("phoneOutputAdapterMongo")
        private PhoneOutputPort phoneOutputPortMongo;

        @Autowired
        private TelefonoMapperCli telefonoMapperCli;

        @Autowired
        private PersonaMapperCli personaMapperCli;

        // La CLI es de un solo usuario, así que la base elegida en el menú puede vivir en un campo
        PhoneInputPort phoneInputPort;

        // Elige el puerto de salida según la base de datos que escogió el usuario en el menú
        public void setPhoneOutputPortInjection(String dbOption) throws InvalidOptionException {
                if (dbOption.equalsIgnoreCase(DatabaseOption.MARIA.toString())) {
                        phoneInputPort = new PhoneUseCase(phoneOutputPortMaria);
                } else if (dbOption.equalsIgnoreCase(DatabaseOption.MONGO.toString())) {
                        phoneInputPort = new PhoneUseCase(phoneOutputPortMongo);
                } else {
                        throw new InvalidOptionException("Invalid database option: " + dbOption);
                }
        }

        // ---------- Read ----------

        public void historial() {
                log.info("Into historial TelefonoEntity in Input Adapter");
                List<Phone> telefonos = phoneInputPort.findAll();
                if (telefonos.isEmpty())
                        System.out.println("No hay teléfonos registrados");
                telefonos.stream().map(telefonoMapperCli::fromDomainToAdapterCli).forEach(System.out::println);
        }

        public void buscar(String numero) throws NoExistException {
                System.out.println(telefonoMapperCli.fromDomainToAdapterCli(phoneInputPort.findOne(numero)));
        }

        public void contar() {
                System.out.println("Total de teléfonos: " + phoneInputPort.count());
        }

        // ---------- Read: agregación (el dueño del teléfono) ----------

        public void duenio(String numero) throws NoExistException {
                System.out.println(personaMapperCli.fromDomainToAdapterCli(phoneInputPort.getOwner(numero)));
        }

        // ---------- Create / Update / Delete ----------

        public void crear(TelefonoModelCli telefono) throws AlreadyExistException {
                Phone creado = phoneInputPort.create(telefonoMapperCli.fromAdapterCliToDomain(telefono));
                System.out.println("Teléfono creado: " + telefonoMapperCli.fromDomainToAdapterCli(creado));
        }

        public void editar(TelefonoModelCli telefono) throws NoExistException {
                Phone editado = phoneInputPort.edit(telefono.getNum(), telefonoMapperCli.fromAdapterCliToDomain(telefono));
                System.out.println("Teléfono editado: " + telefonoMapperCli.fromDomainToAdapterCli(editado));
        }

        public void eliminar(String numero) throws NoExistException {
                System.out.println(phoneInputPort.drop(numero) ? "Teléfono " + numero + " eliminado"
                                : "El teléfono no se pudo eliminar");
        }
}
