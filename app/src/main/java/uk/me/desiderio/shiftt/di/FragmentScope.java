package uk.me.desiderio.shiftt.di;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

/**
 * Defines fragment scope to be used with Dagger
 */

@Documented
@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface FragmentScope {
}
