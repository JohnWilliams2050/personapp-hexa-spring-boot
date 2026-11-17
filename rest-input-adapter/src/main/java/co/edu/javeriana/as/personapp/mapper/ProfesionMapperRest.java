package co.edu.javeriana.as.personapp.mapper;

import java.util.ArrayList;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.model.request.ProfesionRequest;
import co.edu.javeriana.as.personapp.model.response.ProfesionResponse;

@Mapper
public class ProfesionMapperRest {

	public ProfesionResponse fromDomainToAdapterRest(Profession profession) {
		return new ProfesionResponse(profession.getIdentification() + "", profession.getName(),
				profession.getDescription());
	}

	// Valida aquí para que una profesión inválida nunca llegue al caso de uso (igual que PersonaMapperRest)
	public Profession fromAdapterToDomain(ProfesionRequest request) {
		Profession profession = new Profession();
		profession.setIdentification(validateId(request.getId()));
		profession.setName(validateName(request.getName()));
		profession.setDescription(request.getDescription() != null ? request.getDescription().trim() : "");
		profession.setStudies(new ArrayList<Study>());
		return profession;
	}

	private Integer validateId(String value) {
		if (value == null || value.isBlank())
			throw new IllegalArgumentException("id is required");
		try {
			return Integer.parseInt(value.trim());
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("id must be a number: " + value);
		}
	}

	private String validateName(String name) {
		if (name == null || name.isBlank())
			throw new IllegalArgumentException("name is required");
		return name.trim();
	}
}
