package uk.me.desiderio.shiftt.di;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import androidx.lifecycle.ViewModel;
import dagger.MapKey;

/**
 * Defines MapKey annotation to bind {@link ViewModel} instance into a Map.
 */

@Documented
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@MapKey
public @interface ViewModelKey {
    Class<? extends ViewModel> value();
}
