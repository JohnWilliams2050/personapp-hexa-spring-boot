package co.edu.javeriana.as.personapp.model.response;

/** Profesión sin sus estudios (se consultan aparte en /{id}/estudios). */
public record ProfesionResponse(String id, String name, String description) {
}
