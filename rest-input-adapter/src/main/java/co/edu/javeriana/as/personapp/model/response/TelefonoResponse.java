package co.edu.javeriana.as.personapp.model.response;


/*Telefono como visto desde su propietario (agregacion persona -> telefonos)*/
public record TelefonoResponse(String number, String company, String ownerDni) {
}
