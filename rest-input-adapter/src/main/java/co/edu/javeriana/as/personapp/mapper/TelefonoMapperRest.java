package co.edu.javeriana.as.personapp.mapper;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.model.request.TelefonoRequest;
import co.edu.javeriana.as.personapp.model.response.TelefonoResponse;

@Mapper
public class TelefonoMapperRest {

	public TelefonoResponse fromDomainToAdapterRest(Phone phone) {
		return new TelefonoResponse(
				phone.getNumber(),
				phone.getCompany(),
				phone.getOwner() != null ? phone.getOwner().getIdentification() + "" : null);
	}

	// Valida aquí para que un teléfono inválido nunca llegue al caso de uso.
	// El dueño solo lleva el id: los adaptadores de salida solo usan el id (llave en Maria, referencia en Mongo).
	public Phone fromAdapterToDomain(TelefonoRequest request) {
		if (request.getNumber() == null || request.getNumber().isBlank())
			throw new IllegalArgumentException("number is required");
		if (request.getNumber().trim().length() > 15)
			throw new IllegalArgumentException("number must have at most 15 characters");
		if (request.getCompany() == null || request.getCompany().isBlank())
			throw new IllegalArgumentException("company is required");
		if (request.getOwnerDni() == null || request.getOwnerDni().isBlank())
			throw new IllegalArgumentException("ownerDni is required");
		Person owner = new Person();
		try {
			owner.setIdentification(Integer.parseInt(request.getOwnerDni().trim()));
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("ownerDni must be a number: " + request.getOwnerDni());
		}
		Phone phone = new Phone();
		phone.setNumber(request.getNumber().trim());
		phone.setCompany(request.getCompany().trim());
		phone.setOwner(owner);
		return phone;
	}
}
