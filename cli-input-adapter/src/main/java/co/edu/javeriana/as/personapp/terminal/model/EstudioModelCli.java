package co.edu.javeriana.as.personapp.terminal.model;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstudioModelCli {
	private Integer ccPer;
	private String persona;
	private Integer idProf;
	private String profesion;
	private String univer;
	private LocalDate fecha;
}
