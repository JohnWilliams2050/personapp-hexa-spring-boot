package co.edu.javeriana.as.personapp.domain;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

//Contiene la logica de negocio de la aplicacion. Es el lenguaje del dominio. No conoce ni el lenguaje de la infraestructura ni el de la aplicacion. Solo conoce el lenguaje del dominio.
//igual que los diagramas ER pero en puro java.

@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public class Profession {
	@NonNull
	private Integer identification;
	@NonNull
	private String name;
	private String description;
	@ToString.Exclude
	private List<Study> studies;
}
