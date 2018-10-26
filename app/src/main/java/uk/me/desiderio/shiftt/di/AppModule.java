package uk.me.desiderio.shiftt.di;

import dagger.Module;
import uk.me.desiderio.shiftt.viewmodel.ViewModelModule;

/**
 * Module to provided object at application level
 */
@Module(includes = {ViewModelModule.class})
public class AppModule {

}
