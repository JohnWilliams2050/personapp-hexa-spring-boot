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
//en el dl de maria, por ejemplo la identificacion es cc, en el dominio es identification.
@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public class Person {
	@NonNull
	private Integer identification;
	@NonNull
	private String firstName;
	@NonNull
	private String lastName;
	@NonNull
	private Gender gender;
	private Integer age;
	@ToString.Exclude
	private List<Phone> phoneNumbers; //agregacion raiz
	@ToString.Exclude
	private List<Study> studies; //agregacion raiz

	public Boolean isValidAge() {
		return this.age >= 0;
	}
}
