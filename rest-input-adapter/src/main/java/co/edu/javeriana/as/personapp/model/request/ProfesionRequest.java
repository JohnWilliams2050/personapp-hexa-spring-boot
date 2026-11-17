package co.edu.javeriana.as.personapp.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfesionRequest {
        private String id;           // id de la profesión
        private String name;
        private String description;  // opcional
        private String database;     // MARIA o MONGO
}
