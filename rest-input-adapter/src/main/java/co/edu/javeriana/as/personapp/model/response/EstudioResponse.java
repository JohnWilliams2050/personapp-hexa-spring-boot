package co.edu.javeriana.as.personapp.model.response;

/**
 * Estudio visto desde su persona (agregacion persona -> estudios).
 */
public record EstudioResponse(String personDni, String professionId, String professionName,
		String universityName, String graduationDate) {
}
