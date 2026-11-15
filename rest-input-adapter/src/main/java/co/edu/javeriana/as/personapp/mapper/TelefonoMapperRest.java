package co.edu.javeriana.as.personapp.mapper;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.model.response.TelefonoResponse;

@Mapper
public class TelefonoMapperRest {

	public TelefonoResponse fromDomainToAdapterRest(Phone phone) {
		return new TelefonoResponse(
				phone.getNumber(),
				phone.getCompany(),
				phone.getOwner() != null ? phone.getOwner().getIdentification() + "" : null);
	}
}
