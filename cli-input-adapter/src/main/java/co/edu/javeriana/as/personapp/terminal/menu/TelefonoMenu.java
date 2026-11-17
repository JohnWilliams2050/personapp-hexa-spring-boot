package co.edu.javeriana.as.personapp.terminal.menu;

import java.util.Scanner;

import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.terminal.adapter.TelefonoInputAdapterCli;
import co.edu.javeriana.as.personapp.terminal.model.TelefonoModelCli;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TelefonoMenu {

        private static final int OPCION_REGRESAR_MODULOS = 0;
        private static final int PERSISTENCIA_MARIADB = 1;
        private static final int PERSISTENCIA_MONGODB = 2;

        private static final int OPCION_REGRESAR_MOTOR_PERSISTENCIA = 0;
        private static final int OPCION_VER_TODO = 1;
        private static final int OPCION_BUSCAR = 2;
        private static final int OPCION_CONTAR = 3;
        private static final int OPCION_CREAR = 4;
        private static final int OPCION_EDITAR = 5;
        private static final int OPCION_ELIMINAR = 6;
        private static final int OPCION_VER_DUENIO = 7;

        public void iniciarMenu(TelefonoInputAdapterCli telefonoInputAdapterCli, Scanner keyboard) {
                boolean isValid = false;
                do {
                        try {
                                mostrarMenuMotorPersistencia();
                                int opcion = leerEntero(keyboard, "Ingrese una opción: ");
                                switch (opcion) {
                                case OPCION_REGRESAR_MODULOS:
                                        isValid = true;
                                        break;
                                case PERSISTENCIA_MARIADB:
                                        telefonoInputAdapterCli.setPhoneOutputPortInjection("MARIA");
                                        menuOpciones(telefonoInputAdapterCli, keyboard);
                                        break;
                                case PERSISTENCIA_MONGODB:
                                        telefonoInputAdapterCli.setPhoneOutputPortInjection("MONGO");
                                        menuOpciones(telefonoInputAdapterCli, keyboard);
                                        break;
                                default:
                                        log.warn("La opción elegida no es válida.");
                                }
                        } catch (InvalidOptionException e) {
                                log.warn(e.getMessage());
                        }
                } while (!isValid);
        }

        private void menuOpciones(TelefonoInputAdapterCli adapter, Scanner keyboard) {
                boolean isValid = false;
                do {
                        try {
                                mostrarMenuOpciones();
                                int opcion = leerEntero(keyboard, "Ingrese una opción: ");
                                switch (opcion) {
                                case OPCION_REGRESAR_MOTOR_PERSISTENCIA:
                                        isValid = true;
                                        break;
                                case OPCION_VER_TODO:
                                        adapter.historial();
                                        break;
                                case OPCION_BUSCAR:
                                        adapter.buscar(leerTextoObligatorio(keyboard, "Número del teléfono: "));
                                        break;
                                case OPCION_CONTAR:
                                        adapter.contar();
                                        break;
                                case OPCION_CREAR:
                                        adapter.crear(leerTelefono(keyboard));
                                        break;
                                case OPCION_EDITAR:
                                        adapter.editar(leerTelefono(keyboard));
                                        break;
                                case OPCION_ELIMINAR:
                                        adapter.eliminar(leerTextoObligatorio(keyboard, "Número del teléfono: "));
                                        break;
                                case OPCION_VER_DUENIO:
                                        adapter.duenio(leerTextoObligatorio(keyboard, "Número del teléfono: "));
                                        break;
                                default:
                                        log.warn("La opción elegida no es válida.");
                                }
                        } catch (Exception e) {
                                // NoExistException, AlreadyExistException o errores de la base de datos
                                // (por ejemplo, dueño inexistente)
                                log.warn(e.getMessage());
                        }
                } while (!isValid);
        }

        // Para crear y editar: el número identifica el teléfono y no cambia al editar (sí pueden cambiar operador y dueño)
        private TelefonoModelCli leerTelefono(Scanner keyboard) {
                String numero = leerTextoObligatorio(keyboard, "Número del teléfono (máx. 15 caracteres): ");
                String operador = leerTextoObligatorio(keyboard, "Operador: ");
                Integer duenio = leerEntero(keyboard, "Cédula del dueño: ");
                return new TelefonoModelCli(numero, operador, duenio);
        }

        private String leerTextoObligatorio(Scanner keyboard, String mensaje) {
                System.out.print(mensaje);
                while (true) {
                        String linea = keyboard.nextLine().trim();
                        if (!linea.isEmpty())
                                return linea;
                        log.warn("Este campo es obligatorio.");
                        System.out.print(mensaje);
                }
        }

        private Integer leerEntero(Scanner keyboard, String mensaje) {
                System.out.print(mensaje);
                while (true) {
                        String linea = keyboard.nextLine().trim();
                        if (linea.isEmpty())
                                continue; // descarta el salto de línea que dejó un nextInt() de otro menú
                        try {
                                return Integer.valueOf(linea);
                        } catch (NumberFormatException e) {
                                log.warn("Solo se permiten números.");
                                System.out.print(mensaje);
                        }
                }
        }

        private void mostrarMenuOpciones() {
                System.out.println("----------------------");
                System.out.println(OPCION_VER_TODO + " para ver todos los teléfonos");
                System.out.println(OPCION_BUSCAR + " para buscar un teléfono");
                System.out.println(OPCION_CONTAR + " para contar los teléfonos");
                System.out.println(OPCION_CREAR + " para crear un teléfono");
                System.out.println(OPCION_EDITAR + " para editar un teléfono");
                System.out.println(OPCION_ELIMINAR + " para eliminar un teléfono");
                System.out.println(OPCION_VER_DUENIO + " para ver el dueño de un teléfono");
                System.out.println(OPCION_REGRESAR_MOTOR_PERSISTENCIA + " para regresar");
        }

        private void mostrarMenuMotorPersistencia() {
                System.out.println("----------------------");
                System.out.println(PERSISTENCIA_MARIADB + " para MariaDB");
                System.out.println(PERSISTENCIA_MONGODB + " para MongoDB");
                System.out.println(OPCION_REGRESAR_MODULOS + " para regresar");
        }
}
