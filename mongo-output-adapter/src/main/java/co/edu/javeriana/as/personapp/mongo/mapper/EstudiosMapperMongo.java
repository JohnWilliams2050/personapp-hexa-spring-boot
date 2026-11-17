package co.edu.javeriana.as.personapp.mongo.mapper;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.mongo.document.EstudiosDocument;
import co.edu.javeriana.as.personapp.mongo.document.PersonaDocument;
import co.edu.javeriana.as.personapp.mongo.document.ProfesionDocument;
import lombok.NonNull;

@Mapper
public class EstudiosMapperMongo {

	@Autowired
	private PersonaMapperMongo personaMapperMongo;

	@Autowired
	private ProfesionMapperMongo profesionMapperMongo;

	public EstudiosDocument fromDomainToAdapter(Study study) {
		EstudiosDocument estudio = new EstudiosDocument();
		estudio.setId(validateId(study.getPerson().getIdentification(), study.getProfession().getIdentification()));
		estudio.setPrimaryPersona(referenciaPersona(study.getPerson()));
		estudio.setPrimaryProfesion(referenciaProfesion(study.getProfession()));
		estudio.setFecha(validateFecha(study.getGraduationDate()));
		estudio.setUniver(validateUniver(study.getUniversityName()));
		return estudio;
	}

	private String validateId(@NonNull Integer identificationPerson, @NonNull Integer identificationProfession) {
		return identificationPerson + "-" + identificationProfession;
	}

	// Referencia mínima (solo id): @DocumentReference guarda únicamente el _id y se evita el ciclo
	private PersonaDocument referenciaPersona(Person person) {
			PersonaDocument doc = new PersonaDocument();
			doc.setId(person.getIdentification());
			return doc;
	}

	private ProfesionDocument referenciaProfesion(Profession profession) {
			ProfesionDocument doc = new ProfesionDocument();
			doc.setId(profession.getIdentification()); // supone ProfesionDocument.setId(Integer); falta confirmarlo
			return doc;
	}

	private LocalDate validateFecha(LocalDate graduationDate) {
		return graduationDate != null ? graduationDate : null;
	}

	private String validateUniver(String universityName) {
		return universityName != null ? universityName : "";
	}

	public Study fromAdapterToDomain(EstudiosDocument estudiosDocument) {
		Study study = new Study();
		study.setPerson(personaMapperMongo.fromAdapterToDomainWithoutRelations(estudiosDocument.getPrimaryPersona()));
		study.setProfession(profesionMapperMongo.fromAdapterToDomainWithoutRelations(estudiosDocument.getPrimaryProfesion()));
		study.setGraduationDate(validateGraduationDate(estudiosDocument.getFecha()));
		study.setUniversityName(validateUniversityName(estudiosDocument.getUniver()));
		return study;
	}

	private LocalDate validateGraduationDate(LocalDate fecha) {
		return fecha != null ? fecha : null;
	}

	private String validateUniversityName(String univer) {
		return univer != null ? univer : "";
	}
}
