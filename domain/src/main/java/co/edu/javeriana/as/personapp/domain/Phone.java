package co.edu.javeriana.as.personapp.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

//Contiene la logica de negocio de la aplicacion. Es el lenguaje del dominio. No conoce ni el lenguaje de la infraestructura ni el de la aplicacion. Solo conoce el lenguaje del dominio.
//igual que los diagramas ER pero en puro java.

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Phone {
	@NonNull
	private String number;
	@NonNull
	private String company;
	@NonNull
	private Person owner;
}
