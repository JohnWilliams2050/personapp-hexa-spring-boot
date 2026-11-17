package co.edu.javeriana.as.personapp.mariadb.mapper;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.mariadb.entity.EstudiosEntity;
import co.edu.javeriana.as.personapp.mariadb.entity.EstudiosEntityPK;
import co.edu.javeriana.as.personapp.mariadb.entity.PersonaEntity;
import co.edu.javeriana.as.personapp.mariadb.entity.ProfesionEntity;

@Mapper
public class EstudiosMapperMaria {

	@Autowired
	private PersonaMapperMaria personaMapperMaria;

	@Autowired
	private ProfesionMapperMaria profesionMapperMaria;

	public EstudiosEntity fromDomainToAdapter(Study study) {
        EstudiosEntityPK estudioPK = new EstudiosEntityPK();
		estudioPK.setCcPer(study.getPerson().getIdentification());
		estudioPK.setIdProf(study.getProfession().getIdentification());
		EstudiosEntity estudio = new EstudiosEntity();
		estudio.setEstudiosPK(estudioPK);
		// Entidades mínimas (solo el id): basta para la FK y evita el ciclo
		// persona -> estudios -> persona -> ... (StackOverflowError)
		estudio.setPersona(new PersonaEntity(study.getPerson().getIdentification()));
		estudio.setProfesion(new ProfesionEntity(study.getProfession().getIdentification()));
		estudio.setFecha(validateFecha(study.getGraduationDate()));
		estudio.setUniver(validateUniver(study.getUniversityName()));
		return estudio;
	}

	private Date validateFecha(LocalDate graduationDate) {
		return graduationDate != null
				? Date.from(graduationDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())
				: null;
	}

	private String validateUniver(String universityName) {
		return universityName != null ? universityName : "";
	}

	public Study fromAdapterToDomain(EstudiosEntity estudiosEntity) {
        Study study = new Study();
		// WithoutRelations en ambos hijos: corta la recursión persona/profesión <-> estudios
		study.setPerson(personaMapperMaria.fromAdapterToDomainWithoutRelations(estudiosEntity.getPersona()));
		study.setProfession(profesionMapperMaria.fromAdapterToDomainWithoutRelations(estudiosEntity.getProfesion()));
		study.setGraduationDate(validateGraduationDate(estudiosEntity.getFecha()));
		study.setUniversityName(validateUniversityName(estudiosEntity.getUniver()));
		return study; // antes devolvía null: toda lectura devolvía null
	}

	private LocalDate validateGraduationDate(Date fecha) {
		return fecha != null ? Instant.ofEpochMilli(fecha.getTime()).atZone(ZoneId.systemDefault()).toLocalDate() : null;
	}

	private String validateUniversityName(String univer) {
		return univer != null ? univer : "";
	}
}