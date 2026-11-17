package co.edu.javeriana.as.personapp.terminal.mapper;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.terminal.model.TelefonoModelCli;

@Mapper
public class TelefonoMapperCli {

	public TelefonoModelCli fromDomainToAdapterCli(Phone phone) {
		return new TelefonoModelCli(
				phone.getNumber(),
				phone.getCompany(),
				phone.getOwner() != null ? phone.getOwner().getIdentification() : null);
	}

	public Phone fromAdapterCliToDomain(TelefonoModelCli telefonoModelCli) {
		// El dueño solo lleva el id: los adaptadores de salida únicamente usan el id (llave en Maria, referencia en Mongo)
		Person owner = new Person();
		owner.setIdentification(telefonoModelCli.getDuenio());
		Phone phone = new Phone();
		phone.setNumber(telefonoModelCli.getNum());
		phone.setCompany(telefonoModelCli.getOper());
		phone.setOwner(owner);
		return phone;
	}
}
