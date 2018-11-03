package uk.me.desiderio.shiftt.di;

import android.app.Activity;
import android.content.Context;

import java.lang.annotation.Retention;

import javax.inject.Qualifier;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Dagger Qualifier to bind the {@link Activity} {@link Context}
 */

@Qualifier
@Retention(RUNTIME)
public @interface ForActivity {
}
