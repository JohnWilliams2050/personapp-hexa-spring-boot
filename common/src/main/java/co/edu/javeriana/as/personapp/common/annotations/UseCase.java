package co.edu.javeriana.as.personapp.common.annotations;

import static java.lang.annotation.ElementType.TYPE;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

//tecnicamente es un alias para @Component, pero nos permite abstraernos de Spring framework y no tener que cambiar el nombre de la anotacion si cambiamos de framework.
@Documented
@Retention(RUNTIME)
@Target(TYPE)
@Component
public @interface UseCase {
	
	// Solo un alias para abstraernos de Spring framework
    @AliasFor(annotation = Component.class)
    String value() default "";

}
