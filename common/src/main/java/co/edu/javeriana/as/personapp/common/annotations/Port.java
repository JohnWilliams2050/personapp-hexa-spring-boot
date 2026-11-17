package co.edu.javeriana.as.personapp.common.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

//tecnicamente es un alias para @Component, pero nos permite abstraernos de Spring framework y no tener que cambiar el nombre de la anotacion si cambiamos de framework.
@Documented
@Retention(RUNTIME)
@Target(TYPE)
public @interface Port {

	String value() default "";
}
