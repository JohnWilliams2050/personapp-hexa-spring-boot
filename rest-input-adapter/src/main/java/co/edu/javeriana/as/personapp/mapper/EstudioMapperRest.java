package co.edu.javeriana.as.personapp.mapper;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.model.response.EstudioResponse;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.model.response.EstudioProfesionResponse;
import co.edu.javeriana.as.personapp.model.request.EstudioRequest;	
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@Mapper
public class EstudioMapperRest {

	public EstudioResponse fromDomainToAdapterRest(Study study) {
			return new EstudioResponse(
							study.getPerson().getIdentification() + "",
							study.getPerson().getFirstName() + " " + study.getPerson().getLastName(),
							study.getProfession().getIdentification() + "",
							study.getProfession().getName(),
							study.getUniversityName(),
							study.getGraduationDate() != null ? study.getGraduationDate().toString() : null);
	}

	public EstudioProfesionResponse fromDomainToAdapterRestProfesion(Profession profession) {
			return new EstudioProfesionResponse(profession.getIdentification() + "", profession.getName(),
							profession.getDescription());
	}

	// Igual que PersonaMapperRest: valida aquí para que un estudio inválido nunca llegue al caso de uso.
	// Person y Profession solo llevan el id: los adaptadores de salida solo usan el id (llave en Maria, referencia en Mongo).
	public Study fromAdapterToDomain(EstudioRequest request) {
			Person person = new Person();
			person.setIdentification(validateInteger(request.getPersonDni(), "personDni"));
			Profession profession = new Profession();
			profession.setIdentification(validateInteger(request.getProfessionId(), "professionId"));
			Study study = new Study();
			study.setPerson(person);
			study.setProfession(profession);
			study.setUniversityName(request.getUniversityName() != null ? request.getUniversityName().trim() : null);
			study.setGraduationDate(validateDate(request.getGraduationDate()));
			return study;
	}

	private Integer validateInteger(String value, String field) {
			if (value == null || value.isBlank())
					throw new IllegalArgumentException(field + " is required");
			try {
					return Integer.parseInt(value.trim());
			} catch (NumberFormatException e) {
					throw new IllegalArgumentException(field + " must be a number: " + value);
			}
	}

	private LocalDate validateDate(String date) {
			if (date == null || date.isBlank())
					return null;
			try {
					return LocalDate.parse(date.trim());
			} catch (DateTimeParseException e) {
					// DateTimeParseException no es IllegalArgumentException; se convierte para que el handler la trate igual
					throw new IllegalArgumentException("graduationDate must be yyyy-MM-dd: " + date);
			}
	}
}
