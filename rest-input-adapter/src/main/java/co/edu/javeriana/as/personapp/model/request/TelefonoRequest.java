package co.edu.javeriana.as.personapp.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TelefonoRequest {
        private String number;     // llave del teléfono
        private String company;    // operador
        private String ownerDni;   // cc del dueño (debe existir)
        private String database;   // MARIA o MONGO
}
