package co.edu.javeriana.as.personapp.mapper;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.model.response.EstudioResponse;

@Mapper
public class EstudioMapperRest {

	public EstudioResponse fromDomainToAdapterRest(Study study) {
		return new EstudioResponse(
				study.getPerson().getIdentification() + "",
				study.getProfession().getIdentification() + "",
				study.getProfession().getName(),
				study.getUniversityName(),
				study.getGraduationDate() != null ? study.getGraduationDate().toString() : null);
	}
}
