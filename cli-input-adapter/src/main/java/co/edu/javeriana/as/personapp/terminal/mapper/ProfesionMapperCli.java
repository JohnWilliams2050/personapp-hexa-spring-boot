package co.edu.javeriana.as.personapp.terminal.mapper;

import java.util.ArrayList;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.terminal.model.ProfesionModelCli;

@Mapper
public class ProfesionMapperCli {

	public ProfesionModelCli fromDomainToAdapterCli(Profession profession) {
		return new ProfesionModelCli(profession.getIdentification(), profession.getName(), profession.getDescription());
	}

	public Profession fromAdapterCliToDomain(ProfesionModelCli profesionModelCli) {
		Profession profession = new Profession();
		profession.setIdentification(profesionModelCli.getId());
		profession.setName(profesionModelCli.getNom());
		profession.setDescription(profesionModelCli.getDes() != null ? profesionModelCli.getDes() : "");
		profession.setStudies(new ArrayList<Study>());
		return profession;
	}
}
