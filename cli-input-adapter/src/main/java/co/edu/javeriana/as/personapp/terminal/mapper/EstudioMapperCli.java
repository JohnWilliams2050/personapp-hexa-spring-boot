package co.edu.javeriana.as.personapp.terminal.mapper;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.terminal.model.EstudioModelCli;

@Mapper
public class EstudioMapperCli {

	public EstudioModelCli fromDomainToAdapterCli(Study study) {
        return new EstudioModelCli(
            study.getPerson().getIdentification(),
            study.getPerson().getFirstName() + " " + study.getPerson().getLastName(),
            study.getProfession().getIdentification(),
            study.getProfession().getName(),                    
			study.getUniversityName(),                        
			study.getGraduationDate()
		);
	}


	public Study fromAdapterCliToDomain(EstudioModelCli estudioModelCli) {
        // Person y Profession solo con su id: los adaptadores de salida únicamente usan el id
        // para armar la llave (Maria) o la referencia (Mongo)
        Person person = new Person();
        person.setIdentification(estudioModelCli.getCcPer());
        Profession profession = new Profession();
        profession.setIdentification(estudioModelCli.getIdProf());
        Study study = new Study();
        study.setPerson(person);
        study.setProfession(profession);
        study.setGraduationDate(estudioModelCli.getFecha());
        study.setUniversityName(estudioModelCli.getUniver());
        return study;
}
}
