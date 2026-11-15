package co.edu.javeriana.as.personapp.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstudioRequest {
        private String personDni;       // cc de la persona (parte 1 de la llave)
        private String professionId;    // id de la profesión (parte 2 de la llave)
        private String universityName;
        private String graduationDate;  // yyyy-MM-dd, opcional
        private String database;        // MARIA o MONGO
}